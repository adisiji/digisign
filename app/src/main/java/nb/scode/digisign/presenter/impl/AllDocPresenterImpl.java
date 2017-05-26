package nb.scode.digisign.presenter.impl;

import android.support.annotation.NonNull;
import java.util.List;
import javax.inject.Inject;
import nb.scode.digisign.interactor.AllDocInteractor;
import nb.scode.digisign.presenter.AllDocPresenter;
import nb.scode.digisign.view.AllDocView;
import nb.scode.digisign.view.model.ItemAllDoc;
import timber.log.Timber;

public final class AllDocPresenterImpl extends BasePresenterImpl<AllDocView>
    implements AllDocPresenter {
  /**
   * The interactor
   */
  @NonNull private final AllDocInteractor mInteractor;

  // The view is available using the mView variable

  @Inject public AllDocPresenterImpl(@NonNull AllDocInteractor interactor) {
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

  @Override public void getSentPost() {
    mInteractor.getSentPost(new AllDocInteractor.AllDocIntListener() {
      @Override public void onProcess() {
        mView.showLoading();
      }

      @Override public void onSuccess(List<ItemAllDoc> postList) {
        Timber.d("onSuccess(): get sent post");
        mView.showAllDocItems(postList);
        mView.hideLoading();
      }

      @Override public void onFailed(String message) {
        mView.hideLoading();
      }
    });
  }

  @Override public void getReceivedPost() {
    mInteractor.getReceivePost(new AllDocInteractor.AllDocIntListener() {
      @Override public void onProcess() {
        mView.showLoading();
      }

      @Override public void onSuccess(List<ItemAllDoc> postList) {
        mView.showAllDocItems(postList);
        mView.hideLoading();
      }

      @Override public void onFailed(String message) {
        mView.hideLoading();
      }
    });
  }

  @Override public void getAllPost() {
    mInteractor.getAllPost(new AllDocInteractor.AllDocIntListener() {
      @Override public void onProcess() {
        mView.showLoading();
      }

      @Override public void onSuccess(List<ItemAllDoc> postList) {
        mView.showAllDocItems(postList);
        mView.hideLoading();
      }

      @Override public void onFailed(String message) {
        mView.hideLoading();
      }
    });
  }
}