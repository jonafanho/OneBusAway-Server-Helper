import {Component} from "@angular/core";
import {MapComponent} from "./component/map/map";
import {DrawerComponent} from "./component/drawer/drawer";

@Component({
	selector: "app-root",
	imports: [
		MapComponent,
		DrawerComponent,
	],
	templateUrl: "./app.component.html",
	styleUrls: ["./app.component.scss"],
})
export class AppComponent {
}
