package bbsource.trackslogger;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import bbsource.trackslogger.domain.Participant;

/**
 * Created by vdabcursist on 11/10/2017.
 */

public class BackgroundReceiverService extends Service {

    String groupName = "Knights saying NI";//TODO

    private Timer timer;

    public BackgroundReceiverService() {
        super();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        Toast.makeText(getApplicationContext(), "ReceiverService Created", Toast.LENGTH_SHORT).show();
        super.onCreate();

        Log.i("BackgroundService", "created backgroundservice");

        if (timer != null) {
            timer.cancel();
        } else {
            timer = new Timer();
        }

        timer.scheduleAtFixedRate(new FetchParticipantsTimer(), 0, 5000);
        //
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "ReceiverService Started", Toast.LENGTH_SHORT).show();
        Log.i("BackgroundService", "starting backgroundservice");


        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(), "ReceiverService Destroyed", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    class FetchParticipantsTimer extends TimerTask {

        @Override
        public void run() {
            DataReceiveParser.jSonParser(groupName);



        }
    }

}
