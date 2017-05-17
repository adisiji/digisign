package nb.scode.digisign.data.remote;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import java.io.File;

/**
 * Created by neobyte on 4/28/2017.
 */
public interface ApiTask {

  void register(String email, String pass, CommonAListener listener);

  void login(String email, String pass, CommonAListener listener);

  void firebaseAuthWithGoogle(GoogleSignInAccount account, CommonAListener aListener);

  boolean isUserSignedIn();

  void getPhotoUri();

  void logout();

  void getRootCertificate(File file, CommonAListener listener);

  interface CommonAListener {
    void onProcess();

    void onSuccess();

    void onFailed(String message);
  }
}
