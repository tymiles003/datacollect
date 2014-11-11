package org.smap.smapTask.android.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mapbox.mapboxsdk.api.ILatLng;
import com.mapbox.mapboxsdk.geometry.BoundingBox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Icon;
import com.mapbox.mapboxsdk.overlay.ItemizedIconOverlay;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.overlay.PathOverlay;
import com.mapbox.mapboxsdk.overlay.UserLocationOverlay;
import com.mapbox.mapboxsdk.tileprovider.tilesource.ITileLayer;
import com.mapbox.mapboxsdk.tileprovider.tilesource.MBTilesLayer;
import com.mapbox.mapboxsdk.tileprovider.tilesource.MapboxTileLayer;
import com.mapbox.mapboxsdk.tileprovider.tilesource.TileLayer;
import com.mapbox.mapboxsdk.tileprovider.tilesource.WebSourceTileLayer;
import com.mapbox.mapboxsdk.views.MapView;
import com.mapbox.mapboxsdk.views.util.TilesLoadedListener;

import org.smap.smapTask.android.R;

import java.util.ArrayList;
import java.util.List;

import loaders.SmapTaskLoader;
import loaders.TaskEntry;

public class MapFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<TaskEntry>> {

    ItemizedIconOverlay markerOverlay = null;
    ArrayList<Marker> markers = null;
    private double tasksNorth;
    private double tasksSouth;
    private double tasksEast;
    private double tasksWest;
    private LatLng userLocation = null;

