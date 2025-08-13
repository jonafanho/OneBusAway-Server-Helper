import * as Leaflet from "leaflet";

const directionIncrement = Math.PI / 4;
const iconScale = 0.3;
const iconSize = 108 * iconScale;
const iconBorderSize = 6 * iconScale;
const iconCircleRadius = 28.5 * iconScale;
const iconShadowRadius = 3 * iconScale;
const iconArrowPoints = [[102.4, 54], [80.4, 37.5], [85.9, 54], [80.4, 70.5]].map(point => point.map(position => position * iconScale).join(",")).join(" ");

export function createIcon(color: string, direction?: number) {
	const hasDirection = direction !== undefined;
	const normalizedDirection = hasDirection ? normalizeDirection(direction) : 0;
	return Leaflet.divIcon({
		className: "map-icon-wrapper",
		iconSize: [iconSize, iconSize],
		iconAnchor: [iconSize / 2, iconSize / 2],
		html: `
			<svg width="${iconSize}" height="${iconSize}" style="transform: rotate(${hasDirection ? -directionIncrement * normalizedDirection : 0}rad); fill: ${color}; filter: drop-shadow(0 0 ${iconShadowRadius}px rgba(0, 0, 0, 0.25))">
				${hasDirection ? `<polygon points="${iconArrowPoints}"/>` : ""}
				<circle style="stroke: white; stroke-width: ${iconBorderSize}" cx="${iconSize / 2}" cy="${iconSize / 2}" r="${iconCircleRadius - iconBorderSize / 2}"/>
			</svg>
		`,
	});
}

function normalizeDirection(direction: number) {
	let quadrant = Math.round(direction / directionIncrement);
	while (quadrant < 0) {
		quadrant += 8;
	}
	return quadrant;
}
