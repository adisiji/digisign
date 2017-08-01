package nb.scode.digisign.interactor.impl;

import javax.inject.Inject;
import nb.scode.digisign.data.DataTask;
import nb.scode.digisign.data.local.LocalTask;
import nb.scode.digisign.interactor.TakePhotoInteractor;
import timber.log.Timber;

public final class TakePhotoInteractorImpl implements TakePhotoInteractor {

  private final DataTask dataTask;

  @Inject public TakePhotoInteractorImpl(DataTask dataTask) {
    this.dataTask = dataTask;
  }

  @Override public void writeFileToCache(String photoPath) {
    dataTask.createCacheImage(photoPath, new LocalTask.CommonListener() {
      @Override public void onFinished() {
        Timber.d("onFinished(): finish create image cache");
      }

      @Override public void onError(String message) {
        Timber.e("onError(): " + message);
      }

      @Override public void onProcess() {

      }
    });
  }
}