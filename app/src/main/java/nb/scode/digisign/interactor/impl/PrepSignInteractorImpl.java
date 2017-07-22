package nb.scode.digisign.interactor.impl;

import android.support.annotation.NonNull;
import java.io.File;
import javax.inject.Inject;
import nb.scode.digisign.data.DataTask;
import nb.scode.digisign.data.local.LocalTask;
import nb.scode.digisign.interactor.PrepSignInteractor;

public final class PrepSignInteractorImpl implements PrepSignInteractor {

  private final DataTask dataTask;

  @Inject public PrepSignInteractorImpl(DataTask dataTask) {
    this.dataTask = dataTask;
  }

  @Override public void getFilePdf(String uripdf, @NonNull final GetPdfListener listener) {
    dataTask.getPrepFilePdf(uripdf, new LocalTask.ListenerPrepPdf() {
      @Override public void onComplete(File file) {
        listener.onComplete(file);
      }

      @Override public void onGoing() {
        listener.onGoing();
      }

      @Override public void onError(String message) {
        listener.onError(message);
      }

      @Override public void setFileName(String fileName) {
        listener.setFileName(fileName);
      }

      @Override public void setFileSize(String fileSize) {
        listener.setFileSize(fileSize);
      }
    });
  }
}