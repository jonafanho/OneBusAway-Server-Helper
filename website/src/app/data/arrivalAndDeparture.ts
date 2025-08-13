export class ArrivalAndDeparture {
	readonly arrivalTime!: number;
	readonly departureTime!: number;
	readonly isTerminating!: boolean;
	readonly routeShortName?: string;
	readonly routeLongName?: string;
	readonly stopHeadsign?: string;
	readonly tripHeadsign?: string;
	readonly isTimepoint!: boolean;
	readonly vehicleId?: string;
	readonly year?: string;
	readonly make?: string;
	readonly model?: string;
	readonly fuel?: string;
	readonly deviation?: number;
	readonly isAnomaly?: boolean;
	readonly length?: number;
}
