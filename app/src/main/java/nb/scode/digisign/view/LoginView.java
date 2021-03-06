package nb.scode.digisign.view;

import android.support.annotation.UiThread;

@UiThread public interface LoginView {

  void gotoMain();

  void showToast(String message);

  String getEmail();

  String getPassword();

  void showProgress();

  void hideProgress();

}