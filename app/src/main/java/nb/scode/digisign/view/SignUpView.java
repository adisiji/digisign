package nb.scode.digisign.view;

import android.support.annotation.UiThread;

@UiThread public interface SignUpView {

  String getEmail();

  String getPassword();

  void showProgress();

  void hideProgress();

  void showToast(String message);

  void gotoMain();
}