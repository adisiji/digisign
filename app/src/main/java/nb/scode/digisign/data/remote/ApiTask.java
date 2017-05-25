package nb.scode.digisign.data.remote;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import java.io.File;
import java.util.List;
import nb.scode.digisign.data.remote.BusModel.UserBusPost;
import nb.scode.digisign.data.remote.FireModel.KeyUser;
import nb.scode.digisign.data.remote.FireModel.Post;

/**
 * Created by neobyte on 4/28/2017.
 */
public interface ApiTask {

  public static final String PUBLIC_KEY = "pubkey.pbk";
  public static final String PRIVATE_KEY = "privkey.pvk";

  void register(String email, String pass, CommonAListener listener);

  void login(String email, String pass, CommonAListener listener);

  void checkRemoteKeyPair(CommonAListener listener);

  void firebaseAuthWithGoogle(GoogleSignInAccount account, CommonAListener aListener);

  void saveToken(String token);

  boolean isUserSignedIn();

  UserBusPost getUserProfile();

  String getEmailUser();

  void logout();

  void getUserPost();

  KeyUser getOwnerKey();

  List<KeyUser> getListUser();

  void getPostReceived(GetListPostListener listener);

  void getPostSent(GetListPostListener listener);

  void getAllPost(GetListPostListener listener);

  void downloadKeyPair(File publickey, File privatekey, CommonAListener listener);

  void uploadKeyPair(File publickey, File privatekey, CommonAListener listener);

  void uploadSignFile(File signFile, UploadSignListener listener);

  void insertPostData(Post postData, CommonAListener listener);

  void downloadFile(File filezip, String url, CommonAListener listener);

  void downloadPublicKey(File filepub, String senderkey, CommonAListener listener);

  void initListUser(CommonAListener listener);

  interface CommonAListener {
    void onProcess();

    void onSuccess();

    void onFailed(String message);
  }

  interface UploadSignListener {
    void onProcess();

    void onSuccess(String uri);

    void onFailed(String message);
  }

  interface GetListPostListener {
    void onProcess();

    void onSuccess(List<Post> postList);

    void onFailed(String message);
  }
}