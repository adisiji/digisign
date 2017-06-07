package nb.scode.digisign.injection;

import android.support.annotation.NonNull;
import dagger.Module;
import dagger.Provides;
import nb.scode.digisign.interactor.SettingsInteractor;
import nb.scode.digisign.interactor.impl.SettingsInteractorImpl;
import nb.scode.digisign.presenter.SettingsPresenter;
import nb.scode.digisign.presenter.impl.SettingsPresenterImpl;
import nb.scode.digisign.presenter.loader.PresenterFactory;

@Module public final class SettingsViewModule {
  @Provides public SettingsInteractor provideInteractor() {
    return new SettingsInteractorImpl();
  }

  @Provides public PresenterFactory<SettingsPresenter> providePresenterFactory(
      @NonNull final SettingsInteractor interactor) {
    return new PresenterFactory<SettingsPresenter>() {
      @NonNull @Override public SettingsPresenter create() {
        return new SettingsPresenterImpl(interactor);
      }
    };
  }
}
