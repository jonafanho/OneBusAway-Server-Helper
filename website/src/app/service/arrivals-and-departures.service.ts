import {EventEmitter, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {StopExtension} from "../data/stopExtension";
import {url} from "../utility/settings";
import {ArrivalAndDeparture} from "../data/arrivalAndDeparture";

@Injectable({providedIn: "root"})
export class ArrivalsAndDeparturesService {
	public readonly stopClicked = new EventEmitter<StopExtension | undefined>();
	private formattedArrivalsAndDepartures: {
		routeName: string,
		headsign: string,
		deviationString: string,
		absoluteTime: number,
		absoluteTimeFormatted: string,
		relativeTimeFormatted: string,
		vehicleId: string,
		vehicleDetails: string,
	}[] = [];
	private stopExtension?: StopExtension;
	private intervalId = -1;

	constructor(httpClient: HttpClient) {
		this.stopClicked.subscribe(stopExtension => {
			this.stopExtension = stopExtension;
			this.formattedArrivalsAndDepartures = [];
			if (stopExtension) {
				const fetchData = () => {
					clearTimeout(this.intervalId);
					httpClient.get<{ data: { list: ArrivalAndDeparture[] } }>(`${url}/api/arrivals-and-departures-for-stop?stopId=${stopExtension.stop.id.agencyId}_${stopExtension.stop.id.id}`).subscribe({
						next: ({data}) => {
							const currentMillis = Date.now();
							this.formattedArrivalsAndDepartures = data.list.map(arrivalAndDeparture => {
								const hasDeviation = arrivalAndDeparture.deviation !== undefined;
								const actualDeviation = hasDeviation ? arrivalAndDeparture.deviation : 0;
								const deviationMinutes = Math.round(Math.abs(actualDeviation) / 60000);
								const time = arrivalAndDeparture.arrivalTime > currentMillis ? arrivalAndDeparture.arrivalTime : arrivalAndDeparture.departureTime;
								return {
									routeName: arrivalAndDeparture.routeShortName ?? arrivalAndDeparture.routeLongName ?? "",
									headsign: arrivalAndDeparture.stopHeadsign ?? arrivalAndDeparture.tripHeadsign ?? "",
									deviationString: hasDeviation ? deviationMinutes > 0 ? `${deviationMinutes} min ${actualDeviation > 0 ? "delay" : "early"}` : "On time" : "Scheduled",
									absoluteTime: time,
									absoluteTimeFormatted: new Date(time).toLocaleTimeString(),
									relativeTimeFormatted: ArrivalsAndDeparturesService.formatTime(time - currentMillis),
									vehicleId: arrivalAndDeparture.vehicleId ?? "",
									vehicleDetails: `${arrivalAndDeparture.make ?? ""} ${arrivalAndDeparture.model ?? ""} ${arrivalAndDeparture.length ? `(${arrivalAndDeparture.length} m)` : ""}`,
								};
							});
							this.intervalId = setTimeout(() => fetchData(), 3000) as unknown as number;
						},
						error: () => this.intervalId = setTimeout(() => fetchData(), 3000) as unknown as number,
					});
				};
				fetchData();
			}
		});

		setInterval(() => {
			const currentMillis = Date.now();
			this.formattedArrivalsAndDepartures.forEach(formattedArrivalAndDeparture => formattedArrivalAndDeparture.relativeTimeFormatted = ArrivalsAndDeparturesService.formatTime(formattedArrivalAndDeparture.absoluteTime - currentMillis));
		}, 100);
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

	getFormattedArrivalsAndDepartures() {
		return this.formattedArrivalsAndDepartures;
	}

	private static formatTime(millis: number) {
		if (millis <= 0) {
			return "";
		} else {
			const totalSeconds = Math.floor(millis / 1000);
			const seconds = totalSeconds % 60;
			const minutes = Math.floor(totalSeconds / 60) % 60;
			const hours = Math.floor(totalSeconds / 3600);
			const paddedMinutes = String(minutes).padStart(2, "0");
			const paddedSeconds = String(seconds).padStart(2, "0");
			return hours > 0 ? `${hours}:${paddedMinutes}:${paddedSeconds}` : `${minutes}:${paddedSeconds}`;
		}
	}
}
