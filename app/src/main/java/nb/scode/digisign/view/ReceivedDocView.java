package nb.scode.digisign.view;

import android.support.annotation.UiThread;
import java.io.File;

@UiThread public interface ReceivedDocView {

  boolean isIntentEmpty();

  void showLoading();

  void hideLoading();

  String getDescIntent();

  String getDownloadLink();

  String getFromIntent();

  String getSenderKeyIntent();

  String getTimeIntent();

  String getTypeIntent();

  void setGreenStatus();

  void setRedStatus();

  void setFileName(String name);

  void setFileSize(String fileSize);

  void setFileStatus(String fileStatus);

  void setPdfRenderer(File file);
}