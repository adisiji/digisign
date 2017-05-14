package nb.scode.digisign.interactor;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public interface LoginInteractor extends BaseInteractor {

  void authWithGoogle(GoogleSignInAccount account, CommonDListener listener);

  boolean isUserSignedIn();

  void login(String email, String pass, CommonDListener listener);

}