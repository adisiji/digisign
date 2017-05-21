package nb.scode.digisign.data.remote;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import java.io.File;
import java.util.Map;
import nb.scode.digisign.data.remote.BusModel.UserBusPost;
import nb.scode.digisign.data.remote.FireModel.User;

/**
 * Created by neobyte on 4/28/2017.
 */
public interface ApiTask {

  void register(String email, String pass, CommonAListener listener);

  void login(String email, String pass, CommonAListener listener);

  void checkRemoteKeyPair(CommonAListener listener);

  void firebaseAuthWithGoogle(GoogleSignInAccount account, CommonAListener aListener);

  boolean isUserSignedIn();

  UserBusPost getUserProfile();

  String getEmailUser();

  void logout();

  void getUserPost();

  Map<String, User> getListUser();

  void downloadKeyPair(File publickey, File privatekey, CommonAListener listener);

  void uploadKeyPair(File publickey, File privatekey, CommonAListener listener);

  interface CommonAListener {
    void onProcess();

    void onSuccess();

    void onFailed(String message);
  }
}