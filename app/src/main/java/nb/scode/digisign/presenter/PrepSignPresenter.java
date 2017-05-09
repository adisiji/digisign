package nb.scode.digisign.presenter;

import android.net.Uri;
import nb.scode.digisign.view.PrepSignView;

public interface PrepSignPresenter extends BasePresenter<PrepSignView> {

  void getFilePdf(Uri uri);
}