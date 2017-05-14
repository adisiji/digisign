package nb.scode.digisign.injection;

import android.support.annotation.NonNull;
import dagger.Module;
import dagger.Provides;
import nb.scode.digisign.data.DataTask;
import nb.scode.digisign.interactor.MainInteractor;
import nb.scode.digisign.interactor.impl.MainInteractorImpl;
import nb.scode.digisign.presenter.MainPresenter;
import nb.scode.digisign.presenter.impl.MainPresenterImpl;
import nb.scode.digisign.presenter.loader.PresenterFactory;

@Module public final class MainViewModule {
  @Provides public MainInteractor provideInteractor(DataTask dataTask) {
    return new MainInteractorImpl(dataTask);
  }

  @Provides public PresenterFactory<MainPresenter> providePresenterFactory(
      @NonNull final MainInteractor interactor) {
    return new PresenterFactory<MainPresenter>() {
      @NonNull @Override public MainPresenter create() {
        return new MainPresenterImpl(interactor);
      }
    };
  }
}
