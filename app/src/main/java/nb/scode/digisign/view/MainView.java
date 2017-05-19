package nb.scode.digisign.view;

import android.support.annotation.UiThread;

@UiThread public interface MainView {

  void gotoLogin();

  void showProgressDialog(String message);

  void hideProgressDialog();

  void showToast(String message);

}