package nb.scode.digisign.interactor.impl;

import android.support.annotation.NonNull;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import nb.scode.digisign.data.DataTask;
import nb.scode.digisign.data.remote.ApiTask;
import nb.scode.digisign.data.remote.FireModel.KeyUser;
import nb.scode.digisign.data.remote.FireModel.Post;
import nb.scode.digisign.interactor.AllDocInteractor;
import nb.scode.digisign.view.model.ItemAllDoc;

public final class AllDocInteractorImpl implements AllDocInteractor {

  @NonNull private final DataTask dataTask;
  private List<KeyUser> keyUserList;
  private KeyUser keyUser;

  @Inject public AllDocInteractorImpl(@NonNull DataTask dataTask) {
    this.dataTask = dataTask;
    keyUserList = dataTask.getListUser();
    keyUser = dataTask.getOwnerKey();
  }

  @Override public void getSentPost(@NonNull final AllDocIntListener listener) {
    dataTask.getPostSent(new ApiTask.GetListPostListener() {
      @Override public void onProcess() {
        listener.onProcess();
      }

      @Override public void onSuccess(@NonNull List<Post> postList) {
        List<ItemAllDoc> itemAllDocs = new ArrayList<ItemAllDoc>();
        for (Post post : postList) {
          itemAllDocs.add(convertPostToItem(post));
        }
        listener.onSuccess(itemAllDocs);
      }

      @Override public void onFailed(String message) {
        listener.onFailed(message);
      }
    });
  }

  @Override public void getReceivePost(@NonNull final AllDocIntListener listener) {
    dataTask.getPostReceived(new ApiTask.GetListPostListener() {
      @Override public void onProcess() {
        listener.onProcess();
      }

      @Override public void onSuccess(@NonNull List<Post> postList) {
        List<ItemAllDoc> itemAllDocs = new ArrayList<ItemAllDoc>();
        for (Post post : postList) {
          itemAllDocs.add(convertPostToItem(post));
        }
        listener.onSuccess(itemAllDocs);
      }

      @Override public void onFailed(String message) {
        listener.onFailed(message);
      }
    });
  }

  @Override public void getAllPost(@NonNull final AllDocIntListener listener) {
    dataTask.getAllPost(new ApiTask.GetListPostListener() {
      @Override public void onProcess() {
        listener.onProcess();
      }

      @Override public void onSuccess(@NonNull List<Post> postList) {
        List<ItemAllDoc> itemAllDocs = new ArrayList<ItemAllDoc>();
        for (Post post : postList) {
          itemAllDocs.add(convertPostToItem(post));
        }
        listener.onSuccess(itemAllDocs);
      }

      @Override public void onFailed(String message) {
        listener.onFailed(message);
      }
    });
  }

  @NonNull private ItemAllDoc convertPostToItem(@NonNull Post post) {
    ItemAllDoc itemAllDoc = new ItemAllDoc();
    itemAllDoc.setFilename(post.getFilename());
    Long xx = post.getTimestamp();
    Date date = new Date(xx);
    String datez = DateFormat.getDateInstance(DateFormat.LONG).format(date);
    itemAllDoc.setFiledate(datez);
    String from = null;
    if (keyUser.getKey().equals(post.getSenderKey())) {
      itemAllDoc.setSent(true);
      from = keyUser.getUser().getName();
      if (from == null) {
        from = keyUser.getUser().getEmail();
      }
    } else {
      for (KeyUser keyUser : keyUserList) {
        if (keyUser.getKey().equals(post.getSenderKey())) {
          from = keyUser.getUser().getName();
          if (from == null) {
            from = keyUser.getUser().getEmail();
          }
        }
      }
    }
    itemAllDoc.setFromname(from);
    return itemAllDoc;
  }
}