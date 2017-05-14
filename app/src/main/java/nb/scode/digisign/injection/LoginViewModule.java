package nb.scode.digisign.injection;

import android.support.annotation.NonNull;
import dagger.Module;
import dagger.Provides;
import nb.scode.digisign.data.DataTask;
import nb.scode.digisign.interactor.LoginInteractor;
import nb.scode.digisign.interactor.impl.LoginInteractorImpl;
import nb.scode.digisign.presenter.LoginPresenter;
import nb.scode.digisign.presenter.impl.LoginPresenterImpl;
import nb.scode.digisign.presenter.loader.PresenterFactory;

@Module public final class LoginViewModule {
  @Provides public LoginInteractor provideInteractor(DataTask dataTask) {
    return new LoginInteractorImpl(dataTask);
  }

  @Provides public PresenterFactory<LoginPresenter> providePresenterFactory(
      @NonNull final LoginInteractor interactor) {
    return new PresenterFactory<LoginPresenter>() {
      @NonNull @Override public LoginPresenter create() {
        return new LoginPresenterImpl(interactor);
      }
    };
  }
}
