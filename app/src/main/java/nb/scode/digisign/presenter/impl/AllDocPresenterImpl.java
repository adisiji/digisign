package nb.scode.digisign.presenter.impl;

import android.support.annotation.NonNull;
import javax.inject.Inject;
import nb.scode.digisign.interactor.AllDocInteractor;
import nb.scode.digisign.presenter.AllDocPresenter;
import nb.scode.digisign.view.AllDocView;
import nb.scode.digisign.view.adapter.AllDocAdapter;
import nb.scode.digisign.view.model.ItemAllDoc;
import timber.log.Timber;

import static nb.scode.digisign.view.impl.Constants.MENU_ALL;
import static nb.scode.digisign.view.impl.Constants.MENU_RECEIVE;
import static nb.scode.digisign.view.impl.Constants.MENU_SENT;

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
    if (viewCreated) {
      if (mView != null) {
        mView.setupRecycler();
        getAllPost();
      }
    }
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

  @Override public void processSpinnerMenu(int menuId) {
    switch (menuId) {
      case MENU_ALL:
        getAllPost();
        break;
      case MENU_SENT:
        getSentPost();
        break;
      case MENU_RECEIVE:
        getReceivedPost();
        break;
    }
  }

  @Override public void getSentPost() {
    mInteractor.getSentPost(new AllDocInteractor.AllDocIntListener() {
      @Override public void onProcess() {
        mView.showLoading();
      }

      @Override public void onSuccess() {
        Timber.d("onSuccess(): get sent post");
        if (mView != null) {
          mView.notifyAdapter();
          mView.hideLoading();
        }
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

      @Override public void onSuccess() {
        if (mView != null) {
          mView.notifyAdapter();
          mView.hideLoading();
        }
      }

      @Override public void onFailed(String message) {
        if (mView != null) {
          mView.hideLoading();
        }
      }
    });
  }

  @Override public void getAllPost() {
    mInteractor.getAllPost(new AllDocInteractor.AllDocIntListener() {
      @Override public void onProcess() {
        mView.showLoading();
      }

      @Override public void onSuccess() {
        if (mView != null) {
          mView.notifyAdapter();
          mView.hideLoading();
        }
      }

      @Override public void onFailed(String message) {
        if (mView != null) {
          mView.hideLoading();
        }
      }
    });
  }

  @Override public int getPostSize() {
    return mInteractor.getPostSize();
  }

  @Override public void bindRowView(AllDocAdapter.AllDocAdpListener rowview, int position) {
    ItemAllDoc itemAllDoc = mInteractor.getItemDoc(position);
    rowview.setFileDate(itemAllDoc.getFiledate());
    rowview.setFileFrom(itemAllDoc.getFromname());
    rowview.setFileName(itemAllDoc.getFilename());
    rowview.setIvStatus(itemAllDoc.isSent());
  }
}