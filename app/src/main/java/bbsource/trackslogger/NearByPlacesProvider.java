package bbsource.trackslogger;

import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

/**
 * Created by vdabcursist on 09/10/2017.
 */

class NearByPlacesProvider extends AsyncTask<Object,String,String>{

    String googlePlacesData;
    GoogleMap map;
    String url;

    @Override
    protected String doInBackground(Object... params) {
        try {
            map=(GoogleMap) params[0];
            url = (String) params[1];
            HttpHandler downloadUrl = new HttpHandler();
            googlePlacesData = downloadUrl.getDataFromUrl(url);
        } catch (Exception e){
            e.printStackTrace();
        }
        return googlePlacesData;
    }

    public void onPostExecute(String result){
        List<HashMap<String,String>> nearbyPlaceList = null;
        DataParser dataParser = new DataParser();
        nearbyPlaceList = dataParser.parse(result);
        ShowNearByPlaces(nearbyPlaceList);
    }

    private void ShowNearByPlaces(List<HashMap<String, String>> nearbyPlaceList) {

        for (int i=0;i<nearbyPlaceList.size();i++){
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String,String> googlePlace = nearbyPlaceList.get(i);
            double lng = Double.parseDouble(googlePlace.get("lng"));
            double lat = Double.parseDouble(googlePlace.get("lat"));
            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            LatLng latLng = new LatLng(lat,lng);

            markerOptions.position(latLng);
            markerOptions.title(placeName+" : " + vicinity);

            map.addMarker(markerOptions);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            //move map camera
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            map.animateCamera(CameraUpdateFactory.zoomTo(11));

        }
    }
}
