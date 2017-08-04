package nb.scode.digisign.presenter;

import nb.scode.digisign.view.MainView;

public interface MainPresenter extends BasePresenter<MainView> {

  void logout();

  void sendTokenToServer(String token);

}