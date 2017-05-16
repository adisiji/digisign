package nb.scode.digisign.presenter.impl;

import android.support.annotation.NonNull;
import javax.inject.Inject;
import nb.scode.digisign.interactor.MainInteractor;
import nb.scode.digisign.presenter.MainPresenter;
import nb.scode.digisign.view.MainView;

public final class MainPresenterImpl extends BasePresenterImpl<MainView> implements MainPresenter {
  /**
   * The interactor
   */
  @NonNull private final MainInteractor mInteractor;

  // The view is available using the mView variable

  @Inject public MainPresenterImpl(@NonNull MainInteractor interactor) {
    mInteractor = interactor;
  }

  @Override public void onStart(boolean viewCreated) {
    super.onStart(viewCreated);
    //mInteractor.createRootCert();
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

  @Override public void getPhotoUri() {
    mInteractor.getPhotoUri();
  }

  @Override public void logout() {
    mInteractor.logout();
  }
}