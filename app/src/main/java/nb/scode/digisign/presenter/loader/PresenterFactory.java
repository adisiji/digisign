package nb.scode.digisign.presenter.loader;

import android.support.annotation.NonNull;
import nb.scode.digisign.presenter.BasePresenter;

/**
 * Factory to implement to create a presenter
 */
public interface PresenterFactory<T extends BasePresenter> {
  @NonNull T create();
}
