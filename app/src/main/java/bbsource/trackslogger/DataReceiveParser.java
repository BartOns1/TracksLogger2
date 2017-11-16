package bbsource.trackslogger;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import bbsource.trackslogger.domain.Coordinate;
import bbsource.trackslogger.domain.Participant;

/**
 * Created by vdabcursist on 11/10/2017.
 */

public final class DataReceiveParser {
    private void DataReceiverParser(){};



    public static void jSonParser(String groupName){

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                "http://172.30.68.13:8080/api/group/"+ groupName + "/participants", null  ,new Response.Listener<JSONArray>(){
            //IP van de vdab veranderen in 102.0.2... of zoiets  http://172.30.68.18:8080/api/group/Knights%20saying%20NI/participants
            @Override
            public void onResponse(JSONArray response){
               Log.d("Response", response.toString());
                String info;
                try {
                   // Log.i("aantal in response", String.valueOf(response.length()));
                    for(int i=0; i<response.length();i++) {
                        JSONObject sensingParticipant = response.getJSONObject(i);
                        String participantName = sensingParticipant.getString("label");


                        Participant participant = new Participant(participantName, "logfileTODO");
                        JSONArray coords = sensingParticipant.getJSONArray("coordinates");
                        // Log.i("check if coordinates are read from jsonobject", coords.toString());
                        List<Coordinate> coordinates = new ArrayList<>();


                        //parse coordinates from JSONobject
                        for (int j = 0; j < coords.length(); j++) {
                            JSONObject coord = coords.getJSONObject(j);
                            if (coord != null) {
                                //Log.i("coords is :", coord.toString());
                                // Log.i("time is ",String.valueOf(Long.parseLong(coord.getString("time"))));
                                Coordinate coortemp = new Coordinate(Long.parseLong(coord.getString("time")),
                                        Double.parseDouble(coord.getString("latitude")), Double.parseDouble(coord.getString("longitude")));
                                //  Log.i("coortemp is", coortemp.toString());
                                coordinates.add(j, coortemp);
                            }
                        }
                        participant.setCoordinates(coordinates);

                    //hier is de participant ingelezen uit de json en er een jasonobject van gemaakt.
                    //check nu of de participant al bestaat in de unieke(static) lokale participants-collection. zo niet: voeg toe, zowel update deze. Het is deze participantslijst die bijgehouden wordt over methdoen heen doordat het static is

                        //contains roept equalsmethode aan die je hebt overschreven bij de definitie van de klasse participant
                        List<Participant> participants = AppController.getInstance().getParticipants();
                        if (participants.contains(participant)){
                            int participantIndex=participants.indexOf(participant);
                            //Update coordinates depending wether person already exist with some coordinates(IF-clause) or not(ELSE-clause)
                            if (participants !=null){
                                if (coords != null) {

                                  //  Log.i("participants.get(i).getCoordinates().size()", String.valueOf(participants.get(i).getCoordinates().size()));
                                    //  Log.i("coordinates.size", String.valueOf(coordinates.size()));
                                    //assume that coords from http request always larger then coords from local participant

                                        int startIndex = participants.get(participantIndex).getCoordinates().size();
                                        int endIndex = coordinates.size();
                                        participants.get(participantIndex).getCoordinates().addAll(startIndex,coordinates.subList(startIndex,endIndex));

                                } else {
                                participant.setCoordinates(coordinates);
                             }
                            }
                        } else {
                            participants.add(participant);
                        }
                    }

                    AppController.getInstance().notifyObservers();
                        ;

                        //Log.i("check if coordinates are build", coordinates.toString());




                    //



                    System.out.println("++++++++++++++++++++ooooo+++++++++++++++");
                    //Log.i("participant in datareceiveparser", participants.toString());
                } catch (JSONException e) {
                    System.out.println("--------------------------------------");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                VolleyLog.d("response", "Error:" + error.getMessage());
                System.out.println("///////////////////////////////");
            }
        });
        AppController.getInstance().addToRequestQueue(request);

    }

}
