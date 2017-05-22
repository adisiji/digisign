package nb.scode.digisign.interactor;

import java.util.List;
import nb.scode.digisign.data.remote.FireModel.KeyUser;

public interface AddSignerInteractor extends BaseInteractor {

  List<KeyUser> getListUser();

  KeyUser getOwnerKey();
}