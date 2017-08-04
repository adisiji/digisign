package nb.scode.digisign.interactor.impl;

import javax.inject.Inject;
import nb.scode.digisign.data.DataTask;
import nb.scode.digisign.interactor.SettingsInteractor;

public final class SettingsInteractorImpl implements SettingsInteractor {

  private final DataTask dataTask;

  @Inject public SettingsInteractorImpl(DataTask dataTask) {
    this.dataTask = dataTask;
  }

  @Override public String getCacheFolderSize() {
    return dataTask.getCacheFolderSize();
  }

  @Override public void clearCache() {
    dataTask.clearCacheFolder();
  }
}