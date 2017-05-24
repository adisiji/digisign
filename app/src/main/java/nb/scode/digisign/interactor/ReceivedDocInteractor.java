package nb.scode.digisign.interactor;

public interface ReceivedDocInteractor extends BaseInteractor {

  void unzipFile(CommonRListener listener);

  void setFileType(String type);

  void downloadFile(String link, CommonRListener listener);

  void checkingFiles(CommonRListener listener);

  void downloadPublicKey(String senderkey, CommonRListener listener);

  void verifySign(CommonRListener listener);

  interface CommonRListener {
    void onProcess();

    void onSuccess();

    void onFailed(String message);
  }
}