package nb.scode.digisign.interactor.impl;

import android.support.annotation.NonNull;
import java.util.List;
import javax.inject.Inject;
import nb.scode.digisign.data.DataTask;
import nb.scode.digisign.data.local.LocalTask;
import nb.scode.digisign.data.remote.FireModel.KeyUser;
import nb.scode.digisign.interactor.AddSignerInteractor;
import timber.log.Timber;

public final class AddSignerInteractorImpl implements AddSignerInteractor {

  private final DataTask dataTask;

  @Inject public AddSignerInteractorImpl(@NonNull DataTask dataTask) {
    this.dataTask = dataTask;
  }

  @Override public List<KeyUser> getListUser() {
    return dataTask.getListUser();
  }

  @Override public KeyUser getOwnerKey() {
    return dataTask.getOwnerKey();
  }

  @Override public void createSignFile(String uripdf, final CommonIListener listener) {

    dataTask.createSignFile(uripdf, new LocalTask.CommonListener() {
      @Override public void onFinished() {
        dataTask.createZip(new LocalTask.CommonListener() {
          @Override public void onFinished() {
            listener.onSuccess();
          }

          @Override public void onError(String message) {
            listener.onFailed(message);
          }

          @Override public void onProcess() {
            Timber.d("onProcess(): zipp");
          }
        });
      }

      @Override public void onError(String message) {
        listener.onFailed(message);
      }

      @Override public void onProcess() {
        listener.onProcess();
      }
    });

  }
}