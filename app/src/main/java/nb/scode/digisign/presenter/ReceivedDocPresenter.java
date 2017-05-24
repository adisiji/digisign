package nb.scode.digisign.presenter;

import nb.scode.digisign.view.ReceivedDocView;

public interface ReceivedDocPresenter extends BasePresenter<ReceivedDocView> {

  String getTimefromIntent();

  String getFileName();

  String getFileSize();

}