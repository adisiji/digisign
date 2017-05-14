package nb.scode.digisign.presenter;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import nb.scode.digisign.view.LoginView;

public interface LoginPresenter extends BasePresenter<LoginView> {

  void login();

  void firebaseAuthWithGoogle(GoogleSignInAccount account);

  void checkUserSignedIn();
}