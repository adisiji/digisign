package nb.scode.digisign.view;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import java.util.List;

@UiThread public interface AddSignerView {

  void showListEmailUser(List<String> listEmail);

  void showOwnerName(String name);

  String getUriFile();

  @NonNull String getEmail();

  @NonNull String getName();

  @NonNull String getDesc();

  String getFilename();

  void clearErrorEditText();

  void showErrorName(String message);

  void showLoading();

  void hideLoading();

  void showToast(String message);

  void gotoMain();

}