    private MapView mv;
    private String satellite = "brunosan.map_fragment-cyglrrfu";
    private String street = "examples.map_fragment-i87786ca";
    private final String mbTile = "test.MBTiles";
    private String currentLayer = "";
    private static final int MAP_LOADER_ID = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.map_fragment, container, false);

        mv = (MapView) view.findViewById(R.id.mapview);
        // Set Default Map Type
        replaceMapView("mapquest");
        currentLayer = "terrain";

        mv.setUserLocationEnabled(true)
            .setUserLocationTrackingMode(UserLocationOverlay.TrackingMode.FOLLOW);

        getLoaderManager().initLoader(MAP_LOADER_ID, null, this);       // Get the task locations

        /*
        // Original GeoJSON Test that caus es crash when Hardware Acceleration when enabled in TestApp
        mv.loadFromGeoJSONURL("https://gist.githubusercontent.com/tmcw/4a6f5fa40ab9a6b2f163/raw/b1ee1e445225fc0a397e2605feda7da74c36161b/map_fragment.geojson");
        */
        // Smaller GeoJSON Test

        mv.setOnTilesLoadedListener(new TilesLoadedListener() {
            @Override
            public boolean onTilesLoaded() {
                return false;
            }

        @Override
        public boolean onTilesLoadStarted() {
            // TODO Auto-generated method stub
            return false;
        }
        });
        mv.setVisibility(View.VISIBLE);

        return view;
    }

    @Override
    public void onLoadFinished(Loader<List<TaskEntry>> loader, List<TaskEntry> data) {
        Log.i("MapFragment", "============ onLoadFinished");
        showTasks(data);
    }

    @Override
    public void onLoaderReset(Loader<List<TaskEntry>> loader) {
        Log.i("MapFragment", "============ onLoaderReset");
        clearTasks();
    }

    @Override
    public Loader<List<TaskEntry>> onCreateLoader(int id, Bundle args) {
        return new SmapTaskLoader(getActivity());
    }

    final String[] availableLayers = {
        "OpenStreetMap", "OpenSeaMap", "mapquest", "open-streets-dc.mbtiles", "test.MBTiles"
    };

    protected void replaceMapView(String layer) {
        ITileLayer source;
        BoundingBox box;
        if (layer.toLowerCase().endsWith("mbtiles")) {
            TileLayer mbTileLayer = new MBTilesLayer(getActivity(), layer);
            //            mv.setTileSource(mbTileLayer);
            mv.setTileSource(new ITileLayer[] {
                mbTileLayer, new WebSourceTileLayer("mapquest",
                    "http://otile1.mqcdn.com/tiles/1.0.0/osm/{z}/{x}/{y}.png").setName(
                        "MapQuest Open Aerial")
                        .setAttribution("Tiles courtesy of MapQuest and OpenStreetMap contributors.")
                .setMinimumZoomLevel(1)
                .setMaximumZoomLevel(18)
            });
            box = mbTileLayer.getBoundingBox();
        } else {
            if (layer.equalsIgnoreCase("OpenStreetMap")) {
                source = new WebSourceTileLayer("openstreetmap",
                        "http://tile.openstreetmap.org/{z}/{x}/{y}.png").setName("OpenStreetMap")
                    .setAttribution("© OpenStreetMap Contributors")
                    .setMinimumZoomLevel(1)
                    .setMaximumZoomLevel(18);
            } else if (layer.equalsIgnoreCase("OpenSeaMap")) {
                source = new WebSourceTileLayer("openstreetmap",
                        "http://tile.openstreetmap.org/seamark/{z}/{x}/{y}.png").setName(
                            "OpenStreetMap")
                            .setAttribution("© OpenStreetMap Contributors")
                            .setMinimumZoomLevel(1)
                            .setMaximumZoomLevel(18);
            } else if (layer.equalsIgnoreCase("mapquest")) {
                source = new WebSourceTileLayer("mapquest",
                        "http://otile1.mqcdn.com/tiles/1.0.0/osm/{z}/{x}/{y}.png").setName(
                            "MapQuest Open Aerial")
                            .setAttribution(
                                    "Tiles courtesy of MapQuest and OpenStreetMap contributors.")
                            .setMinimumZoomLevel(1)
                            .setMaximumZoomLevel(18);
            } else {
                source = new MapboxTileLayer(layer);
            }
            mv.setTileSource(source);
            box = source.getBoundingBox();
        }
        //        mv.setScrollableAreaLimit(mv.getTileProvider().getBoundingBox());
        mv.setScrollableAreaLimit(box);
        mv.setMinZoomLevel(mv.getTileProvider().getMinimumZoomLevel());
        mv.setMaxZoomLevel(mv.getTileProvider().getMaximumZoomLevel());
        mv.setCenter(mv.getTileProvider().getCenterCoordinate());
        mv.setZoom(0);
        Log.d("MainActivity", "zoomToBoundingBox " + box.toString());
        //        mv.zoomToBoundingBox(box);
    }

    private void addLine() {
        // Configures a line
        Paint linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(Color.BLUE);
        linePaint.setStrokeWidth(5);

        PathOverlay po = new PathOverlay().setPaint(linePaint);

        po.addPoint(new LatLng(51.7, 0.3));
        po.addPoint(new LatLng(51.2, 0));

        // Adds line and marker to the overlay
        mv.getOverlays().add(po);
    }

    private Button changeButtonTypeface(Button button) {
        //Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/semibold.ttf");
        //button.setTypeface(tf);
        return button;
    }

    public LatLng getMapCenter() {
        return mv.getCenter();
    }

    public void setMapCenter(ILatLng center) {
        mv.setCenter(center);
    }

    /**
     * Method to show settings  in alert dialog
     * On pressing Settings button will lauch Settings Options - GPS
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getActivity().startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    private void showTasks(List<TaskEntry> data) {

        tasksNorth = -90.0;
        tasksSouth = 90.0;
        tasksEast = -180.0;
        tasksWest = 180.0;

        markers = new ArrayList<Marker> ();

        for(TaskEntry t : data) {
            Log.i("showTasks--- ", t.name + " :: " + t.status + " :: " + t.lon + " :: " + t.lat);
            if(t.lon == 0.0 && t.lat == 0.0) {
                continue;                   // Assume this is not a valid location !!
            }

            if(t.lat > tasksNorth) {
                tasksNorth = t.lat;
            }
            if(t.lat < tasksSouth) {
                tasksSouth = t.lat;
            }
            if(t.lon > tasksEast) {
                tasksEast = t.lon;
            }
            if(t.lat < tasksWest) {
                tasksWest = t.lon;
            }

            LatLng ll = new LatLng(t.lat, t.lon);
            Marker m = new Marker(mv, t.name, t.taskAddress, ll);
            m.setIcon(new Icon(getActivity(), Icon.Size.LARGE, "marker-stroked", getIconColour(t.status)));
            m.setMarker(mv.getDefaultPinDrawable());
            markers.add(m);
        }
        // Add the markers overlay
        if(markers.size() > 0) {

            Log.i("showTasks--- ", "size:: " + markers.size());
            zoomToData();
            Log.i("showTasks--- ", "zoomed ");
            if (markerOverlay == null) {
                markerOverlay = new ItemizedIconOverlay(getActivity(), markers, onItemGestureListener);
                Log.i("showTasks--- ", "created overlay ");
                mv.getOverlays().add(markerOverlay);
                Log.i("showTasks--- ", "added overlay ");
            } else {
                markerOverlay.addItems(markers);
            }
        }
    }

    private void clearTasks() {


    }

    public void setUserLocation(LatLng location) {

    }

    private void zoomToData() {

        // Todo Add current location
        // Todo Add Trace

        double north = tasksNorth;
        double south = tasksSouth;
        double east = tasksEast;
        double west = tasksWest;
        BoundingBox bb = new BoundingBox(north, east, south, west);

        mv.zoomToBoundingBox(bb, true, true, true);
    }

    /*
     * Get the colour to represent the passed in task status
     */
    private String getIconColour(String status) {

        if(status.equals("rejected")) {
            return "FF0000";
        } else if(status.equals("accepted")) {
            return "00FF00";
        } else if(status.equals("complete")) {
            return "0000FF";
        } else if(status.equals("submitted")) {
            return "FF00FF";
        } else {
            return "FFFFFF";
        }
    }

    /*
 * Get the colour to represent the passed in task status
 */
    private String getMakiIcon(String status) {

        if(status.equals("rejected")) {
            return "marker-stroked";
        } else if(status.equals("accepted")) {
            return "marker-stroked";
        } else if(status.equals("complete")) {
            return "marker-stroked";
        } else if(status.equals("submitted")) {
            return "marker-stroked";
        } else {
            return "marker-stroked";
        }
    }


    ItemizedIconOverlay.OnItemGestureListener<Marker> onItemGestureListener
            = new ItemizedIconOverlay.OnItemGestureListener<Marker>(){

        @Override
        public boolean onItemLongPress(int arg0, Marker arg1) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean onItemSingleTapUp(int index, Marker item) {

            return true;
        }

    };
}
