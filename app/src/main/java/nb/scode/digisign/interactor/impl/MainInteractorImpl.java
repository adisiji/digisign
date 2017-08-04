package nb.scode.digisign.interactor.impl;

import android.support.annotation.NonNull;
import java.io.File;
import javax.inject.Inject;
import nb.scode.digisign.data.DataTask;
import nb.scode.digisign.data.local.LocalTask;
import nb.scode.digisign.data.remote.ApiTask;
import nb.scode.digisign.data.remote.BusModel.SignOutEvent;
import nb.scode.digisign.data.remote.BusModel.UserBusPost;
import nb.scode.digisign.interactor.MainInteractor;
import org.greenrobot.eventbus.EventBus;

public final class MainInteractorImpl implements MainInteractor {

  private final DataTask dataTask;

  @Inject public MainInteractorImpl(DataTask dataTask) {
    this.dataTask = dataTask;
  }

  @Override public void getUser() {
    UserBusPost busPost = dataTask.getUserProfile();
    EventBus.getDefault().post(busPost);
  }

  @Override public void saveToken(String token) {
    dataTask.saveToken(token);
  }

  @Override public void logout() {
    dataTask.logout();
    EventBus.getDefault().post(new SignOutEvent());
  }

  @Override public boolean isRecentEmailSame() {
    String email = dataTask.getEmailUser();
    return dataTask.isEmailSame(email);
  }

  @Override public boolean isUserNull() {
    return dataTask.getEmailUser() == null;
  }

  @Override public void getUser(final MainListener listener) {
    dataTask.firebaseReAuth(dataTask.getUserToken(), new ApiTask.CommonAListener() {
      @Override public void onProcess() {
        listener.onProcess();
      }

      @Override public void onSuccess() {
        listener.onSuccess();
      }

      @Override public void onFailed(String message) {
        listener.onFailed(message);
      }
    });
  }

  @Override public void downloadKeyPair(@NonNull final MainListener listener) {
    File privkey = dataTask.getPrivateKey();
    File pubkey = dataTask.getPublicKey();
    dataTask.downloadKeyPair(pubkey, privkey, new ApiTask.CommonAListener() {
      @Override public void onProcess() {
        listener.onProcess();
      }

      @Override public void onSuccess() {
        listener.onSuccess();
      }

      @Override public void onFailed(String message) {
        listener.onFailed(message);
      }
    });
  }

  @Override public void setRecentEmail() {
    String email = dataTask.getEmailUser();
    dataTask.setEmailPref(email);
  }

  @Override public boolean isKeyPairAvailable() {
    return dataTask.isLocalKeyPairAvailable();
  }

  @Override public void isRemoteKeyPairAvail(@NonNull final MainListener listener) {
    dataTask.checkRemoteKeyPair(new ApiTask.CommonAListener() {
      @Override public void onProcess() {
        listener.onProcess();
      }

      @Override public void onSuccess() {
        listener.onSuccess();
      }

      @Override public void onFailed(String message) {
        listener.onFailed(message);
      }
    });
  }

  @Override public void initKeyPair(@NonNull final MainInitListener listener) {
    try {
      listener.onStartInit();
      dataTask.createKey(new LocalTask.CommonListener() {
        @Override public void onFinished() {
          uploadKeyPair(listener);
        }

        @Override public void onError(String message) {

        }

        @Override public void onProcess() {
          listener.onCreateKey();
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void uploadKeyPair(@NonNull final MainInitListener listener) {
    File privkey = dataTask.getPrivateKey();
    File pubkey = dataTask.getPublicKey();
    dataTask.uploadKeyPair(pubkey, privkey, new ApiTask.CommonAListener() {
      @Override public void onProcess() {
        listener.onUploadKey();
      }

      @Override public void onSuccess() {
        listener.onFinishInit();
      }

      @Override public void onFailed(String message) {
        listener.onError(message);
      }
    });
  }

  @Override public void uploadKeyPair(@NonNull final MainListener listener) {
    File privkey = dataTask.getPrivateKey();
    File pubkey = dataTask.getPublicKey();
    dataTask.uploadKeyPair(pubkey, privkey, new ApiTask.CommonAListener() {
      @Override public void onProcess() {
        listener.onProcess();
      }

      @Override public void onSuccess() {
        listener.onSuccess();
      }

      @Override public void onFailed(String message) {
        listener.onFailed(message);
      }
    });
  }

  @Override public void initListUser(@NonNull final MainListener listener) {
    dataTask.initListUser(new ApiTask.CommonAListener() {
      @Override public void onProcess() {
        listener.onProcess();
      }

      @Override public void onSuccess() {
        listener.onSuccess();
      }

      @Override public void onFailed(String message) {
        listener.onFailed(message);
      }
    });
  }
}