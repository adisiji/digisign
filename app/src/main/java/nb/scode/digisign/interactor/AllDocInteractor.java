package nb.scode.digisign.interactor;

import nb.scode.digisign.view.model.ItemAllDoc;

public interface AllDocInteractor extends BaseInteractor {

  void getSentPost(AllDocIntListener listener);

  void getReceivePost(AllDocIntListener listener);

  void getAllPost(AllDocIntListener listener);

  int getPostSize();

  ItemAllDoc getItemDoc(int pos);

  interface AllDocIntListener {
    void onProcess();

    void onSuccess();

    void onFailed(String message);
  }
}