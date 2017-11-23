package bbsource.trackslogger.domain;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by vdabcursist on 11/10/2017.
 */

public class Coordinate {


    private long timeStamp;
    private double latitude;
    private double longitude;

    public Coordinate() {
    }

    public Coordinate(long timeStamp, double latitude, double longitude) {
        this.timeStamp = timeStamp;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public long getTime() {
        return timeStamp;
    }

    public void setTime(long time) {
        this.timeStamp = time;
    }

    public double getLatitude() { return latitude;}

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Coordinate{" +
                ", time=" + timeStamp + ", latitude=" + latitude + ", longitude= " + longitude + "}";
    }

    public JSONObject toJSON() {
        JSONObject jo = new JSONObject();
        try {
            jo.put("latitude",  this.getLatitude());
            jo.put("longitude", this.getLongitude());
            jo.put("timeStamp", this.getTime());
        } catch (JSONException e)
        {
            e.printStackTrace();

        }
        Log.d("Coordinate Json", jo.toString());
        return jo;
    }
}
