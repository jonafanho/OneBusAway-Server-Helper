export function getCookie(name: string, defaultValue = "") {
	const splitCookies = document.cookie.split("; ").filter(cookie => cookie.startsWith(name + "="));
	if (splitCookies.length > 0 && splitCookies[0].includes("=")) {
		return decodeURIComponent(splitCookies[0].split("=")[1]);
	} else {
		return defaultValue;
	}
}

export function setCookie(name: string, value: string) {
	document.cookie = `${name}=${value}; expires=${new Date(2999, 11, 31).toUTCString()}; path=/`;
}

export function formatTime(millis: number) {
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
