package nb.scode.digisign.data;

import android.net.Uri;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import javax.inject.Inject;
import javax.inject.Singleton;
import nb.scode.digisign.data.local.LocalTask;
import nb.scode.digisign.data.remote.ApiTask;

/**
 * Created by neobyte on 4/28/2017.
 */
@Singleton public class DataManager implements DataTask {

  private final LocalTask localTask;
  private final ApiTask apiTask;

  /**
   * Instantiates a new Data manager.
   *
   * @param localTask the preferences task
   * @param apiTask the api task
   */
  @Inject DataManager(LocalTask localTask, ApiTask apiTask) {
    this.localTask = localTask;
    this.apiTask = apiTask;
  }

  @Override public void clear() {

  }

  @Override public boolean isFirstUse() {
    return localTask.isFirstUse();
  }

  @Override public void setNotFirstUse() {
    localTask.setNotFirstUse();
  }

  @Override public void getPrepFilePdf(Uri uri, ListenerPrepPdf listenerPrepPdf) {
    localTask.getPrepFilePdf(uri, listenerPrepPdf);
  }

  @Override public void getCert(CommonListener listener) {
    localTask.getCert(listener);
  }

  @Override public void register(String email, String pass, CommonAListener listener) {
    apiTask.register(email, pass, listener);
  }

  @Override public void login(String email, String pass, CommonAListener listener) {
    apiTask.login(email, pass, listener);
  }

  @Override public void firebaseAuthWithGoogle(GoogleSignInAccount account,
      CommonAListener aListener) {
    apiTask.firebaseAuthWithGoogle(account, aListener);
  }

  @Override public boolean isUserSignedIn() {
    return apiTask.isUserSignedIn();
  }

  @Override public void getPhotoUri() {
    apiTask.getPhotoUri();
  }
}
