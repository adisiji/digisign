package nb.scode.digisign.presenter;

import nb.scode.digisign.view.ChoosePdfView;

public interface ChoosePdfPresenter extends BasePresenter<ChoosePdfView> {

  void getFilePdf(String uripdf);
}