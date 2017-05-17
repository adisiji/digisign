package nb.scode.digisign.interactor;

public interface MainInteractor extends BaseInteractor {

  void getPhotoUri();

  void logout();

  void initKeyCert(InitListener listener);

  interface InitListener {

    void onStartInit();

    void onGetRootCert();

    void onCreateKey();

    void onUploadKey();

    void onFinishInit();
  }

  //void createRootCert();

}