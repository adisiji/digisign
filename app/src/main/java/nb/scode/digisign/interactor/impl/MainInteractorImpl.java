package nb.scode.digisign.interactor.impl;

import javax.inject.Inject;
import nb.scode.digisign.data.DataTask;
import nb.scode.digisign.data.remote.ApiTask;
import nb.scode.digisign.interactor.MainInteractor;

public final class MainInteractorImpl implements MainInteractor {

  private final DataTask dataTask;

  @Inject public MainInteractorImpl(DataTask dataTask) {
    this.dataTask = dataTask;
  }

  @Override public void getPhotoUri() {
    dataTask.getUserProfile();
  }

  @Override public void logout() {
    dataTask.logout();
  }

  @Override public boolean isRecentEmailSame() {
    return dataTask.isRecentEmailSame();
  }

  @Override public void downloadKeyPair(final MainListener listener) {
    dataTask.downloadKeyPair(new DataTask.DataListener() {
      @Override public void onSuccess() {
        listener.onSuccess();
      }

      @Override public void onProcess() {
        listener.onProcess();
      }

      @Override public void onFailed(String message) {
        listener.onFailed(message);
      }
    });
  }

  @Override public void setRecentEmail() {
    dataTask.setRecentEmail();
  }

  @Override public boolean isKeyPairAvailable() {
    return dataTask.isLocalKeyPairAvailable();
  }

  @Override public void isRemoteKeyPairAvail(final MainListener listener) {
    dataTask.checkRemoteKeyPair(new ApiTask.CommonAListener() {
      @Override public void onProcess() {
        listener.onProcess();
      }

      @Override public void onSuccess() {
        listener.onSuccess();
      }

      @Override public void onFailed(String message) {
        listener.onFailed(message);
      }
    });
  }

  @Override public void initKeyPair(final InitCListener listener) {
    try {
      dataTask.initKeyPair(new DataTask.InitListener() {
        @Override public void onStartInit() {
          listener.onStartInit();
        }

        @Override public void onCreateKey() {
          listener.onCreateKey();
        }

        @Override public void onUploadKey() {
          listener.onUploadKey();
        }

        @Override public void onFinishInit() {
          listener.onFinishInit();
        }

        @Override public void onError(String message) {
          listener.onError(message);
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override public void uploadKeyPair(final MainListener listener) {
    dataTask.uploadKeyPair(new DataTask.DataListener() {
      @Override public void onSuccess() {
        listener.onSuccess();
      }

      @Override public void onProcess() {
        listener.onProcess();
      }

      @Override public void onFailed(String message) {
        listener.onFailed(message);
      }
    });
  }

  /*
  @Override public void createRootCert() {
    dataTask.createRootCert();
  }
  */
}