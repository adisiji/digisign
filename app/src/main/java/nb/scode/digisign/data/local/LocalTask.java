package nb.scode.digisign.data.local;

import android.net.Uri;
import java.io.File;
import java.security.cert.X509Certificate;

/**
 * Created by neobyte on 4/28/2017.
 * ********************************
 * PreferencesTask define the tasks for SharedPreferences
 */
public interface LocalTask {
  /**
   * Clear.
   */
  void clear();

  /**
   * Is first use boolean.
   *
   * @return the boolean
   */
  boolean isFirstUse();

  /**
   * Sets not first use.
   */
  void setNotFirstUse();

  X509Certificate getRootCertificate();

  void getPrepFilePdf(Uri uri, ListenerPrepPdf listenerPrepPdf);

  void createUserCertificate(CommonListener listener) throws Exception;

  boolean isKeyStoreExist();

  void createSignature(Uri uri, CommonListener listener);

  //void createRootCert();

  interface ListenerPrepPdf {
    void setFileName(String fileName);

    void setFileSize(String fileSize);

    void onComplete(File file);

    void onGoing();

    void onError(String message);
  }

  interface CommonListener {
    void onFinished();

    void onError(String message);

    void onProcess();
  }
}
