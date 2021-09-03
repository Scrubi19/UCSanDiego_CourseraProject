package module4;

import de.fhpotsdam.unfolding.data.PointFeature;
import processing.core.PGraphics;

public class OceanQuakeMarker extends EarthquakeMarker {
	
	public OceanQuakeMarker(PointFeature quake) {
		super(quake);
		
		// setting field in earthquake marker
		isOnLand = false;
	}
	

	@Override
	public void drawEarthquake(PGraphics pg, float x, float y) {
		pg.rect(x, y, this.radius*1.5f, this.radius*1.5f);		
	}
}
