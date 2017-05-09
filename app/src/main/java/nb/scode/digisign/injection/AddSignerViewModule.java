package nb.scode.digisign.injection;

import android.support.annotation.NonNull;
import dagger.Module;
import dagger.Provides;
import nb.scode.digisign.interactor.AddSignerInteractor;
import nb.scode.digisign.interactor.impl.AddSignerInteractorImpl;
import nb.scode.digisign.presenter.AddSignerPresenter;
import nb.scode.digisign.presenter.impl.AddSignerPresenterImpl;
import nb.scode.digisign.presenter.loader.PresenterFactory;

@Module public final class AddSignerViewModule {
  @Provides public AddSignerInteractor provideInteractor() {
    return new AddSignerInteractorImpl();
  }

  @Provides public PresenterFactory<AddSignerPresenter> providePresenterFactory(
      @NonNull final AddSignerInteractor interactor) {
    return new PresenterFactory<AddSignerPresenter>() {
      @NonNull @Override public AddSignerPresenter create() {
        return new AddSignerPresenterImpl(interactor);
      }
    };
  }
}
