package nb.scode.digisign.injection;

import android.support.annotation.NonNull;
import dagger.Module;
import dagger.Provides;
import nb.scode.digisign.interactor.TakePhotoInteractor;
import nb.scode.digisign.interactor.impl.TakePhotoInteractorImpl;
import nb.scode.digisign.presenter.TakePhotoPresenter;
import nb.scode.digisign.presenter.impl.TakePhotoPresenterImpl;
import nb.scode.digisign.presenter.loader.PresenterFactory;

@Module public final class TakePhotoViewModule {
  @Provides public TakePhotoInteractor provideInteractor() {
    return new TakePhotoInteractorImpl();
  }

  @Provides public PresenterFactory<TakePhotoPresenter> providePresenterFactory(
      @NonNull final TakePhotoInteractor interactor) {
    return new PresenterFactory<TakePhotoPresenter>() {
      @NonNull @Override public TakePhotoPresenter create() {
        return new TakePhotoPresenterImpl(interactor);
      }
    };
  }
}
