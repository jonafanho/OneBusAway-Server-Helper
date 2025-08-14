import {Component} from "@angular/core";
import {DrawerModule} from "primeng/drawer";
import {ArrivalsService} from "../../service/arrivals.service";
import {DividerModule} from "primeng/divider";
import {TranslocoDirective} from "@jsverse/transloco";
import {ProgressBarModule} from "primeng/progressbar";
import {ButtonModule} from "primeng/button";

@Component({
	selector: "app-drawer",
	imports: [
		DrawerModule,
		DividerModule,
		ProgressBarModule,
		ButtonModule,
		TranslocoDirective,
	],
	templateUrl: "./drawer.html",
	styleUrl: "./drawer.scss",
})
export class DrawerComponent {
	protected visible = false;

	constructor(private readonly arrivalsService: ArrivalsService) {
		arrivalsService.stopClicked.subscribe(stopExtension => this.visible = !!stopExtension);
	}

	closeDrawer() {
		this.arrivalsService.stopClicked.emit();
	}

	getName() {
		return this.arrivalsService.getName();
	}

	getRouteList() {
		return this.arrivalsService.getRoutes().join(", ");
	}

	getId() {
		return this.arrivalsService.getId();
	}

	getArrivals() {
		return this.arrivalsService.getArrivals();
	}

	getLoading() {
		return this.arrivalsService.getLoading();
	}

	getAddMinutesAfterLoading() {
		return this.arrivalsService.getAddMinutesAfterLoading();
	}

	addMinutesAfter() {
		this.arrivalsService.addMinutesAfter();
	}

	getHoursAfter() {
		return Math.floor(this.arrivalsService.getMinutesAfter() / 60);
	}
}
