package nb.scode.digisign.view;

import android.support.annotation.UiThread;

@UiThread public interface AllDocView {

  void setupRecycler();

  void notifyAdapter();

  void showLoading();

  void hideLoading();

}