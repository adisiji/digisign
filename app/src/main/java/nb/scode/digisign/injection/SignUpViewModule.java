package nb.scode.digisign.injection;

import android.support.annotation.NonNull;
import dagger.Module;
import dagger.Provides;
import nb.scode.digisign.data.DataTask;
import nb.scode.digisign.interactor.SignUpInteractor;
import nb.scode.digisign.interactor.impl.SignUpInteractorImpl;
import nb.scode.digisign.presenter.SignUpPresenter;
import nb.scode.digisign.presenter.impl.SignUpPresenterImpl;
import nb.scode.digisign.presenter.loader.PresenterFactory;

@Module public final class SignUpViewModule {
  @NonNull @Provides public SignUpInteractor provideInteractor(DataTask dataTask) {
    return new SignUpInteractorImpl(dataTask);
  }

  @NonNull @Provides public PresenterFactory<SignUpPresenter> providePresenterFactory(
      @NonNull final SignUpInteractor interactor) {
    return new PresenterFactory<SignUpPresenter>() {
      @NonNull @Override public SignUpPresenter create() {
        return new SignUpPresenterImpl(interactor);
      }
    };
  }
}
