package nb.scode.digisign.interactor;

import java.util.List;
import nb.scode.digisign.data.remote.FireModel.KeyUser;

public interface AddSignerInteractor extends BaseInteractor {

  List<KeyUser> getListUser();

  KeyUser getOwnerKey();

  void createSignFile(String uripdf, CommonIListener listener);

  void uploadSignFile(CommonIListener listener);

  void insertPostData(String desc, String from, String name, String receiverKey, String filename,
      String type, CommonIListener listener);

  interface CommonIListener {

    void onProcess();

    void onSuccess();

    void onFailed(String message);
  }
}