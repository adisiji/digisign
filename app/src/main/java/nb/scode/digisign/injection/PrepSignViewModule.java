package nb.scode.digisign.injection;

import android.support.annotation.NonNull;
import dagger.Module;
import dagger.Provides;
import nb.scode.digisign.data.DataTask;
import nb.scode.digisign.interactor.PrepSignInteractor;
import nb.scode.digisign.interactor.impl.PrepSignInteractorImpl;
import nb.scode.digisign.presenter.PrepSignPresenter;
import nb.scode.digisign.presenter.impl.PrepSignPresenterImpl;
import nb.scode.digisign.presenter.loader.PresenterFactory;

@Module public final class PrepSignViewModule {
  @NonNull @Provides public PrepSignInteractor provideInteractor(final DataTask dataTask) {
    return new PrepSignInteractorImpl(dataTask);
  }

  @NonNull @Provides public PresenterFactory<PrepSignPresenter> providePresenterFactory(
      @NonNull final PrepSignInteractor interactor) {
    return new PresenterFactory<PrepSignPresenter>() {
      @NonNull @Override public PrepSignPresenter create() {
        return new PrepSignPresenterImpl(interactor);
      }
    };
  }
}
