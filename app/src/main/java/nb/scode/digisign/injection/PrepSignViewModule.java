package nb.scode.digisign.injection;

import android.support.annotation.NonNull;
import dagger.Module;
import dagger.Provides;
import nb.scode.digisign.data.DataTask;
import nb.scode.digisign.interactor.ChoosePdfInteractor;
import nb.scode.digisign.interactor.impl.ChoosePdfInteractorImpl;
import nb.scode.digisign.presenter.ChoosePdfPresenter;
import nb.scode.digisign.presenter.impl.ChoosePdfPresenterImpl;
import nb.scode.digisign.presenter.loader.PresenterFactory;

@Module public final class PrepSignViewModule {
  @NonNull @Provides public ChoosePdfInteractor provideInteractor(final DataTask dataTask) {
    return new ChoosePdfInteractorImpl(dataTask);
  }

  @NonNull @Provides public PresenterFactory<ChoosePdfPresenter> providePresenterFactory(
      @NonNull final ChoosePdfInteractor interactor) {
    return new PresenterFactory<ChoosePdfPresenter>() {
      @NonNull @Override public ChoosePdfPresenter create() {
        return new ChoosePdfPresenterImpl(interactor);
      }
    };
  }
}
