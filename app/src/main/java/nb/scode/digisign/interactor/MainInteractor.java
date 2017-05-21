package nb.scode.digisign.interactor;

public interface MainInteractor extends BaseInteractor {

  void getUser();

  void logout();

  boolean isKeyPairAvailable();

  void isRemoteKeyPairAvail(MainListener listener);

  void uploadKeyPair(MainListener listener);

  boolean isRecentEmailSame();

  void downloadKeyPair(MainListener listener);

  void setRecentEmail();

  void initKeyPair(MainInitListener listener);

  interface MainListener {
    void onSuccess();

    void onProcess();

    void onFailed(String message);
  }

  interface MainInitListener {

    void onStartInit();

    void onCreateKey();

    void onUploadKey();

    void onFinishInit();

    void onError(String message);
  }

  //void createRootCert();
}