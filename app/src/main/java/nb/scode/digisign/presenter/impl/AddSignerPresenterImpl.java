package nb.scode.digisign.presenter.impl;

import android.support.annotation.NonNull;
import javax.inject.Inject;
import nb.scode.digisign.interactor.AddSignerInteractor;
import nb.scode.digisign.presenter.AddSignerPresenter;
import nb.scode.digisign.view.AddSignerView;

public final class AddSignerPresenterImpl extends BasePresenterImpl<AddSignerView>
    implements AddSignerPresenter {
  /**
   * The interactor
   */
  @NonNull private final AddSignerInteractor mInteractor;

  // The view is available using the mView variable

  @Inject public AddSignerPresenterImpl(@NonNull AddSignerInteractor interactor) {
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