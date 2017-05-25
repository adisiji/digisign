package nb.scode.digisign.interactor;

import java.util.List;
import nb.scode.digisign.view.model.ItemAllDoc;

public interface AllDocInteractor extends BaseInteractor {

  void getSentPost(AllDocIntListener listener);

  void getReceivePost(AllDocIntListener listener);

  void getAllPost(AllDocIntListener listener);

  interface AllDocIntListener {
    void onProcess();

    void onSuccess(List<ItemAllDoc> postList);

    void onFailed(String message);
  }
}