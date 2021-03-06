package gambee.robert.commutimer;

import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class GtfsInfo {
    private HashMap<String, ArrayList<String>> routesByMode = new HashMap<>();
    private HashMap<String, String> routeNamesToIds = new HashMap<>();
    private HashMap<String, String> routeIdsToNames = new HashMap<>();
    private HashMap<String, HashMap<String, ArrayList<String>>> stopsByRoute = new HashMap<>();
    private RequestQueue requestQ;
    private final String MBTA_API_URL = ("http://realtime.mbta.com/developer/api/v2/" + "%s"
                                         + "?api_key=%s" + "&format=%s" + "%s");
    private final String MBTA_API_KEY = "Ut2vwfYEi0OKShX3x5AFyw";
    private final String MBTA_API_FORMAT = "xml";

    public GtfsInfo(RequestQueue rq) {
        requestQ = rq;
        parseRoutes();
    }

    public ArrayList<String> getRoutesForMode(String mode) {
        if (routesByMode.isEmpty()) {
            parseRoutes();
        }
        return routesByMode.get(mode);
    }

    public ArrayList<String> getDirectionsForRoute(String routeName) {
        String routeId = routeNamesToIds.get(routeName);
        if (!stopsByRoute.containsKey(routeId)) {
            if (!parseStops(routeId)) {
                return null;
            }
        }
        return new ArrayList<>(stopsByRoute.get(routeId).keySet());
    }

    public ArrayList<String> getStopsForRouteDirection(String routeName, String direction) {
        String routeId = routeNamesToIds.get(routeName);
        if (!stopsByRoute.containsKey(routeId)) {
            if (!parseStops(routeId)) {
                return null;
            }
        }
        return stopsByRoute.get(routeId).get(direction);
    }

    private boolean parseRoutes() {
        File routesFile = new File(new File(new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS),
                "Commutimer"),
                "RouteInfo"),
                "routes.xml");
        if (!routesFile.exists()) {
            if (!downloadRoutes()) {
                return false;
            }
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
            return false;
        }
        return true;
    }

    private void readMode(XmlPullParser parser) throws IOException, XmlPullParserException {
        String modeName = parser.getAttributeValue(null, "mode_name");
        ArrayList<String> routes = new ArrayList<>();
        while (parser.nextTag() != XmlPullParser.END_TAG) {
            if (parser.getName().equals("route")) {
                String routeId = parser.getAttributeValue(null, "route_id");
                String routeName = parser.getAttributeValue(null, "route_name");
                routes.add(routeName);
                routeIdsToNames.put(routeId, routeName);
                routeNamesToIds.put(routeName, routeId);
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
        File stopsFile = new File(new File(new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS),
                "Commutimer"),
                "RouteInfo"),
                route + ".xml");
        if (!stopsFile.exists()) {
            if (!downloadStops(route)) {
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
                            String stopName = parser.getAttributeValue(null, "parent_station_name");
                            if (stopName.equals("")) {
                                stopName = parser.getAttributeValue(null, "stop_name");
                            }
                            stops.add(stopName);
                            parser.nextTag();
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

    private String formatUrl(String query) {
        return formatUrl(query, "");
    }

    private String formatUrl(String query, String parameters) {
        if (!parameters.equals("")) {
            parameters = "&" + parameters;
        }
        return String.format(MBTA_API_URL, query, MBTA_API_KEY, MBTA_API_FORMAT, parameters);
    }

    private boolean downloadRoutes() {
        String url = formatUrl("routes");
        String response = dowloadGtfsData(url);
        return response != null && saveToFile(
            response, "routes.xml");
    }

    private boolean downloadStops(String routeId) {
        String url = formatUrl("stopsbyroute", "route=" + routeId);
        String response = dowloadGtfsData(url);
        return response != null && saveToFile(
            response, routeId + ".xml");
    }

    private String dowloadGtfsData(String url) {
        Log.d("CommutimerDebug", url);
        RequestFuture<String> future = RequestFuture.newFuture();
        StringRequest routesRequest = new StringRequest(
                Request.Method.GET, url, future, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("CommutimerError", error.toString());
            }
        });
        requestQ.add(routesRequest);
        try {
            return future.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
            Log.e("CommutimerError", ex.toString());
            return null;
        }
    }

    private boolean saveToFile(String data, String fileName) {
        File file = new File(new File(new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS),
                "Commutimer"),
                "RouteInfo"),
                fileName);
        file.getParentFile().mkdirs();
        try {
            FileOutputStream fos = new FileOutputStream(file);
            Log.d("CommutimerDebug", "Saving to " + fileName);
            try {
                fos.write(data.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                fos.write(data.getBytes());
            }
            fos.close();
        } catch (IOException ex) {
            Log.e("CommutimerError", ex.toString());
            return false;
        }
        return true;
    }
}
