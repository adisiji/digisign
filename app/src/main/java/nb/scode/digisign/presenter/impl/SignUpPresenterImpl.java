package nb.scode.digisign.presenter.impl;

import android.support.annotation.NonNull;
import javax.inject.Inject;
import nb.scode.digisign.interactor.CommonDListener;
import nb.scode.digisign.interactor.SignUpInteractor;
import nb.scode.digisign.presenter.SignUpPresenter;
import nb.scode.digisign.view.SignUpView;

public final class SignUpPresenterImpl extends BasePresenterImpl<SignUpView>
    implements SignUpPresenter {
  /**
   * The interactor
   */
  @NonNull private final SignUpInteractor mInteractor;

  // The view is available using the mView variable

  @Inject public SignUpPresenterImpl(@NonNull SignUpInteractor interactor) {
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

  @Override public void register() {
    String email = mView.getEmail();
    String pass = mView.getPassword();

    mInteractor.register(email, pass, new CommonDListener() {
      @Override public void onProccess() {
        mView.showProgress();
      }

      @Override public void onSuccess() {
        mView.hideProgress();
        mView.showToast("Registration Success! Email verification has been sent to your email");
      }

      @Override public void onFailed(String message) {
        mView.hideProgress();
        mView.showToast(message);
      }
    });
  }
}