package nb.scode.digisign.injection;

import android.support.annotation.NonNull;
import dagger.Module;
import dagger.Provides;
import nb.scode.digisign.interactor.AllDocInteractor;
import nb.scode.digisign.interactor.impl.AllDocInteractorImpl;
import nb.scode.digisign.presenter.AllDocPresenter;
import nb.scode.digisign.presenter.impl.AllDocPresenterImpl;
import nb.scode.digisign.presenter.loader.PresenterFactory;

@Module public final class AllDocViewModule {
  @Provides public AllDocInteractor provideInteractor() {
    return new AllDocInteractorImpl();
  }

  @Provides public PresenterFactory<AllDocPresenter> providePresenterFactory(
      @NonNull final AllDocInteractor interactor) {
    return new PresenterFactory<AllDocPresenter>() {
      @NonNull @Override public AllDocPresenter create() {
        return new AllDocPresenterImpl(interactor);
      }
    };
  }
}
