package gambee.robert.commutimer;

import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class GtfsInfo {
    private HashMap<String, ArrayList<String>> routesByMode = new HashMap<>();
    private HashMap<String, HashMap<String, ArrayList<String>>> stopsByRoute = new HashMap<>();

    public GtfsInfo() {
        parseRoutes();
    }

    public ArrayList<String> getRoutesForMode(String mode) {
        if (routesByMode.isEmpty()) {
            parseRoutes();
        }
        return routesByMode.get(mode);
    }

    public Set<String> getDirectionsForRoute(String route) {
        if (!stopsByRoute.containsKey(route)) {
            if (!parseStops(route)) {
                return null;
            }
        }
        return stopsByRoute.get(route).keySet();
    }

    public ArrayList<String> getStopsForRouteDirection(String route, String direction) {
        if (!stopsByRoute.containsKey(route)) {
            if (!parseStops(route)) {
                return null;
            }
        }
        return stopsByRoute.get(route).get(direction);
    }

    private void parseRoutes() {
        File routesFile = new File(new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "RouteInfo"), "routes.xml");
        if (!routesFile.exists()) {
            downloadRoutes();
        }
        try {
            FileInputStream fis = new FileInputStream(routesFile);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(fis, null);
            parser.nextTag();
            while (parser.nextTag() != XmlPullParser.END_TAG) {
                if (parser.getEventType() == XmlPullParser.START_TAG) {
                    String name = parser.getName();
                    if (name.equals("mode")) {
                        readMode(parser);
                    }
                }
            }
        } catch (IOException | XmlPullParserException ex) {
            Log.e("CommutimerError", ex.toString());
        }
    }

    private void readMode(XmlPullParser parser) throws IOException, XmlPullParserException {
        String modeName = parser.getAttributeValue(null, "mode_name");
        ArrayList<String> routes = new ArrayList<>();
        while (parser.nextTag() != XmlPullParser.END_TAG) {
            if (parser.getName().equals("route")) {
                routes.add(parser.getAttributeValue(null, "route_name"));
            }
            parser.nextTag();
        }
        if (routesByMode.containsKey(modeName)) {
            routesByMode.get(modeName).addAll(routes);
        } else {
            routesByMode.put(modeName, routes);
        }
    }

    private boolean parseStops(String route) {
        File stopsFile = new File(new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "RouteInfo"), route + ".xml");
        if (!stopsFile.exists()) {
            if (!downloadStops()) {
                return false;
            }
        }
        try {
            FileInputStream fis = new FileInputStream(stopsFile);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(fis, null);
            parser.nextTag();
            while (parser.nextTag() != XmlPullParser.END_TAG) {
                if (parser.getEventType() == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("direction")) {
                        String direction = parser.getAttributeValue(null, "direction_name");
                        ArrayList<String> stops = new ArrayList<>();
                        while (parser.nextTag() != XmlPullParser.END_TAG) {
                            stops.add(parser.getAttributeValue(null, "parent_station_name"));
                        }
                        if (stopsByRoute.containsKey(route)) {
                            stopsByRoute.get(route).put(direction, stops);
                        } else {
                            HashMap<String, ArrayList<String>> stopsForDirection = new HashMap<>();
                            stopsForDirection.put(direction, stops);
                            stopsByRoute.put(route, stopsForDirection);
                        }
                    }
                }
            }
        } catch (IOException | XmlPullParserException ex) {
            Log.e("CommutimerError", ex.toString());
            return false;
        }
        return true;
    }

    private boolean downloadRoutes() {
        return false;
    }

    private boolean downloadStops() {
        return false;
    }
}
