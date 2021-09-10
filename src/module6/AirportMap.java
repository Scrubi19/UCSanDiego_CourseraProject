package module6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.geo.Location;
import parsing.ParseFeed;
import processing.core.PApplet;
import processing.core.PShape;

/** An applet that shows airports (and routes)
 * on a world map.  
 * @author Alexander Bulatov
 */
public class AirportMap extends PApplet {

	UnfoldingMap map;
	private List<Marker> airportList;

	List<Marker> routeList;

	private AirportMarker lastSelectedAirport;
	private Marker lastSelectedCountry;

	private Marker lastClickedCountry;
	private AirportMarker lastClickedAirport;

	private List<Marker> countryMarkers;

	public void setup() {
		size(1920,1080);

		PShape airplaneImage = loadShape("airplane.svg");

		// setting up map and default events
		map = new UnfoldingMap(this, 50, 50, 1920, 1080);
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// get features from airport data
		List<PointFeature> features = ParseFeed.parseAirports(this, "airports.dat");
		
		// list for markers, hashmap for quicker access when matching with routes
		airportList = new ArrayList<>();
		HashMap<Integer, Location> airports = new HashMap<>();
		
		// create markers from features
		for(PointFeature feature : features) {
			AirportMarker m = new AirportMarker(feature, airplaneImage);

			m.setRadius(5);
			airportList.add(m);

//			System.out.println("id = "+Integer.parseInt(feature.getId()));
			
			// put airport in hashmap with OpenFlights unique id for key
			airports.put(Integer.parseInt(feature.getId()), feature.getLocation());
		
		}
		List<Feature> countries = GeoJSONReader.loadData(this, "countries.geo.json");
		countryMarkers = MapUtils.createSimpleMarkers(countries);

		// parse route data
		List<ShapeFeature> routes = ParseFeed.parseRoutes(this, "routes.dat");
		routeList = new ArrayList<>();
		for(ShapeFeature route : routes) {
			
			// get source and destination airportIds
			int source = Integer.parseInt((String)route.getProperty("source"));
			int dest = Integer.parseInt((String)route.getProperty("destination"));
			
			// get locations for airports on route
			if(airports.containsKey(source) && airports.containsKey(dest)) {
				route.addLocation(airports.get(source));
				route.addLocation(airports.get(dest));
			}
			
			SimpleLinesMarker sl = new SimpleLinesMarker(route.getLocations(), route.getProperties());

			routeList.add(sl);
		}
		map.addMarkers(countryMarkers);
	}

	@Override
	public void mouseMoved()  {
		// clear the last selection
		if (lastSelectedAirport != null) {
			lastSelectedAirport.setSelected(false);
			lastSelectedAirport = null;
		}
		selectMarkerIfHover(airportList);
	}

	private void selectMarkerIfHover(List<Marker> markers) {
		// Abort if there's already a marker selected
		if (lastSelectedAirport != null) {
			return;
		}
		for (Marker marker : markers) {
			if (marker.isInside(map, mouseX+40, mouseY+40)) {
				lastSelectedAirport = (AirportMarker) marker;
				marker.setSelected(true);
				return;
			}
		}
	}

	@Override
	public void mouseClicked() {
		map.addMarkers(airportList);
		setDefaultAirportMarker();
		if (lastClickedCountry != null) { // Country already clicked
			if (lastClickedAirport == null) {
				if (!lastClickedCountry.isInside(map, mouseX + 40, mouseY + 40)) {
					unhideCountries();
					hideAirports();
					lastClickedAirport = null;
					lastClickedCountry = null;
				} else {
					showAvailableRoutes();
				}
			} else {
				if (!lastClickedCountry.isHidden() && lastClickedCountry.isInside(map, mouseX + 40, mouseY + 40)) {
					showAvailableAirports();
					lastClickedAirport = null;
				} else if (!lastClickedCountry.isInside(map, mouseX + 40, mouseY + 40)) {
					unhideCountries();
					hideAirports();
					lastClickedAirport = null;
					lastClickedCountry = null;
				}
			}
		} else { // Country not clicked
			showAvailableAirports();
		}
	}

	public void showAvailableAirports() {
		for (Marker countryMarker : countryMarkers) {
			if (countryMarker.isInside(map, mouseX + 40, mouseY + 40)) { // for Ubuntu
				hideAirports();
				lastClickedCountry = countryMarker;
				for (Marker marker : countryMarkers) {
					marker.setHidden(true);
				}
				lastClickedCountry.setHidden(false);
				System.out.println(lastClickedCountry.getProperties());
				for (Marker airport : airportList) {
					if (lastClickedCountry.getStringProperty("name").equalsIgnoreCase(airport.getStringProperty("country").replaceAll("\"", ""))) {
						airport.setHidden(false);
					}
				}
			}
		}
	}

	public void showAvailableRoutes() {
		for (Marker airportMarker : airportList) {
			//if (!marker.isHidden() && marker.isInside(map, mouseX, mouseY)) {  // for Other System
			if (airportMarker.isInside(map, mouseX + 40, mouseY + 40)) { // for Ubuntu
				lastClickedAirport = (AirportMarker) airportMarker;

				for (Marker marker : airportList) {
					AirportMarker airplaneMarker = (AirportMarker) marker;
					airplaneMarker.setHidden(true);
					airplaneMarker.setRoute(false);
				}
				lastClickedAirport.setHidden(false);
				for (Marker route : routeList) {
					if (lastClickedAirport.getAirportId().equals(route.getStringProperty("source"))) {
						for (Marker showMarker : airportList) {
							AirportMarker availableRoute = (AirportMarker) showMarker;
							if (availableRoute.getAirportId().equals(route.getStringProperty("destination"))) {
								availableRoute.setRoute(true);
								availableRoute.setHidden(false);
								availableRoute.setSourceLocation(lastClickedAirport.getMyLocation());
							}
						}
					}
				}
			}
		}
	}

	private void unhideCountries() {
		for(Marker marker : countryMarkers) {
			marker.setHidden(false);
		}
	}

	private void hideAirports() {
		for(Marker marker : airportList) {
			marker.setHidden(true);
		}
	}

	private void setDefaultAirportMarker() {
		for(Marker marker : airportList) {
			AirportMarker airportMarker = (AirportMarker) marker;
			airportMarker.setRoute(false);
		}
	}
	
	public void draw() {
		background(0);
		map.draw();
	}
}
