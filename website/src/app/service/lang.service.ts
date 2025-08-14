import {Injectable} from "@angular/core";
import {getCookie} from "../utility/utilities";
import {TranslocoService} from "@jsverse/transloco";

@Injectable({providedIn: "root"})
export class LangService {

	constructor(private readonly translocoService: TranslocoService) {
		const lang = getCookie("lang");
		if (translocoService.getAvailableLangs().some(langDefinition => langDefinition.toString() === lang)) {
			translocoService.setActiveLang(lang);
		}
	}

	init() {
		console.log("Started the language service with these languages:", this.translocoService.getAvailableLangs());
	}
}
