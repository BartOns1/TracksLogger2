package bbsource.trackslogger;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

import bbsource.trackslogger.domain.Participant;

/**
 * Created by vdabcursist on 10/10/2017.
 */

public class AppController extends Application{
    private static final String TAG = "AppController";
    private static AppController instance;

    private List<Participant> participants = new ArrayList<>();

    private List<ParticipantObserver> observers =  new ArrayList<>();



    public interface ParticipantObserver {
        void notifyParticipantsChanged();
    }

    public void addParticipantObserver(ParticipantObserver observer) {
        observers.add(observer);
    }

    public void removeParticipantObserver(ParticipantObserver observer) {
        observers.remove(observer);
    }

    //
    public void notifyObservers() {
        for (ParticipantObserver observer : observers) {
            observer.notifyParticipantsChanged();
        }
    }


    public static synchronized AppController getInstance() {
        return instance;
    }


    private RequestQueue requestQueue;

    @Override
    public void onCreate(){
        super.onCreate();
        instance=this;
    }


    public RequestQueue getRequestQueue(){
        if (requestQueue==null){
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag){
        req.setTag(TextUtils.isEmpty(tag) ? TAG: tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request <T> req){
        req.setTag(TAG);
        getRequestQueue().add(req);
    }


    public void cancelPendingRequests(Object tag){
        if (requestQueue!=null){
            requestQueue.cancelAll(tag);
        }
    }

    public List<Participant> getParticipants() {
        return participants;
    }
}
