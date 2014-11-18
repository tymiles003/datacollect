package org.smap.smapTask.android.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
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
import com.mapbox.mapboxsdk.tileprovider.tilesource.ITileLayer;
import com.mapbox.mapboxsdk.tileprovider.tilesource.MBTilesLayer;
import com.mapbox.mapboxsdk.tileprovider.tilesource.MapboxTileLayer;
import com.mapbox.mapboxsdk.tileprovider.tilesource.TileLayer;
import com.mapbox.mapboxsdk.tileprovider.tilesource.WebSourceTileLayer;
import com.mapbox.mapboxsdk.views.MapView;
import com.mapbox.mapboxsdk.views.util.TilesLoadedListener;

import org.smap.smapTask.android.R;
import org.smap.smapTask.android.utilities.Utilities;

import java.util.ArrayList;
import java.util.List;

import loaders.SmapTaskLoader;
import loaders.TaskEntry;

import static org.smap.smapTask.android.R.drawable;

public class MapFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<TaskEntry>>,
{

    ItemizedIconOverlay markerOverlay = null;
    PathOverlay po = null;
    ArrayList<Marker> markers = null;
    private double tasksNorth;
    private double tasksSouth;
    private double tasksEast;
    private double tasksWest;

    Marker userLocationMarker = null;
    Icon userLocationIcon = null;
    Icon accepted = null;
    Icon rejected = null;
    Icon complete = null;
    Icon submitted = null;

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

        // Create icons
        userLocationIcon = new Icon(new BitmapDrawable(getResources(),BitmapFactory.decodeResource(getResources(), drawable.ic_userlocation)));
        accepted = new Icon(new BitmapDrawable(getResources(),BitmapFactory.decodeResource(getResources(), drawable.ic_task_open)));
        rejected = new Icon(new BitmapDrawable(getResources(),BitmapFactory.decodeResource(getResources(), drawable.ic_task_reject)));
        complete = new Icon(new BitmapDrawable(getResources(),BitmapFactory.decodeResource(getResources(), drawable.ic_task_done)));
        submitted = new Icon(new BitmapDrawable(getResources(),BitmapFactory.decodeResource(getResources(), drawable.ic_task_submitted)));

        // Set Default Map Type
        replaceMapView("mapquest");
        currentLayer = "terrain";

