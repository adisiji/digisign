package nb.scode.digisign.interactor.impl;

import android.support.annotation.NonNull;
import java.util.List;
import javax.inject.Inject;
import nb.scode.digisign.data.DataTask;
import nb.scode.digisign.data.remote.FireModel.KeyUser;
import nb.scode.digisign.interactor.AddSignerInteractor;

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
}