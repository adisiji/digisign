package nb.scode.digisign.view;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

@UiThread public interface SignUpView {

  @NonNull String getEmail();

  @NonNull String getPassword();

  void showProgress();

  void hideProgress();

  void showToast(String message);

  void gotoMain();
}