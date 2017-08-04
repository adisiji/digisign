package nb.scode.digisign.presenter.impl;

import android.support.annotation.NonNull;
import javax.inject.Inject;
import nb.scode.digisign.interactor.MainInteractor;
import nb.scode.digisign.presenter.MainPresenter;
import nb.scode.digisign.view.MainView;
import timber.log.Timber;

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
    if (viewCreated) {
      checkNullUser();
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

  private void checkNullUser() {
    mView.showProgressDialog("Loading...");
    if (mInteractor.isUserNull()) {
      mInteractor.getUser(new MainInteractor.MainListener() {
        @Override public void onSuccess() {
          mainStarter();
        }

        @Override public void onProcess() {

        }

        @Override public void onFailed(String message) {

        }
      });
    } else {
      mainStarter();
    }
  }

  private void mainStarter() {
    if (mInteractor.isRecentEmailSame()) {
      if (mInteractor.isKeyPairAvailable()) {
        mInteractor.isRemoteKeyPairAvail(new MainInteractor.MainListener() {
          @Override public void onSuccess() {
            initListUser();
            Timber.d("onSuccess(): Ok we can start sign after init");
          }

          @Override public void onProcess() {
            Timber.d("onProcess(): Good get RemoteKeyPair");
          }

          @Override public void onFailed(String message) {
            Timber.d("onFailed(): lets upload the keypair");
            uploadKeyPair();
          }
        });
      } else {
        downloadKeyPair();
      }
    } else {
      mInteractor.setRecentEmail();
      mInteractor.isRemoteKeyPairAvail(new MainInteractor.MainListener() {
        @Override public void onSuccess() {
          downloadKeyPair();
        }

        @Override public void onProcess() {
          Timber.d("onProcess(): getting remote KeyPair");
        }

        @Override public void onFailed(String message) {
          initKeyPair();
        }
      });
    }
  }

  @Override public void logout() {
    mInteractor.logout();
  }

  private void downloadKeyPair() {
    mInteractor.downloadKeyPair(new MainInteractor.MainListener() {
      @Override public void onSuccess() {
        initListUser();
        Timber.d("mainStarter(): finish");
      }

      @Override public void onProcess() {

      }

      @Override public void onFailed(String message) {
        showToastHideProgress(message);
      }
    });
  }

  private void uploadKeyPair() {
    mInteractor.uploadKeyPair(new MainInteractor.MainListener() {
      @Override public void onSuccess() {
        mView.hideProgressDialog();
        Timber.d("onSuccess(): Ok we can start sign");
      }

      @Override public void onProcess() {
        Timber.d("onProcess(): Process Upload KeyPair");
      }

      @Override public void onFailed(String message) {
        showToastHideProgress(message);
      }
    });
  }

  private void initKeyPair() {
    mInteractor.initKeyPair(new MainInteractor.MainInitListener() {
      @Override public void onStartInit() {
        mView.showProgressDialog("Initialize Key");
        Timber.d("onStartInit(): Good");
      }

      @Override public void onCreateKey() {
        Timber.d("onCreateKey(): Good");
      }

      @Override public void onUploadKey() {
        mView.showProgressDialog("Uploading Key");
        Timber.d("onUploadKey(): Good");
      }

      @Override public void onFinishInit() {
        initListUser();
        Timber.d("onFinishInit(): Good");
      }

      @Override public void onError(String message) {
        showToastHideProgress(message);
        Timber.d("onError(): GOOO");
      }
    });
  }

  private void initListUser() {
    mInteractor.initListUser(new MainInteractor.MainListener() {
      @Override public void onSuccess() {
        mInteractor.getUser();
        showToastHideProgress("You can sign document now");
      }

      @Override public void onProcess() {

      }

      @Override public void onFailed(String message) {
        showToastHideProgress(message);
        Timber.d("onError(): GOOO");
      }
    });
  }

  private void showToastHideProgress(String message) {
    mView.hideProgressDialog();
    mView.showToast(message);
  }

  @Override public void sendTokenToServer(String token) {
    mInteractor.saveToken(token);
  }
}