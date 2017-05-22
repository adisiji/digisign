package nb.scode.digisign.presenter;

import nb.scode.digisign.view.AddSignerView;

public interface AddSignerPresenter extends BasePresenter<AddSignerView> {

  void getOwnerSignature();

  void getUserList();

  void getUserName(int pos);

}