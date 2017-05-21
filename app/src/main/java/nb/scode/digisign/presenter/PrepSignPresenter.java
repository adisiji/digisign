package nb.scode.digisign.presenter;

import nb.scode.digisign.view.PrepSignView;

public interface PrepSignPresenter extends BasePresenter<PrepSignView> {

  void getFilePdf(String uripdf);
}