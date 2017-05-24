package nb.scode.digisign.view;

import android.support.annotation.UiThread;

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
}