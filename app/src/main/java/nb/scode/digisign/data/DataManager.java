package nb.scode.digisign.data;

import android.net.Uri;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import java.io.File;
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

  @Override public boolean isKeyPairAvailable() {
    return localTask.isKeyPairAvailable();
  }

  @Override public void createKey(CommonListener listener) throws Exception {
    localTask.createKey(listener);
  }

  @Override public void initKeyPair(final InitListener listener) {
    listener.onStartInit();
    try {
      createKey(new CommonListener() {
        @Override public void onFinished() {
          File file = getPublicKey();
          uploadPublicKey(file, listener);
        }

        @Override public void onError(String message) {

        }

        @Override public void onProcess() {

        }
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void uploadPublicKey(File publicKey, final InitListener listener) {
    uploadPublicKey(publicKey, new CommonAListener() {
      @Override public void onProcess() {
        listener.onUploadKey();
      }

      @Override public void onSuccess() {
        listener.onFinishInit();
      }

      @Override public void onFailed(String message) {
        listener.onError(message);
      }
    });
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

  @Override public File getPublicKey() {
    return null;
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

  @Override public void logout() {
    apiTask.logout();
  }

  /*
  @Override public void createUserCertificate(CommonListener listener) {
    try {
      localTask.createUserCertificate(listener);
    } catch (Exception e) {
      Throwable e1 = e.fillInStackTrace();
      Timber.e(e1, "Ooops! An exception happened");
    }
  }

  @Override public void createSignature(Uri uri, CommonListener listener) {
    localTask.createSignature(uri, listener);
  }
  */

  @Override public void getRootCertificate(File fileroot, File privkey, CommonAListener listener) {
    apiTask.getRootCertificate(fileroot, privkey, listener);
  }

  @Override public void uploadPublicKey(File publickey, CommonAListener listener) {
    apiTask.uploadPublicKey(publickey, listener);
  }
}
