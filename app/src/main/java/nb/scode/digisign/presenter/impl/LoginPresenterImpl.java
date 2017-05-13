package nb.scode.digisign.presenter.impl;

import android.support.annotation.NonNull;
import javax.inject.Inject;
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
}