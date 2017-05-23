package nb.scode.digisign.data.local;

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

  boolean isEmailSame(String email);

  void setEmailPref(String email);

  boolean isLocalKeyPairAvailable();

  void createKey(CommonListener listener) throws Exception;

  File getPublicKey();

  File getPrivateKey();

  void getPrepFilePdf(String uripdf, ListenerPrepPdf listenerPrepPdf);

  void createZip(CommonListener listener);

  File getFileToSend();

  void unZipFile(File zipfile, File targetDir, CommonListener listener);

  void createSignFile(String uripdf, CommonListener listener);

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
