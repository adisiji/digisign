package nb.scode.digisign.view;

import android.support.annotation.UiThread;

@UiThread public interface TakePhotoView {

  String getPhotoPath();

  String getFileName();

  void dispatchTakePictureIntent();

  void sendFile(String photoPath, String fieleName);
}