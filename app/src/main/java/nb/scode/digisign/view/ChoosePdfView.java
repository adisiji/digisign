package nb.scode.digisign.view;

import android.support.annotation.UiThread;
import java.io.File;

@UiThread public interface ChoosePdfView {

  void setPdfRenderer(File file);

  void setFileName(String fileName);

  void setFileSize(String fileSize);

  void setEnableBtnReceiver();

  void setDisableBtnReceiver();
}