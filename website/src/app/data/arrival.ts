import {formatTime} from "../utility/utilities";

export class Arrival {

	constructor(
		readonly routeName: string,
		readonly headsign: string,
		readonly deviationMinutes: number,
		readonly deviationString: string,
		readonly absoluteTime: number,
		readonly absoluteTimeFormatted: string,
		readonly vehicleId: string,
		readonly vehicleDetails: string,
		private relativeTimeFormatted: string,
	) {
	}

	formatRelativeTime() {
		const currentMillis = Date.now();
		this.relativeTimeFormatted = formatTime(this.absoluteTime - currentMillis);
	}

	getRelativeTimeFormatted() {
		return this.relativeTimeFormatted;
	}
}
