package nb.scode.digisign.interactor.impl;

import android.support.annotation.NonNull;
import java.io.File;
import java.util.Calendar;
import java.util.List;
import javax.inject.Inject;
import nb.scode.digisign.data.DataTask;
import nb.scode.digisign.data.local.LocalTask;
import nb.scode.digisign.data.remote.ApiTask;
import nb.scode.digisign.data.remote.FireModel.KeyUser;
import nb.scode.digisign.data.remote.FireModel.Post;
import nb.scode.digisign.interactor.AddSignerInteractor;
import timber.log.Timber;

public final class AddSignerInteractorImpl implements AddSignerInteractor {

  @NonNull private final DataTask dataTask;
  private String uriSignFile;

  @Inject public AddSignerInteractorImpl(@NonNull DataTask dataTask) {
    this.dataTask = dataTask;
  }

  @Override public List<KeyUser> getListUser() {
    return dataTask.getListUser();
  }

  @Override public KeyUser getOwnerKey() {
    return dataTask.getOwnerKey();
  }

  @Override public void createSignFile(String urifile, String filetype,
      @NonNull final CommonIListener listener) {

    dataTask.createSignFile(urifile, filetype, new LocalTask.CommonListener() {
      @Override public void onFinished() {
        dataTask.createZip(new LocalTask.CommonListener() {
          @Override public void onFinished() {
            // next, upload file
            listener.onSuccess();
          }

          @Override public void onError(String message) {
            listener.onFailed(message);
          }

          @Override public void onProcess() {
            Timber.d("onProcess(): zipp");
          }
        });
      }

      @Override public void onError(String message) {
        listener.onFailed(message);
      }

      @Override public void onProcess() {
        listener.onProcess();
      }
    });
  }

  @Override public void uploadSignFile(@NonNull final CommonIListener listener) {
    File file = dataTask.getFileToSend();
    dataTask.uploadSignFile(file, new ApiTask.UploadSignListener() {
      @Override public void onProcess() {
        listener.onProcess();
      }

      @Override public void onSuccess(String uri) {
        Timber.d("onSuccess(): Good");
        uriSignFile = uri;
        listener.onSuccess();
      }

      @Override public void onFailed(String message) {
        listener.onFailed(message);
      }
    });
  }

  @Override public void insertPostData(String desc, String from, String name, String receiverKey,
      String filename, String type, @NonNull final CommonIListener listener) {
    Post post = new Post();
    post.setDesc(desc);
    post.setSenderKey(from);
    post.setTimestamp(Calendar.getInstance().getTimeInMillis());
    post.setType(type);
    post.setLinkDownload(uriSignFile);
    post.setReceiverKey(receiverKey);
    post.setReceiverName(name);
    post.setFilename(filename);

    dataTask.insertPostData(post, new ApiTask.CommonAListener() {
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