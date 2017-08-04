package nb.scode.digisign.view;

import android.support.annotation.UiThread;

@UiThread public interface SettingsView {

  void showToast(String message);

  void setCacheSize(String cacheSize);

  void setPhotoSize(String photoSize);

}