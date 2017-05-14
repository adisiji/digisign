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
}