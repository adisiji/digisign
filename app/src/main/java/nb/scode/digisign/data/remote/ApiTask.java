package nb.scode.digisign.data.remote;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import java.io.File;
import java.util.List;
import nb.scode.digisign.data.remote.FireModel.ListUid;

/**
 * Created by neobyte on 4/28/2017.
 */
public interface ApiTask {

  void register(String email, String pass, CommonAListener listener);

  void login(String email, String pass, CommonAListener listener);

  void checkRemoteKeyPair(CommonAListener listener);

  void firebaseAuthWithGoogle(GoogleSignInAccount account, CommonAListener aListener);

  boolean isUserSignedIn();

  void getUserProfile();

  String getEmailUser();

  void logout();

  void getUserPost();

  List<ListUid> getListUid();

  void downloadKeyPair(File publickey, File privatekey, CommonAListener listener);

  void uploadKeyPair(File publickey, File privatekey, CommonAListener listener);

  interface CommonAListener {
    void onProcess();

    void onSuccess();

    void onFailed(String message);
  }
}