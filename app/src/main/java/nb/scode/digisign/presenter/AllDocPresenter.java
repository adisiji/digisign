package nb.scode.digisign.presenter;

import nb.scode.digisign.view.AllDocView;

public interface AllDocPresenter extends BasePresenter<AllDocView> {

  void getSentPost();

  void getReceivedPost();

}