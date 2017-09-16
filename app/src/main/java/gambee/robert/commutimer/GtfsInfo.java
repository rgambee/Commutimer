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

public class GtfsInfo {
    private HashMap<String, ArrayList<String>> routesByMode = new HashMap<>();

    public GtfsInfo() {
        parseRoutes();
    }

    private HashMap<String, ArrayList<String>> parseRoutes() {
        File routesFile = new File(new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "RouteInfo"), "routes.xml");
        if (!routesFile.exists()) {
            downloadRoutes();
        }
        try {
            FileInputStream fis = new FileInputStream(routesFile);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(fis, null);
            parser.next();
            while (parser.next() != XmlPullParser.END_TAG) {
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
        return routesByMode;
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

    private void downloadRoutes() {
        return;
    }
}
