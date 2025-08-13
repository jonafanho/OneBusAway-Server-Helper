export class StopExtension {
	readonly stop!: Stop;
	readonly direction?: number;
	readonly routes!: string[];
}

class Stop {
	readonly id!: StopId;
	readonly lat!: number;
	readonly lon!: number;
	readonly name!: string;
}

class StopId {
	readonly agencyId!: string;
	readonly id!: string;
}
