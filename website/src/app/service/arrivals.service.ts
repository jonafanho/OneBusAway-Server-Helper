import {EventEmitter, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {StopExtension} from "../data/stopExtension";
import {url} from "../utility/settings";
import {Arrival} from "../data/arrival";
import {formatAbsoluteTime, formatRelativeTime} from "../utility/utilities";
import {MILLIS_PER_MINUTE} from "../utility/constants";

const fetchInterval = 3000;
const defaultMinutesAfter = 120;

@Injectable({providedIn: "root"})
export class ArrivalsService {
	public readonly stopClicked = new EventEmitter<StopExtension | undefined>();
	private arrivals: Arrival[] = [];
	private minutesAfter = defaultMinutesAfter;
	private loading = false;
	private addMinutesAfterLoading = false;
	private stopExtension?: StopExtension;
	private intervalId = -1;

	constructor(private readonly httpClient: HttpClient) {
		this.stopClicked.subscribe(stopExtension => {
			this.stopExtension = stopExtension;
			this.arrivals = [];
			this.minutesAfter = defaultMinutesAfter;
			this.loading = !!this.stopExtension;
			this.addMinutesAfterLoading = false;
			this.fetchData();
		});

		setInterval(() => this.arrivals.forEach(arrival => arrival.formatRelativeTime()), 100);
	}

	getName() {
		return this.stopExtension?.stop?.name ?? "";
	}

	getRoutes() {
		return this.stopExtension?.routes ?? [];
	}

	getId() {
		return this.stopExtension ? `${this.stopExtension.stop.id.agencyId}_${this.stopExtension.stop.id.id}` : "";
	}

	getArrivals() {
		return this.arrivals;
	}

	getLoading() {
		return this.loading;
	}

	getAddMinutesAfterLoading() {
		return this.addMinutesAfterLoading;
	}

	addMinutesAfter() {
		this.minutesAfter += defaultMinutesAfter;
		this.loading = false;
		this.addMinutesAfterLoading = !!this.stopExtension;
		this.fetchData();
	}

	getMinutesAfter() {
		return this.minutesAfter;
	}

	private fetchData() {
		const stopId = this.getId();
		if (stopId) {
			clearTimeout(this.intervalId);
			this.httpClient.get<{
				data: {
					list: {
						arrivalTime?: number,
						departureTime?: number,
						isTerminating: boolean,
						routeShortName?: string,
						routeLongName?: string,
						stopHeadsign?: string,
						tripHeadsign?: string,
						lastStopName?: string,
						isTimepoint: boolean,
						frequencies?: { startTime: number, endTime: number, headway: number }[],
						vehicleId?: string,
						year?: string,
						make?: string,
						model?: string,
						fuel?: string,
						deviation?: number,
						isAnomaly?: boolean,
						length?: number,
					}[]
				}
			}>(`${url}api/arrivals-and-departures-for-stop?stopId=${stopId}&minutesAfter=${this.minutesAfter}`).subscribe({
				next: ({data}) => {
					if (this.getId() === stopId) {
						const currentMillis = Date.now();
						this.arrivals = data.list.map(arrivalAndDeparture => {
							const actualDeviation = arrivalAndDeparture.deviation ?? 0;
							const deviationMinutes = Math.round(Math.abs(actualDeviation) / MILLIS_PER_MINUTE);
							const absoluteTime = arrivalAndDeparture.arrivalTime === undefined ? undefined : arrivalAndDeparture.arrivalTime > currentMillis ? arrivalAndDeparture.arrivalTime : arrivalAndDeparture.departureTime;
							return new Arrival(
								arrivalAndDeparture.routeShortName ?? arrivalAndDeparture.routeLongName ?? "",
								arrivalAndDeparture.stopHeadsign ?? arrivalAndDeparture.tripHeadsign ?? arrivalAndDeparture.lastStopName ?? "",
								deviationMinutes,
								arrivalAndDeparture.deviation === undefined ? "scheduled" : deviationMinutes > 0 ? actualDeviation > 0 ? "delay" : "early" : "on-time",
								absoluteTime ?? 0,
								absoluteTime ? formatAbsoluteTime(absoluteTime) : "",
								absoluteTime ? formatRelativeTime(absoluteTime - currentMillis) : "",
								arrivalAndDeparture.frequencies ? arrivalAndDeparture.frequencies.map(frequency => ({
									startTimeFormatted: formatAbsoluteTime(frequency.startTime),
									endTimeFormatted: formatAbsoluteTime(frequency.endTime),
									headwayMinutes: Math.floor(frequency.headway / MILLIS_PER_MINUTE),
								})) : [],
								arrivalAndDeparture.vehicleId ?? "",
								`${arrivalAndDeparture.make ?? ""} ${arrivalAndDeparture.model ?? ""} ${arrivalAndDeparture.length ? `(${arrivalAndDeparture.length} m)` : ""}`,
							);
						});
						this.loading = false;
						this.addMinutesAfterLoading = false;
						clearTimeout(this.intervalId);
						this.intervalId = setTimeout(() => this.fetchData(), fetchInterval) as unknown as number;
					} else {
						console.warn("Skipping request", stopId);
					}
				},
				error: () => {
					if (this.getId() === stopId) {
						this.loading = false;
						this.addMinutesAfterLoading = false;
						clearTimeout(this.intervalId);
						this.intervalId = setTimeout(() => this.fetchData(), fetchInterval) as unknown as number;
					}
				},
			});
		}
	}
}
