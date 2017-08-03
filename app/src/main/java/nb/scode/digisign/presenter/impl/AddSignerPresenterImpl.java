package nb.scode.digisign.presenter.impl;

import android.support.annotation.NonNull;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import nb.scode.digisign.data.remote.FireModel.KeyUser;
import nb.scode.digisign.interactor.AddSignerInteractor;
import nb.scode.digisign.presenter.AddSignerPresenter;
import nb.scode.digisign.view.AddSignerView;
import timber.log.Timber;

public final class AddSignerPresenterImpl extends BasePresenterImpl<AddSignerView>
    implements AddSignerPresenter {

  /**
   * The interactor
   */
  @NonNull private final AddSignerInteractor mInteractor;
  private List<KeyUser> keyUserList;

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

  @Override public void sendDocWithSign() {
    String uripdf = null;
    String filetype = null;
    if (mView != null) {
      mView.showLoading();
      uripdf = mView.getUriFile();
      int sizeFileString = mView.getFilename().length();
      filetype = mView.getFilename().substring(sizeFileString - 3);
    }
    if (uripdf != null) {
      final String finalFiletype = filetype;
      mInteractor.createSignFile(uripdf, mView.getFilename(), filetype,
          new AddSignerInteractor.CommonIListener() {
            @Override public void onProcess() {
              Timber.d("onProcess(): good");
            }

            @Override public void onSuccess() {
              Timber.d("onSuccess(): good");
              uploadDoc(finalFiletype);
            }

            @Override public void onFailed(String message) {
              mView.showToast(message);
              Timber.e("onFailed(): " + message);
            }
          });
    }
  }

  private void uploadDoc(final String filetype) {
    mInteractor.uploadSignFile(new AddSignerInteractor.CommonIListener() {
      @Override public void onProcess() {

      }

      @Override public void onSuccess() {
        Timber.d("onSuccess(): NICE !");
        insertPostData(filetype);
      }

      @Override public void onFailed(String message) {
        Timber.e("onFailed(): FIAASl");
        mView.hideLoading();
        mView.showToast(message);
      }
    });
  }

  private void insertPostData(String filetype) {
    mView.clearErrorEditText();
    String name = mView.getName();
    if (name.length() == 0) {
      mView.showErrorName("Name cannot be empty");
      return;
    }
    String email = mView.getEmail();
    if (email.length() == 0) {
      mView.showErrorEmail("Email cannot be empty");
      return;
    }
    String desc = mView.getDesc();
    String token = null;
    String filename = mView.getFilename();
    String keyReceiver = null;
    for (KeyUser keyUser : keyUserList) {
      if (keyUser.getUser().getEmail().equals(email)) {
        token = keyUser.getUser().getToken();
        keyReceiver = keyUser.getKey();
        break;
      }
    }

    String from = mInteractor.getOwnerKey().getKey();

    Timber.d("insertPostData(): from => %s, to => %s", from, keyReceiver);
    if (token != null) {
      mInteractor.insertPostData(desc, from, name, keyReceiver, filename, filetype,
          new AddSignerInteractor.CommonIListener() {
            @Override public void onProcess() {
              Timber.d("onProcess(): post good");
            }

            @Override public void onSuccess() {
              mView.hideLoading();
              mView.showToast("Doc has been sent");
              Single.timer(1500, TimeUnit.MILLISECONDS)
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(new Consumer<Long>() {
                    @Override public void accept(@io.reactivex.annotations.NonNull Long aLong)
                        throws Exception {
                      mView.gotoMain();
                    }
                  });
              Timber.d("onSuccess(): post okay");
            }

            @Override public void onFailed(String message) {

            }
          });
    } else {
      // // TODO: 5/23/2017 Add error to mView
    }
  }
}