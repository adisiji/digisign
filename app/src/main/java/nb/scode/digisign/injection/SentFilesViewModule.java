package nb.scode.digisign.injection;

import android.support.annotation.NonNull;
import dagger.Module;
import dagger.Provides;
import nb.scode.digisign.interactor.SentFilesInteractor;
import nb.scode.digisign.interactor.impl.SentFilesInteractorImpl;
import nb.scode.digisign.presenter.SentFilesPresenter;
import nb.scode.digisign.presenter.impl.SentFilesPresenterImpl;
import nb.scode.digisign.presenter.loader.PresenterFactory;

@Module public final class SentFilesViewModule {
  @Provides public SentFilesInteractor provideInteractor() {
    return new SentFilesInteractorImpl();
  }

  @Provides public PresenterFactory<SentFilesPresenter> providePresenterFactory(
      @NonNull final SentFilesInteractor interactor) {
    return new PresenterFactory<SentFilesPresenter>() {
      @NonNull @Override public SentFilesPresenter create() {
        return new SentFilesPresenterImpl(interactor);
      }
    };
  }
}
