import {Component} from "@angular/core";
import {DrawerModule} from "primeng/drawer";
import {ArrivalsAndDeparturesService} from "../../service/arrivals-and-departures.service";
import {DividerModule} from "primeng/divider";

@Component({
	selector: "app-drawer",
	imports: [
		DrawerModule,
		DividerModule,
	],
	templateUrl: "./drawer.html",
	styleUrl: "./drawer.scss",
})
export class DrawerComponent {
	protected visible = false;

	constructor(private readonly arrivalsAndDeparturesService: ArrivalsAndDeparturesService) {
		arrivalsAndDeparturesService.stopClicked.subscribe(stopExtension => this.visible = !!stopExtension);
	}

	closeDrawer() {
		this.arrivalsAndDeparturesService.stopClicked.emit();
	}

	getName() {
		return this.arrivalsAndDeparturesService.getName();
	}

	getRouteList() {
		return this.arrivalsAndDeparturesService.getRoutes().join(", ");
	}

	getId() {
		return this.arrivalsAndDeparturesService.getId();
	}

	getFormattedDepartures() {
		return this.arrivalsAndDeparturesService.getFormattedArrivalsAndDepartures();
	}
}
