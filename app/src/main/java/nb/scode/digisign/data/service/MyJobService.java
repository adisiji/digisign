package nb.scode.digisign.data.service;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import timber.log.Timber;

/**
 * Created by neobyte on 5/23/2017.
 */

public class MyJobService extends JobService {

  private static final String TAG = "MyJobService";

  @Override public boolean onStartJob(JobParameters jobParameters) {
    Timber.d(TAG, "Performing long running task in scheduled job");
    // TODO(developer): add long running task here.
    return false;
  }

  @Override public boolean onStopJob(JobParameters jobParameters) {
    return false;
  }
}
