package nb.scode.digisign.interactor.impl;

import javax.inject.Inject;
import nb.scode.digisign.data.DataTask;
import nb.scode.digisign.interactor.MainInteractor;

public final class MainInteractorImpl implements MainInteractor {

  private final DataTask dataTask;

  @Inject public MainInteractorImpl(DataTask dataTask) {
    this.dataTask = dataTask;
  }

  @Override public void getPhotoUri() {
    dataTask.getPhotoUri();
  }

  @Override public void logout() {
    dataTask.logout();
  }

  @Override public boolean isKeyPairAvailable() {
    return dataTask.isKeyPairAvailable();
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

  /*
  @Override public void createRootCert() {
    dataTask.createRootCert();
  }
  */
}