package project;

import java.util.List;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import module6.CommonMarker;
import processing.core.PGraphics;
import processing.core.PShape;

/** 
 * A class to represent AirportMarkers on a world map.
 *   
 * @author Alexandr Bulatov
 *
 */
public class AirportMarker extends CommonMarker {
	private String airportId;
	private PShape airplaneImage;

	private Location sourceLocation;

	private boolean route;

	public static List<SimpleLinesMarker> routes;
	
	public AirportMarker(Feature city, PShape airplaneImage) {
		super(((PointFeature)city).getLocation(), city.getProperties());
		this.airplaneImage = airplaneImage;
		this.location = ((PointFeature)city).getLocation();
		this.airportId = city.getId();
		this.route = false;
	}
	
	@Override
	public void drawMarker(PGraphics pg, float x, float y) {
		if (isRoute()) {
			pg.fill(255, 0, 0);
			pg.ellipse(x, y, 15, 15);
		} else {
			airplaneImage.setVisible(true);
			pg.shape(airplaneImage, x, y, 20, 20);
		}
	}

	/** Show the title of the earthquake if this marker is selected */
	@Override
	public void showTitle(PGraphics pg, float x, float y) {
		pg.fill(255, 255, 255);
		pg.rect(x+3, y-30, Math.max(pg.textWidth(this.getStringProperty("code")+"code:"), pg.textWidth(this.getStringProperty("name")))+10, 40);
		pg.fill(0, 0, 0);
		pg.text(this.getStringProperty("name")+"\n code:"+this.getStringProperty("code"), x+7, y-15);
	}

	public String getAirportId() {
		return this.airportId;
	}

	public boolean isRoute() {
		return this.route;
	}

	public void setRoute(boolean route) {
		this.route = route;
	}

	public void setSourceLocation(Location sourceLocation) {
		this.sourceLocation = sourceLocation;
	}

	public Location getMyLocation() {
		return this.location;
	}
}
