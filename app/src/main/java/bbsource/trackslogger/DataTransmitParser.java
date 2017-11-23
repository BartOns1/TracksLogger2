package bbsource.trackslogger;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bbsource.trackslogger.domain.Coordinate;
import bbsource.trackslogger.domain.Group;
import bbsource.trackslogger.domain.Participant;

/**
 * Created by vdabcursist on 21/11/2017.
 */

public final class DataTransmitParser {

        public static void sendRequest (String participantName, final Coordinate coordinate) {

            JsonObjectRequest putrequest = new JsonObjectRequest (Request.Method.PUT,
                    "http://172.30.68.13:8080/api/group/participant/coordinates/" + participantName, coordinate.toJSON(), new Response.Listener<JSONObject>()

            {
                //IP van de vdab veranderen in 102.0.2... of zoiets  http://172.30.68.18:8080/api/group/Knights%20saying%20NI/participants

                @Override
                public void onResponse(JSONObject response) {
                    Log.d("Sending Coordinates", response.toString());

                }
            },
                    // TODO Bart: een PUT verwacht geen response, vandaar dat het parsen een error geeft... om de error te 'ignoren' mag je null gebruiken voor de error listener
//            , new Response.ErrorListener()
//            {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    VolleyLog.d("Sending coordinate failed " + error.getMessage() + " coord: " + coordinate.toJSON());
//                    System.out.println("///////////////////////////////");
//                }
//
//            }
            null
            );
            AppController.getInstance().addToRequestQueue(putrequest);
             /*   {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }

           //     @Override
           /*     public byte[] getBody() {

                    try {
                        Log.i("json", participant.toJSON().toString());
                        return participant.toJSON().toString().getBytes("UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    return null;
                }*/
            }


        }
