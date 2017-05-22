package nb.scode.digisign.view;

import android.support.annotation.UiThread;
import java.util.List;

@UiThread public interface AddSignerView {

  void showListEmailUser(List<String> listEmail);

  void showOwnerEmail(String email);

  void showOwnerName(String name);

}