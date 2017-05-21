package nb.scode.digisign.data;

import nb.scode.digisign.data.local.LocalTask;
import nb.scode.digisign.data.remote.ApiTask;

/**
 * Created by neobyte on 4/28/2017.
 * ********************************
 * Define Task that has not been declare in {@link LocalTask}
 */
public interface DataTask extends LocalTask, ApiTask {

  void initKeyPair(InitListener listener);

  boolean isRecentEmailSame();

  void setRecentEmail();

  void uploadKeyPair(DataListener listener);

  void downloadKeyPair(DataListener listener);

  interface DataListener {

    void onSuccess();

    void onProcess();

    void onFailed(String message);
  }

  interface InitListener {

    void onStartInit();

    void onCreateKey();

    void onUploadKey();

    void onFinishInit();

    void onError(String message);

  }
}
