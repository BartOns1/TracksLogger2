package bbsource.trackslogger;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import bbsource.trackslogger.domain.Coordinate;
import bbsource.trackslogger.domain.Participant;

/**
 * Created by vdabcursist on 11/10/2017.
 */

public class BackgroundTransmitterService extends Service {

//todo hier eigenlijk de permisie controleren en locatiemanage gebruiken om coordinaten op te vangen

    private static Timer timer;

    public BackgroundTransmitterService() {
        super();
    }
    String participantName=null;
    String groupName;

    Coordinate coordinate = new Coordinate();



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        Toast.makeText(getApplicationContext(), "TransmitterService Created", Toast.LENGTH_SHORT).show();
        super.onCreate();

        Log.i("BackgroundService", "created backgroundservice TTTT");

        if (timer != null) {
            timer.cancel();
        } else {
            timer = new Timer();
        }



        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 4, locationListener);

    }



    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            String msg = "Location captured successfully: longitude:%s  latitude:%s";
            msg = String.format(msg, location.getLongitude(), location.getLatitude());

            coordinate.setLongitude(location.getLongitude());
            coordinate.setLatitude(location.getLatitude());
            coordinate.setTime(System.currentTimeMillis());

            Log.d("participantnameisempthycheck", String.valueOf(participantName.isEmpty())+" "+participantName);
            if (!participantName.isEmpty()) {
                DataTransmitParser.sendRequest(participantName, coordinate);
            }

            Log.i("GEOLOCATION", msg);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "TransmitterService Started", Toast.LENGTH_SHORT).show();
        Log.i("BackgroundService", "starting backgroundservice TT");

        // todo read extra's from intent
        if (intent.getExtras()!=null) {
            groupName = intent.getExtras().getString("groupName");
            participantName = intent.getExtras().getString("participantName");
            Log.i("check name in service transmit", participantName);
        }



        if (coordinate.getLatitude()!=0 && coordinate.getLongitude()!=0 && coordinate.getTime()!=0) {
            Log.d("Timer","Send Coordinates started every 10 seconds");
            timer.scheduleAtFixedRate(new SendCoordinatesTimer(), 0, 10000);
        }
        



        //
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(), "TransmitterService Destroyed", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }


    class SendCoordinatesTimer extends TimerTask {
        @Override
        public void run() {
            DataTransmitParser.sendRequest(participantName, coordinate);

        }
    }
}
