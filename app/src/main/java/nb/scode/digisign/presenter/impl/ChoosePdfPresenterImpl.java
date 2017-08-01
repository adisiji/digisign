package nb.scode.digisign.presenter.impl;

import android.support.annotation.NonNull;
import java.io.File;
import javax.inject.Inject;
import nb.scode.digisign.interactor.ChoosePdfInteractor;
import nb.scode.digisign.presenter.ChoosePdfPresenter;
import nb.scode.digisign.view.ChoosePdfView;

public final class ChoosePdfPresenterImpl extends BasePresenterImpl<ChoosePdfView>
    implements ChoosePdfPresenter {
  /**
   * The interactor
   */
  @NonNull private final ChoosePdfInteractor mInteractor;

  // The view is available using the mView variable

  @Inject public ChoosePdfPresenterImpl(@NonNull ChoosePdfInteractor interactor) {
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

  @Override public void getFilePdf(String uripdf) {
    mInteractor.getFilePdf(uripdf, new ChoosePdfInteractor.GetPdfListener() {
      @Override public void onComplete(File file) {
        mView.setPdfRenderer(file);
      }

      @Override public void onGoing() {

      }

      @Override public void onError(String message) {

      }

      @Override public void setFileName(String fileName) {
        mView.setFileName(fileName);
      }

      @Override public void setFileSize(String fileSize) {
        mView.setFileSize(fileSize);
      }
    });
  }
}