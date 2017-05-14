package nb.scode.digisign.presenter.impl;

import android.support.annotation.NonNull;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import javax.inject.Inject;
import nb.scode.digisign.interactor.CommonDListener;
import nb.scode.digisign.interactor.LoginInteractor;
import nb.scode.digisign.presenter.LoginPresenter;
import nb.scode.digisign.view.LoginView;

public final class LoginPresenterImpl extends BasePresenterImpl<LoginView>
    implements LoginPresenter {
  /**
   * The interactor
   */
  @NonNull private final LoginInteractor mInteractor;

  // The view is available using the mView variable

  @Inject public LoginPresenterImpl(@NonNull LoginInteractor interactor) {
    mInteractor = interactor;
  }

  @Override public void onStart(boolean viewCreated) {
    super.onStart(viewCreated);

    // Your code here. Your view is available using mView and will not be null until next onStop()
  }

  @Override public void onStop() {
    // Your code here, mView will be null after this method until next onStart()

    super.onStop();
  }

  @Override public void onPresenterDestroyed() {
        /*
         * Your code here. After this method, your presenter (and view) will be completely destroyed
         * so make sure to cancel any HTTP call or database connection
         */

    super.onPresenterDestroyed();
  }

  @Override public void login() {
    mInteractor.login(mView.getEmail(), mView.getPassword(), new CommonDListener() {
      @Override public void onProccess() {
        mView.showProgress();
      }

      @Override public void onSuccess() {
        mView.hideProgress();
        mView.gotoMain();
      }

      @Override public void onFailed(String message) {
        mView.hideProgress();
        mView.showToast(message);
      }
    });
  }

  @Override public void firebaseAuthWithGoogle(GoogleSignInAccount account) {
    mInteractor.authWithGoogle(account, new CommonDListener() {
      @Override public void onProccess() {
        mView.showProgress();
      }

      @Override public void onSuccess() {
        mView.gotoMain();
      }

      @Override public void onFailed(String message) {
        mView.hideProgress();
        mView.showToast(message);
      }
    });
  }

  @Override public void checkUserSignedIn() {
    if (mInteractor.isUserSignedIn()) {
      mView.gotoMain();
    }
  }
}