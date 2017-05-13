package nb.scode.digisign.interactor;

public interface HomeInteractor extends BaseInteractor {

  void getCert(GetCertListener listener);

  interface GetCertListener {
    void onProcess();

    void onFinished();

    void onError(String message);
  }

}