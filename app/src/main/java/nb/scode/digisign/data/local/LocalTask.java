package nb.scode.digisign.data.local;

import android.net.Uri;
import java.io.File;

/**
 * Created by neobyte on 4/28/2017.
 * ********************************
 * PreferencesTask define the tasks for SharedPreferences
 */
public interface LocalTask {

  int USER = 1;

  int ROOT = 0;

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

  boolean isKeyPairAvailable();

  void createKey(CommonListener listener) throws Exception;

  File getPublicKey();

  void getPrepFilePdf(Uri uri, ListenerPrepPdf listenerPrepPdf);
  /*
  X509Certificate getCertificate(int owner);

  void createUserCertificate(CommonListener listener) throws Exception;

  boolean isKeyStoreExist();

  void createSignature(Uri uri, CommonListener listener);

  File getCertRootDest();

  File getCertPrivKeyDest();

  void createRootCert();
  */

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
