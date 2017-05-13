package nb.scode.digisign.interactor.impl;

import javax.inject.Inject;
import nb.scode.digisign.data.DataTask;
import nb.scode.digisign.data.local.LocalTask;
import nb.scode.digisign.interactor.HomeInteractor;

public final class HomeInteractorImpl implements HomeInteractor {
  private final DataTask dataTask;

  @Inject public HomeInteractorImpl(DataTask dataTask) {
    this.dataTask = dataTask;
  }

  @Override public void getCert(final GetCertListener listener) {
    dataTask.getCert(new LocalTask.CommonListener() {
      @Override public void onFinished() {
        listener.onFinished();
      }

      @Override public void onError(String message) {
        listener.onError(message);
      }

      @Override public void onProcess() {
        listener.onProcess();
      }
    });
  }
}