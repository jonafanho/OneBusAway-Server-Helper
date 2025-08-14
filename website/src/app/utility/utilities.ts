import {MILLIS_PER_DAY, MILLIS_PER_HOUR, MILLIS_PER_MINUTE, MILLIS_PER_SECOND, MINUTES_PER_HOUR, SECONDS_PER_MINUTE} from "./constants";

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

export function formatAbsoluteTime(millis: number) {
	const date = new Date(millis);
	return `${millis - Date.now() >= MILLIS_PER_DAY ? date.toLocaleDateString() : ""} ${date.toLocaleTimeString()}`;
}

export function formatRelativeTime(millis: number) {
	if (millis <= 0) {
		return "";
	} else {
		const seconds = Math.floor(millis / MILLIS_PER_SECOND) % SECONDS_PER_MINUTE;
		const minutes = Math.floor(millis / MILLIS_PER_MINUTE) % MINUTES_PER_HOUR;
		const hours = Math.floor(millis / MILLIS_PER_HOUR);
		const paddedMinutes = String(minutes).padStart(2, "0");
		const paddedSeconds = String(seconds).padStart(2, "0");
		return hours > 0 ? `${hours}:${paddedMinutes}:${paddedSeconds}` : `${minutes}:${paddedSeconds}`;
	}
}
