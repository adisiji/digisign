package nb.scode.digisign.interactor;

public interface MainInteractor extends BaseInteractor {

  void getPhotoUri();

  void logout();

  boolean isKeyPairAvailable();

  void initKeyPair(InitCListener listener);

  interface InitCListener {

    void onStartInit();

    void onCreateKey();

    void onUploadKey();

    void onFinishInit();

    void onError(String message);
  }

  //void createRootCert();

}