        getLoaderManager().initLoader(MAP_LOADER_ID, null, this);       // Get the task locations


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
        showTasks(data);
    }

    @Override
    public void onLoaderReset(Loader<List<TaskEntry>> loader) {
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
     * On pressing Settings button will launch Settings Options - GPS
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

        // Add the user location
        if(userLocationMarker != null) {
            markers.add(userLocationMarker);
        }

        // Add the tasks to the marker array
        for(TaskEntry t : data) {
            if(t.type.equals("task")) {
                LatLng ll = getTaskCoords(t);
                if (ll != null) {
                    Marker m = new Marker(mv, t.name, t.taskAddress, ll);
                    m.setIcon(getIcon(t.taskStatus));
                    markers.add(m);
                }
            }
        }

        // Remove any existing markers
        if(markerOverlay != null) {
            markerOverlay.removeAllItems();
        }

        // Add the marker layer
        if(markers.size() > 0) {
            if (markerOverlay == null) {
                markerOverlay = new ItemizedIconOverlay(getActivity(), markers, onItemGestureListener);
                mv.getOverlays().add(markerOverlay);
            } else {
                markerOverlay.addItems(markers);
            }
        }

        zoomToData(false);
    }


    private void clearTasks() {
        if(markerOverlay != null) {
            markerOverlay.removeAllItems();
        }
    }

    public void setUserLocation(Location location) {
        Log.i("MapFragment", "setUserLocation()");

        LatLng point = new LatLng(location.getLatitude(), location.getLongitude());

        if(markers == null) {
            markers = new ArrayList<Marker>();
        }
        if(userLocationMarker == null) {
            userLocationMarker = new Marker(mv, "", "", point);
            userLocationMarker.setIcon(userLocationIcon);

            if (markerOverlay == null) {
                markers.add(userLocationMarker);
                markerOverlay = new ItemizedIconOverlay(getActivity(), markers, onItemGestureListener);
                mv.getOverlays().add(markerOverlay);
            } else {
                markerOverlay.addItem(userLocationMarker);
            }
        } else {
            userLocationMarker.setPoint(point);
            userLocationMarker.updateDrawingPosition();
            userLocationMarker.setIcon(userLocationIcon);
        }
        updatePath(point);
        zoomToData(true);
    }

    private void updatePath(LatLng point) {
        if(po == null) {
            Paint linePaint = new Paint();
            linePaint.setStyle(Paint.Style.STROKE);
            linePaint.setColor(Color.BLUE);
            linePaint.setStrokeWidth(5);


            po = new PathOverlay().setPaint(linePaint);
            mv.getOverlays().add(po);
        }

        po.addPoint(point);

    }

    private void zoomToData(boolean userLocationChanged) {

        boolean userOutsideBoundingBox = false;
        double north = tasksNorth;    // Add Margin
        double south = tasksSouth;
        double east = tasksEast;
        double west = tasksWest;

        // Add current location to bounding box
        if(userLocationMarker != null) {
            double lat = userLocationMarker.getPoint().getLatitude();
            double lon = userLocationMarker.getPoint().getLongitude();
            if(lat > north) {
                north = lat;
                userOutsideBoundingBox = true;
            }
            if(lat < south) {
                south = lat;
                userOutsideBoundingBox = true;
            }
            if(lon > east) {
                east = lon;
                userOutsideBoundingBox = true;
            }
            if(lon < west) {
                west = lon;
                userOutsideBoundingBox = true;
            }

            if(userLocationChanged) {
                BoundingBox viewableBox = mv.getBoundingBox();
                if(lat > viewableBox.getLatNorth() ||
                        lat > viewableBox.getLatSouth() ||
                        lon > viewableBox.getLonEast() ||
                        lon < viewableBox.getLonWest()
                        ) {
                    userOutsideBoundingBox = true;
                }
            }
        }

        // Make sure bounding box is not a point
        if(north == south) {
            north += 0.01;
            south -= 0.01;
        }
        if(east == west) {
            east += 0.01;
            west -= 0.01;
        }

        /*
         * Zoom to the new bounding box only if the task list has changed or the user is outside of the current
         *  viewable area
         */
        if(north > south && east > west) {
            if(!userLocationChanged || userOutsideBoundingBox) {
                BoundingBox bb = new BoundingBox(north, east, south, west);
                mv.zoomToBoundingBox(bb, true, true, true, true);
            }
        }
    }

    /*
     * Get the colour to represent the passed in task status
     */
    private Icon getIcon(String status) {

        if(status.equals(Utilities.STATUS_T_REJECTED) || status.equals(Utilities.STATUS_T_CANCELLED)) {
            return rejected;
        } else if(status.equals(Utilities.STATUS_T_ACCEPTED)) {
            return accepted;
        } else if(status.equals(Utilities.STATUS_T_COMPLETE)) {
            return complete;
        } else if(status.equals(Utilities.STATUS_T_SUBMITTED)) {
            return submitted;
        } else {
            Log.i("MapFragment", "Unknown task status: " + status);
            return accepted;
        }
    }

    /*
     * Get the coordinates of the task and update the bounding box
     */
    private LatLng getTaskCoords(TaskEntry t) {

        double lat = 0.0;
        double lon = 0.0;
        LatLng locn = null;

        if((t.actLat == 0.0) && (t.actLon == 0.0)) {
            lat = t.schedLat;       // Scheduled coordinates of task
            lon = t.schedLon;
        } else  {
            lat = t.actLat;         // Actual coordinates of task
            lon = t.actLon;
        }

        if(lat != 0.0 && lon != 0.0) {
            // Update bounding box
            if(lat > tasksNorth) {
                tasksNorth = lat;
            }
            if(lat < tasksSouth) {
                tasksSouth = lat;
            }
            if(lon > tasksEast) {
                tasksEast = lon;
            }
            if(lat < tasksWest) {
                tasksWest = lon;
            }

            // Create Point
            locn = new LatLng(lat, lon);
        }


        return locn;
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
