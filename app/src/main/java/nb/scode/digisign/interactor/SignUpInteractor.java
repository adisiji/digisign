package nb.scode.digisign.interactor;

public interface SignUpInteractor extends BaseInteractor {

  void register(String email, String pass, CommonDListener dListener);
}