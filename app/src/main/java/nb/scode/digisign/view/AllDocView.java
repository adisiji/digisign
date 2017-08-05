package nb.scode.digisign.view;

import android.support.annotation.UiThread;

@UiThread public interface AllDocView {

  void showFirstData();

  void notifyAdapter();

  void showLoading();

  void hideLoading();

}