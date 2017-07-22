package nb.scode.digisign.injection;

import android.support.annotation.NonNull;
import dagger.Module;
import dagger.Provides;
import nb.scode.digisign.data.DataTask;
import nb.scode.digisign.interactor.HomeInteractor;
import nb.scode.digisign.interactor.impl.HomeInteractorImpl;
import nb.scode.digisign.presenter.HomePresenter;
import nb.scode.digisign.presenter.impl.HomePresenterImpl;
import nb.scode.digisign.presenter.loader.PresenterFactory;

@Module public final class HomeViewModule {
  @NonNull @Provides public HomeInteractor provideInteractor(final DataTask dataTask) {
    return new HomeInteractorImpl(dataTask);
  }

  @NonNull @Provides public PresenterFactory<HomePresenter> providePresenterFactory(
      @NonNull final HomeInteractor interactor) {
    return new PresenterFactory<HomePresenter>() {
      @NonNull @Override public HomePresenter create() {
        return new HomePresenterImpl(interactor);
      }
    };
  }
}
