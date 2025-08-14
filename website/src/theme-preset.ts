import {definePreset} from "@primeng/themes";
import Aura from "@primeng/themes/aura";

export const myPreset = definePreset(Aura, {
	semantic: {
		primary: {
			50: "#081208",
			100: "#0F2410",
			200: "#1F4721",
			300: "#2E6B31",
			400: "#3E8E41",
			500: "#4CAF50",
			600: "#71C174",
			700: "#94D197",
			800: "#B8E0BA",
			900: "#DBF0DC",
			950: "#EDF7EE",
		},
	},
	components: {
		progressspinner: {
			colorScheme: {
				colorOne: "{neutral.500}",
				colorTwo: "{neutral.500}",
				colorThree: "{neutral.500}",
				colorFour: "{neutral.500}",
			},
		},
	},
});
