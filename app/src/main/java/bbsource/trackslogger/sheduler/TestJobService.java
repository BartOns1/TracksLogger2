package bbsource.trackslogger.sheduler;

        import android.app.job.JobParameters;
        import android.app.job.JobService;
        import android.content.Intent;

        import bbsource.trackslogger.BackgroundReceiverService;

/**
 * JobService to be scheduled by the JobScheduler.
 * start another service
 */
public class TestJobService extends JobService {
    private static final String TAG = "SyncService";

    @Override
    public boolean onStartJob(JobParameters params) {
        Intent service = new Intent(getApplicationContext(), BackgroundReceiverService.class);
        getApplicationContext().startService(service);
        Util.scheduleJob(getApplicationContext()); // reschedule the job
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

}