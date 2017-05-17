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

  @Override public void initKeyCert(final InitListener listener) {
    dataTask.initKeyCert(new DataTask.InitListener() {
      @Override public void onStartInit() {
        listener.onStartInit();
      }

      @Override public void onGetRootCert() {
        listener.onGetRootCert();
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
    });
  }

  /*
  @Override public void createRootCert() {
    dataTask.createRootCert();
  }
  */
}