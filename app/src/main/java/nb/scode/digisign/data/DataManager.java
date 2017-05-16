package nb.scode.digisign.data;

import android.net.Uri;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import java.security.cert.X509Certificate;
import javax.inject.Inject;
import javax.inject.Singleton;
import nb.scode.digisign.data.local.LocalTask;
import nb.scode.digisign.data.remote.ApiTask;
import timber.log.Timber;

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

  @Override public void checkKeyStore(final CertCheckListener listener) {
    listener.onProgress();
    if (isKeyStoreExist()) {
      listener.onSuccess();
    } else {
      getRootCertificate(new CommonAListener() {
        @Override public void onProcess() {

        }

        @Override public void onSuccess() {
          createUserCertificate(new CommonListener() {
            @Override public void onFinished() {
              listener.onSuccess();
            }

            @Override public void onError(String message) {
              listener.onFailed(message);
            }

            @Override public void onProcess() {

            }
          });
        }

        @Override public void onFailed(String message) {
          listener.onFailed(message);
        }
      });
    }
  }

  @Override public boolean isKeyStoreExist() {
    return localTask.isKeyStoreExist();
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

  @Override public X509Certificate getRootCertificate() {
    return localTask.getRootCertificate();
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

  /**
   * Get Root Certificate from Server
   *
   * @param listener to Connect here
   */
  @Override public void getRootCertificate(CommonAListener listener) {
    apiTask.getRootCertificate(listener);
  }

  @Override public void createUserCertificate(CommonListener listener) {
    try {
      localTask.createUserCertificate(listener);
    } catch (Exception e) {
      Timber.e("createUserCertificate(): " + e.toString());
    }
  }

  @Override public void createSignature(Uri uri, CommonListener listener) {
    localTask.createSignature(uri, listener);
  }

  /*
  @Override public void createRootCert() {
    localTask.createRootCert();
  }
  */
}
