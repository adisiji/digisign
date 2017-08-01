package nb.scode.digisign.presenter.impl;

import android.support.annotation.NonNull;
import javax.inject.Inject;
import nb.scode.digisign.interactor.TakePhotoInteractor;
import nb.scode.digisign.presenter.TakePhotoPresenter;
import nb.scode.digisign.view.TakePhotoView;

public final class TakePhotoPresenterImpl extends BasePresenterImpl<TakePhotoView>
    implements TakePhotoPresenter {
  /**
   * The interactor
   */
  @NonNull private final TakePhotoInteractor mInteractor;

  // The view is available using the mView variable

  @Inject public TakePhotoPresenterImpl(@NonNull TakePhotoInteractor interactor) {
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

  @Override public void addSigner() {
    if (mView != null) {
      String photoPath = mView.getPhotoPath();
      String fileName = mView.getFileName();
      if (fileName.length() == 0) {
        fileName = "Image.jpg";
      } else if (!fileName.contains(".jpg")) {
        fileName = fileName + ".jpg";
      }
      mView.sendFile(photoPath, fileName);
    }
  }
}