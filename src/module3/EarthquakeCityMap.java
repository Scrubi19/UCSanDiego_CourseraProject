package module3;

import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
import java.util.List;

//Processing library
import processing.core.PApplet;

//Unfolding libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;

//Parsing library
import parsing.ParseFeed;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Alexander Bulatov
 * */

public class EarthquakeCityMap extends PApplet {

	private static final long serialVersionUID = 1L;

	private static final boolean offline = true;
	
	// Меньше этого порога - легкое землетрясение
	public static final float THRESHOLD_MODERATE = 5;
	// Меньше этого порога - небольшое землетрясение.
	public static final float THRESHOLD_LIGHT = 4;

	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "Dropbox/Courses/Coursera/Java UCSD/UnfoldingMaps_UCSDCourseraProject/data/blankLight-1-3.mbtiles";
	
	// The map
	private UnfoldingMap map;
	
	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";
	
	public void setup() {
		size(1920, 1080);

		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 700, 500, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom"; 	// Same feed, saved Aug 7, 2015, for working offline
		}
		else {
			map = new UnfoldingMap(this, 200, 50, 700, 500, new Google.GoogleTerrainProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
			//earthquakesURL = "2.5_week.atom";
		}
		
	    map.zoomToLevel(2);
	    MapUtils.createDefaultEventDispatcher(this, map);	
			
	    // The List you will populate with new SimplePointMarkers
	    List<Marker> markers = new ArrayList<Marker>();

	    //Use provided parser to collect properties for each earthquake
	    //PointFeatures have a getLocation method
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    
	    for (PointFeature point : earthquakes) {
	    	SimplePointMarker newMarker = createMarker(point);
	    	markers.add(newMarker);
	    }
	    	    
	    map.addMarkers(markers);
	}
		
	private SimplePointMarker createMarker(PointFeature feature) {  
		SimplePointMarker marker = new SimplePointMarker(feature.getLocation());
		
		Object magObj = feature.getProperty("magnitude");
		float mag = Float.parseFloat(magObj.toString());
		int markerColor = 0;
		
		if (mag > THRESHOLD_MODERATE) {
			marker.setColor(color(255, 0, 0)); // RED
			marker.setRadius(15);
		} else if (mag > THRESHOLD_LIGHT && mag < THRESHOLD_MODERATE) {
			marker.setColor(color(255, 255, 0)); // YELLOW
			marker.setRadius(10);
		} else if (mag < THRESHOLD_LIGHT) {
			marker.setColor(color(0, 0, 255)); // BLUE
			marker.setRadius(7);
		}
	    return marker;
	}
	
	 private void addKey()  { // Добавление легенды
		fill(255, 250, 240); //color white
		rect(25, 50, 160, 250); // (x location of upper left corner, y location of upper left corner, width, height)
		
		fill(0); //needed for text to appear, sets the color to fill shapes, takes in an int rgb value
		textAlign(LEFT, CENTER);
		textSize(12);
		text("Earthquake Key", 50, 75); //heading of key, takes (string, float x, and float y)
		//
		fill(color(255, 0, 0)); //red
		ellipse(50, 125, 15, 15); //(x coordinate, y coordinate, width, height)   )
		fill(color(255, 255, 0)); //yellow 
		ellipse(50, 175, 10, 10);
		fill(color(0, 0, 255));
		ellipse(50, 225, 7, 7);
		
		fill(0, 0, 0);
		text("5.0+ Magnitude", 75, 125);
		text("4.0+ Magnitude", 75, 175); // same y coordinate but different x so it could appear right beside marker
		text("Below 4.0", 75, 225);
	}
	
	public void draw() {
	    background(10);
	    map.draw();
	    addKey();
	}
}
