package nb.scode.digisign.interactor.impl;

import javax.inject.Inject;
import nb.scode.digisign.data.DataTask;
import nb.scode.digisign.data.remote.ApiTask;
import nb.scode.digisign.interactor.CommonDListener;
import nb.scode.digisign.interactor.SignUpInteractor;

public final class SignUpInteractorImpl implements SignUpInteractor {
  private final DataTask dataTask;

  @Inject public SignUpInteractorImpl(DataTask dataTask) {
    this.dataTask = dataTask;
  }

  @Override public void register(String email, String pass, final CommonDListener dInteractor) {
    dataTask.register(email, pass, new ApiTask.CommonAListener() {
      @Override public void onProcess() {
        dInteractor.onProccess();
      }

      @Override public void onSuccess() {
        dInteractor.onSuccess();
      }

      @Override public void onFailed(String message) {
        dInteractor.onFailed(message);
      }
    });
  }
}