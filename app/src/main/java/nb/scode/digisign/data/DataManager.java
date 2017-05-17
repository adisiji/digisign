package nb.scode.digisign.data;

import android.net.Uri;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import java.io.File;
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

  @Override public void initKeyCert(final InitListener listener) {
    listener.onStartInit();
    if (localTask.isKeyStoreExist()) {
      listener.onFinishInit();
    } else {
      if (getRootCertificate() == null) {
        getRootCertificate(getCertRootDest(), new CommonAListener() {
          @Override public void onProcess() {
            listener.onGetRootCert();
          }

          @Override public void onSuccess() {
            createUserCertificate(listener);
          }

          @Override public void onFailed(String message) {

          }
        });
      } else {
        createUserCertificate(listener);
      }
    }
  }

  private void createUserCertificate(final InitListener listener) {
    createUserCertificate(new CommonListener() {
      @Override public void onFinished() {
        listener.onFinishInit();
      }

      @Override public void onError(String message) {

      }

      @Override public void onProcess() {

      }
    });
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

  @Override public void getRootCertificate(File file, CommonAListener listener) {
    apiTask.getRootCertificate(file, listener);
  }

  @Override public File getCertRootDest() {
    return localTask.getCertRootDest();
  }

  /*
  @Override public void createRootCert() {
    localTask.createRootCert();
  }
  */
}
