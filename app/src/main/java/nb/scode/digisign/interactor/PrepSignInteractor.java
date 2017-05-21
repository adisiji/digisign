package nb.scode.digisign.interactor;

import java.io.File;

public interface PrepSignInteractor extends BaseInteractor {

  void getFilePdf(String uripdf, GetPdfListener listener);

  interface GetPdfListener {
    void setFileName(String fileName);

    void setFileSize(String fileSize);

    void onComplete(File file);

    void onGoing();

    void onError(String message);
  }
}