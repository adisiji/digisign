package nb.scode.digisign.view;

import android.support.annotation.UiThread;
import java.io.File;

@UiThread public interface ReceivedDocView {

  boolean isIntentEmpty();

  void showLoading();

  void hideLoading();

  void showDialog(String title, String body);

  String getDescIntent();

  String getDownloadLink();

  String getFromIntent();

  String getSenderKeyIntent();

  String getTimeIntent();

  String getTypeIntent();

  void setGreenStatus();

  void setRedStatus();

  void setFileStatus(String fileStatus);

  void setPdfRenderer(File file);

  void setImageRenderer(File file);
}