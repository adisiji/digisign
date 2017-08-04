package nb.scode.digisign.presenter;

import nb.scode.digisign.view.TakePhotoView;

public interface TakePhotoPresenter extends BasePresenter<TakePhotoView> {

  void addSigner();

  void countFileSize(String imagePath);
}