package bbsource.trackslogger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import bbsource.trackslogger.domain.Coordinate;
import bbsource.trackslogger.domain.Participant;


public class RegisterActivity extends AppCompatActivity {

    String sensingGroupName = null;
    int sensingId = -1;
    public static final int NAMES_PROVIDED = 11;
    EditText groupName;
    EditText participantName;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        groupName = (EditText) findViewById(R.id.groupName);
        participantName = (EditText) findViewById(R.id.trackName);
        submit = (Button) findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                if (groupName.getText().toString().trim().isEmpty()||participantName.getText().toString().trim().isEmpty()) {
                    // set toast
                } else {

                    Log.i("status", "tot hier gekomen");


                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                            "http://172.30.68.13:8080/api/group/" + groupName.getText().toString().trim(), null, new Response.Listener<JSONObject>() {
                        //IP van de vdab veranderen in 102.0.2... of zoiets  http://172.30.68.18:8080/api/group/Knights%20saying%20NI/participants
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("Response check registration", response.toString());
                            String info;
                            try {
                                //
                                sensingGroupName = response.getString("groupName");
                                sensingId = response.getInt("id");
                                Log.i("sensing Group", "Groupname: " + sensingGroupName + " groupID: " + sensingId);
                                if (sensingId != -1){ RegisterActivity.this.finish();}

                            } catch (JSONException e) {
                                System.out.println("--------------------------------------");
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.d("response", "Error:" + error.getMessage());
                            System.out.println("///////////////////////////////");
                        }
                    });
                    AppController.getInstance().addToRequestQueue(request);}
            }
        });

    }

    @Override
    public void finish() {
        Intent data = new Intent();
        data.putExtra("groupName", sensingGroupName);
        data.putExtra("participantName", participantName.getText().toString().trim());
        setResult(NAMES_PROVIDED, data);



        Participant participant = new Participant(participantName.getText().toString().trim(), sensingGroupName);

        JsonObjectRequest putrequest = new JsonObjectRequest(Request.Method.POST,
                "http://172.30.68.13:8080/api/group/participant", participant.toJSON(), new Response.Listener<JSONObject>()

        {
            //IP van de vdab veranderen in 102.0.2... of zoiets  http://172.30.68.18:8080/api/group/Knights%20saying%20NI/participants

            @Override
            public void onResponse(JSONObject response) {
                Log.d("Response participant created", response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("response", "Error:" + error.getMessage());
                System.out.println("/////no new participant created//////////////////////////");
            }
        });

        AppController.getInstance().addToRequestQueue(putrequest);


        super.finish();

    }

    ;

}
