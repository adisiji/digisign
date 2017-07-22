package nb.scode.digisign.view;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

@UiThread public interface LoginView {

  void gotoMain();

  void showToast(String message);

  @NonNull String getEmail();

  @NonNull String getPassword();

  void showProgress();

  void hideProgress();

}