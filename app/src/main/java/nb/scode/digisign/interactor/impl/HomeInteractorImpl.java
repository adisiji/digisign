package nb.scode.digisign.interactor.impl;

import javax.inject.Inject;
import nb.scode.digisign.data.DataTask;
import nb.scode.digisign.interactor.HomeInteractor;

public final class HomeInteractorImpl implements HomeInteractor {
  private final DataTask dataTask;

  @Inject public HomeInteractorImpl(DataTask dataTask) {
    this.dataTask = dataTask;
  }

}