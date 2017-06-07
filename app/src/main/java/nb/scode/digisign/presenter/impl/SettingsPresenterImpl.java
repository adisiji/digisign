package nb.scode.digisign.presenter.impl;

import android.support.annotation.NonNull;
import javax.inject.Inject;
import nb.scode.digisign.interactor.SettingsInteractor;
import nb.scode.digisign.presenter.SettingsPresenter;
import nb.scode.digisign.view.SettingsView;

public final class SettingsPresenterImpl extends BasePresenterImpl<SettingsView>
    implements SettingsPresenter {
  /**
   * The interactor
   */
  @NonNull private final SettingsInteractor mInteractor;

  // The view is available using the mView variable

  @Inject public SettingsPresenterImpl(@NonNull SettingsInteractor interactor) {
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