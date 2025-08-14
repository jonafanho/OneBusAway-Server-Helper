import {formatRelativeTime} from "../utility/utilities";

export class Arrival {

	constructor(
		readonly routeName: string,
		readonly headsign: string,
		readonly deviationMinutes: number,
		readonly deviationString: string,
		private readonly absoluteTime: number,
		readonly absoluteTimeFormatted: string,
		private relativeTimeFormatted: string,
		readonly frequencies: Frequency[],
		readonly vehicleId: string,
		readonly vehicleDetails: string,
	) {
	}

	formatRelativeTime() {
		const currentMillis = Date.now();
		this.relativeTimeFormatted = formatRelativeTime(this.absoluteTime - currentMillis);
	}

	getRelativeTimeFormatted() {
		return this.relativeTimeFormatted;
	}
}

export class Frequency {
	readonly startTimeFormatted!: string;
	readonly endTimeFormatted!: string;
	readonly headwayMinutes!: number;
}
