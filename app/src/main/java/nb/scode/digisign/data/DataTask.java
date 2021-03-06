package nb.scode.digisign.data;

import nb.scode.digisign.data.local.LocalTask;
import nb.scode.digisign.data.remote.ApiTask;

/**
 * Created by neobyte on 4/28/2017.
 * ********************************
 * Define Task that has not been declare in {@link LocalTask}
 */
public interface DataTask extends LocalTask, ApiTask {

  void checkKeyStore(CertCheckListener listener);

  interface CertCheckListener {
    void onProgress();

    void onSuccess();

    void onFailed(String message);
  }
}
