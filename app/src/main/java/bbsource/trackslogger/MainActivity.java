package bbsource.trackslogger;

import android.Manifest;
import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import bbsource.trackslogger.domain.Coordinate;
import bbsource.trackslogger.domain.Participant;

import static bbsource.trackslogger.RegisterActivity.NAMES_PROVIDED;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, AppController.ParticipantObserver {


    private static final int GPSREQUEST_FINE_LOCATION = 777;
    private static final int GET_NAME = 10;
    public static final int RECEIVE_DATA = 12;
    public static final int TRANSMIT_DATA = 14;

    TextView tvCapturedGeoLoc;
    private GoogleApiClient googleApiClient;
    ImageView ivRadar;

    Button btnCaptureGeoinfo;
    Button btnStopCaptureGeoinfo;

    private String participantName;
    private String groupName;
    private static final String google_map_key = "AIzaSyBvqVBvWAak2PLh0YEEwWHx780LEXX72kk";
    private double longitude = 50.85077;
    private double latitude = 4.724099;
    private SupportMapFragment mapFragment;

    public Intent getReceiverServiceIntent() {
        return receiverServiceIntent;
    }

    public Intent getTransmitterServiceIntent() {
        return transmitterServiceIntent;
    }

    private Intent receiverServiceIntent;
    private Intent transmitterServiceIntent;
    private GoogleMap googleMap;

    private HashMap<Integer, Integer> trackColors = new HashMap<Integer, Integer>();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivRadar = (ImageView) findViewById(R.id.ivRadar);
        tvCapturedGeoLoc = (TextView) findViewById(R.id.tvCapturedGeoLocation);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // todo only launch the first time (check if preference existif not, then register and save in preference, then use button te do new registration and save new preferences
        // todo for now define participantname and groupname in global scope and set values via registration form, save it in preferences and provide as extra info to the serviceintents

        Intent registerQuery = new Intent(this, RegisterActivity.class);
        startActivityForResult(registerQuery, GET_NAME);



        //String[] participants = new String[]{"person1", "person2", "person3", "person4",
        //        "person5", "person6", "person7", "person8", "person9", "person10", "person11"};
        //String[] colors = new String[]{"white", "red", "blue", "green",
          //      "yellow", "cyan", "magenta", "darkgray"};
        trackColors.put(0, Color.WHITE);
        trackColors.put(1,Color.RED);
        trackColors.put(2,Color.BLUE);
        trackColors.put(3,Color.GREEN);
        trackColors.put(4,Color.YELLOW);
        trackColors.put(5,Color.CYAN);
        trackColors.put(6,Color.MAGENTA);
        trackColors.put(7,Color.DKGRAY);



        List<Participant> participants=AppController.getInstance().getParticipants();
        List<String> participantNames=new ArrayList<>();
        for(Participant p:participants){
            participantNames.add(p.getLabel());
        }

        Log.d("PARTICIPANTNAMES", participantNames.toString());

        ArrayAdapter<String> aa_names = new ArrayAdapter<String>(this, R.layout.list_os, participantNames );
/*        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(trackColors.get(position));
                return view;
            }
        };*/
        ListView listviewName = (ListView) findViewById(R.id.listViewOS);
        listviewName.setAdapter(aa_names);

        //ArrayAdapter<String> aa_colors = new ArrayAdapter<String>(this, R.layout.list_col, colors);
        // ListView listViewCol = (ListView) findViewById(R.id.list_color);
        //listViewCol.setAdapter(aa_colors);


        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        System.out.println("**************************************************");




        btnCaptureGeoinfo = (Button) findViewById(R.id.btnCaptureGeolocation);
        btnCaptureGeoinfo.setVisibility(View.VISIBLE);
        btnStopCaptureGeoinfo = (Button) findViewById(R.id.btnStopCaptureGeolocation);
        btnStopCaptureGeoinfo.setVisibility(View.INVISIBLE);
        btnCaptureGeoinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCapturedLocationOnClick();

                //form om naam aan te maken en bij group te voegen


            }
        });

        btnStopCaptureGeoinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStopCapturedLocationOnClick();
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        AppController.getInstance().addParticipantObserver(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppController.getInstance().removeParticipantObserver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(receiverServiceIntent);
        Log.d("RrecieverServiceIntent", receiverServiceIntent.toString());

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(longitude, latitude));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
        googleMap.moveCamera(center);
        googleMap.animateCamera(zoom);


        // getNearByRestaurent(longitude,latitude , googleMap);
    }

    private void getNearByRestaurent(double lat, double longu, GoogleMap googleMap) {
        googleMap.clear();
        String url = getUrl(lat, longu);
        Object[] DataTransfer = new Object[2];
        DataTransfer[0] = googleMap;
        DataTransfer[1] = url;
        NearByPlacesProvider nearByPlacesProvider = new NearByPlacesProvider();
        nearByPlacesProvider.execute(DataTransfer);
        Toast.makeText(this, "Nearby Restaurants", Toast.LENGTH_LONG).show();

    }

    private String getUrl(double latitude, double longitude) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + 15000);
        googlePlacesUrl.append("&type=restaurant");
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + google_map_key);
        Log.d("url", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }

    private boolean checkPermisionMissing() {
        return (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(this, "GPS permission needed in order to capture your location", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, GPSREQUEST_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case GPSREQUEST_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(tvCapturedGeoLoc, "Permission Granted, Now you can access location data.", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(tvCapturedGeoLoc, "Permission denied, You cannot access locatino data.", Snackbar.LENGTH_LONG).show();
                }
                break;
        }


    }


    private String getAddressFromLocation(double latitude, double longitude) throws IOException {
        Geocoder geoCoder = new Geocoder(this);
        List<Address> matches = geoCoder.getFromLocation(latitude, longitude, 1);
        if (matches.size() < 1) return "";
        return (matches.isEmpty() ? null : matches.get(0)).getAddressLine(0).toString();
    }

    @SuppressLint("MissingPermission")
    public void btnCapturedLocationOnClick() {
        if (checkPermisionMissing()) {
            requestPermission();
        }

        if (!checkPermisionMissing()) {


            if (participantName!=null){
            transmitterServiceIntent = new Intent(MainActivity.this, BackgroundTransmitterService.class);
            transmitterServiceIntent.putExtra("groupName", groupName);
            transmitterServiceIntent.putExtra("participantName", participantName);


            startService(transmitterServiceIntent);
            Log.d("transmitterServiceIntent", transmitterServiceIntent.toString());
            }
        }

        //btnCaptureGeoinfo = (Button) findViewById(R.id.btnCaptureGeolocation);
        btnCaptureGeoinfo.setVisibility(View.INVISIBLE);
        //btnStopCaptureGeoinfo = (Button) findViewById(R.id.btnStopCaptureGeolocation);
        btnStopCaptureGeoinfo.setVisibility(View.VISIBLE);
    }

    public void btnStopCapturedLocationOnClick() {

        //code om het visualiseren van eigen coordinaten te stoppen eigenlijk moet dit een veld activeren visibility=actief en meesturen naar database.
        // Bij het binnenhalen van de data mogen personen die invisible willen zijn niet getoond worden.

        //btnCaptureGeoinfo = (Button) findViewById(R.id.btnCaptureGeolocation);
        btnCaptureGeoinfo.setVisibility(View.VISIBLE);
        //btnStopCaptureGeoinfo = (Button) findViewById(R.id.btnStopCaptureGeolocation);
        btnStopCaptureGeoinfo.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Unable to find nearby restaurants, check your internet connection", Toast.LENGTH_LONG).show();
    }

    @Override
    public void notifyParticipantsChanged() {
        //hier plotten coordinaten en participants
        List<Participant> participants = AppController.getInstance().getParticipants();

        //Log.i("MainActivity", "MainActivity notified with new list ");
        //Log.i("MainActivity", AppController.getInstance().getParticipants().toString());
        for(Integer i = 0;i<participants.size(); i++) {
            List<LatLng> points = new ArrayList<>();
            PolylineOptions polylineOptions = new PolylineOptions();
            if (participants.get(i).getCoordinates() != null && !participants.get(i).getCoordinates().isEmpty()) {
                for (Coordinate c : participants.get(i).getCoordinates()) {
                    polylineOptions = polylineOptions.add(new LatLng(c.getLatitude(), c.getLongitude()));
                }
                Polyline line = googleMap.addPolyline(
                        polylineOptions
                                .width(5)
                                .color(trackColors.get(i%trackColors.size())));
               // Log.d("PolylineNumber", String.valueOf(i));

            }Log.d("polylineCoordinaten", participants.get(i).getCoordinates().toString());
        }
        Log.d("Participants in notifyParticipantsChanged", participants.get(participants.size()-1).getCoordinates().toString());
        //
        //onMapReady(googleMap);



        List<String> participantNames=new ArrayList<>();
        for(Participant p:participants){
            participantNames.add(p.getLabel());
        }

        Log.d("PARTICIPANTNAMES", participantNames.toString());

        ArrayAdapter<String> aa_names = new ArrayAdapter<String>(this, R.layout.list_os, participantNames )
        {
            @Override
            public View getView(int position, View view, ViewGroup parent) {
                View convertView = super.getView(position, view, parent);
                convertView.setBackgroundColor(trackColors.get(position % trackColors.size()));

                return convertView;

            }
        };
        ListView listviewName = (ListView) findViewById(R.id.listViewOS);
        listviewName.setAdapter(aa_names);



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        switch(requestCode){
            case GET_NAME:
                if(resultCode == RegisterActivity.NAMES_PROVIDED){
                    Bundle resultData = data.getExtras();
                    //Log.i("groupName", resultData.getString("groupName"));
                    //Log.i("participantName", resultData.getString("participantName"));
                    groupName = resultData.getString("groupName","this ni gelukt1");
                    participantName = resultData.getString("participantName", "ne contributeur1");


                    int mode = MainActivity.MODE_PRIVATE;
                    SharedPreferences mySharedPreferences = getSharedPreferences("mySharedPreferences", mode);


                    SharedPreferences.Editor editor = mySharedPreferences.edit();
                    editor.putString("groupName", groupName);
                    editor.putString("participantName",participantName );
                    editor.commit();

                    // todo save in preferences-->done

                    launchServices(groupName);
                }

        }
    }

    private void launchServices(String groupName) {

        receiverServiceIntent = new Intent(MainActivity.this, BackgroundReceiverService.class);
        receiverServiceIntent.putExtra("groupName", groupName);


        startService(receiverServiceIntent);
        Log.d("ReceiverServiceIntent", receiverServiceIntent.toString());





    }
}
