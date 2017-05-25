package nb.scode.digisign.view;

import android.support.annotation.UiThread;
import java.util.List;
import nb.scode.digisign.view.model.ItemAllDoc;

@UiThread public interface AllDocView {

  void showAllDocItems(List<ItemAllDoc> docList);

}