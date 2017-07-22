package nb.scode.digisign.interactor;

import android.support.annotation.NonNull;
import java.io.File;

public interface ReceivedDocInteractor extends BaseInteractor {

  void unzipFile(CommonRListener listener);

  void setFileType(String type);

  void downloadFile(String link, CommonRListener listener);

  void checkingFiles(CommonRListener listener);

  void downloadPublicKey(String senderkey, CommonRListener listener);

  void showingPdf(showPdfListener listener);

  @NonNull String getOriFileName();

  @NonNull String getOriFileSize();

  void verifySign(CommonRListener listener);

  interface showPdfListener {
    void onProcess();

    void onSuccess(File file);

    void onFailed(String message);
  }

  interface CommonRListener {
    void onProcess();

    void onSuccess();

    void onFailed(String message);
  }
}