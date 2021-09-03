package module1;

import processing.core.PApplet;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.AbstractMapProvider;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;

/** HelloWorld
  * An application with two maps side-by-side zoomed in on different locations.
  * Author: UC San Diego Coursera Intermediate Programming team
  * @author Alexander Bulatov
  * */
public class HelloWorld extends PApplet
{
	/** Your goal: add code to display second map, zoom in, and customize the background.
	 * Feel free to copy and use this code, adding to it, modifying it, etc.  
	 * Don't forget the import lines above. */

	private static final long serialVersionUID = 1L;

	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "Dropbox/Courses/Coursera/Java UCSD/UnfoldingMaps_UCSDCourseraProject/data/blankLight-1-3.mbtiles";
	
	// IF YOU ARE WORKING OFFLINE: Change the value of this variable to true
	private static final boolean offline = false;
	
	/** The map we use to display our home town: La Jolla, CA */
	UnfoldingMap map1;
	
	/** The map you will use to display your home town */ 
	UnfoldingMap map2;

	public void setup() {
		size(1920, 1080);  // Set up the Applet window to be 800x600
		                      // The OPENGL argument indicates to use the 
		                      // Processing library's 2D drawing
		                      // You'll learn more about processing in Module 3

		// This sets the background color for the Applet.  
		// Play around with these numbers and see what happens!
		this.background(200, 200, 200);
		
		// Select a map provider
		AbstractMapProvider provider = new Google.GoogleTerrainProvider();
		// Set a zoom level
		int zoomLevel = 10;
		
		if (offline) {
			// If you are working offline, you need to use this provider 
			// to work with the maps that are local on your computer.  
			provider = new MBTilesMapProvider(mbTilesString);
			// 3 is the maximum zoom level for working offline
			zoomLevel = 3;
		}

		map1 = new UnfoldingMap(this, 50, 50, 200, 300, provider);
		
		map2 = new UnfoldingMap(this, 250, 50, 200, 300, provider); //my home town

	    map1.zoomAndPanTo(zoomLevel, new Location(32.9f, -117.2f));
	    
	    map2.zoomAndPanTo(zoomLevel, new Location(54.6109607, 83.3844278));

		// This line makes the map interactive
		MapUtils.createDefaultEventDispatcher(this, map1, map2);
	}
	/** Draw the Applet window.  */
	public void draw() {
		map1.draw();
		map2.draw();
	}
}
