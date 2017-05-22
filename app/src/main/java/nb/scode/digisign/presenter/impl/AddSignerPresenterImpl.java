package nb.scode.digisign.presenter.impl;

import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import nb.scode.digisign.data.remote.FireModel.KeyUser;
import nb.scode.digisign.interactor.AddSignerInteractor;
import nb.scode.digisign.presenter.AddSignerPresenter;
import nb.scode.digisign.view.AddSignerView;

public final class AddSignerPresenterImpl extends BasePresenterImpl<AddSignerView>
    implements AddSignerPresenter {

  /**
   * The interactor
   */
  @NonNull private final AddSignerInteractor mInteractor;
  List<KeyUser> keyUserList;

  // The view is available using the mView variable

  @Inject public AddSignerPresenterImpl(@NonNull AddSignerInteractor interactor) {
    mInteractor = interactor;
  }

  @Override public void onStart(boolean viewCreated) {
    super.onStart(viewCreated);
    if (viewCreated) {
      getUserList();
    }
    // Your code here. Your view is available using mView and will not be null until next onStop()
  }

  @Override public void onStop() {
    // Your code here, mView will be null after this method until next onStart()

    super.onStop();
  }

  @Override public void onPresenterDestroyed() {
        /*
         * Your code here. After this method, your presenter (and view) will be completely destroyed
         * so make sure to cancel any HTTP call or database connection
         */

    super.onPresenterDestroyed();
  }

  @Override public void getUserList() {
    keyUserList = mInteractor.getListUser();
    List<String> emailList = new ArrayList<>();
    for (KeyUser keyUser : keyUserList) {
      emailList.add(keyUser.getUser().getEmail());
    }
    mView.showListEmailUser(emailList);
  }

  @Override public void getUserName(int pos) {
    String name = keyUserList.get(pos).getUser().getName();
    mView.showOwnerName(name);
  }

  @Override public void getOwnerSignature() {
    KeyUser keyUser = mInteractor.getOwnerKey();
    String email = keyUser.getUser().getEmail();
    mView.showOwnerEmail(email);
    String name = keyUser.getUser().getName();
    mView.showOwnerName(name);
  }
}