package nb.scode.digisign.presenter;

import nb.scode.digisign.view.AddSignerView;

public interface AddSignerPresenter extends BasePresenter<AddSignerView> {

  void getUserList();

  void getUserName(int pos);

  //  void sendDoc();

  void sendDocWithSign();

}