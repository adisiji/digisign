package nb.scode.digisign.interactor.impl;

import android.support.annotation.NonNull;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import javax.inject.Inject;
import nb.scode.digisign.data.DataTask;
import nb.scode.digisign.data.remote.ApiTask;
import nb.scode.digisign.interactor.CommonDListener;
import nb.scode.digisign.interactor.LoginInteractor;

public final class LoginInteractorImpl implements LoginInteractor {
  private final DataTask dataTask;

  @Inject public LoginInteractorImpl(DataTask dataTask) {
    this.dataTask = dataTask;
  }

  @Override public void authWithGoogle(final GoogleSignInAccount account,
      @NonNull final CommonDListener listener) {
    dataTask.firebaseAuthWithGoogle(account, new ApiTask.CommonAListener() {
      @Override public void onProcess() {
        listener.onProccess();
      }

      @Override public void onSuccess() {
        dataTask.setUserToken(account.getIdToken());
        listener.onSuccess();
      }

      @Override public void onFailed(String message) {
        listener.onFailed(message);
      }
    });
  }

  @Override public boolean isUserSignedIn() {
    return dataTask.isUserSignedIn();
  }

  @Override public void login(String email, String pass, @NonNull final CommonDListener listener) {
    dataTask.login(email, pass, new ApiTask.CommonAListener() {
      @Override public void onProcess() {
        listener.onProccess();
      }

      @Override public void onSuccess() {
        listener.onSuccess();
      }

      @Override public void onFailed(String message) {
        listener.onFailed(message);
      }
    });
  }
}