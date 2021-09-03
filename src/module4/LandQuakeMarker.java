package module4;

import de.fhpotsdam.unfolding.data.PointFeature;
import processing.core.PGraphics;

public class LandQuakeMarker extends EarthquakeMarker {
	
	public LandQuakeMarker(PointFeature quake) {
		
		// calling EarthquakeMarker constructor
		super(quake);
		
		// setting field in earthquake marker
		isOnLand = true;
	}

	@Override
	public void drawEarthquake(PGraphics pg, float x, float y) {
		pg.ellipse(x, y, this.radius*1.5f, this.radius*1.5f);
				
	}
	

	// Get the country the earthquake is in
	public String getCountry() {
		return (String) getProperty("country");
	}		
}