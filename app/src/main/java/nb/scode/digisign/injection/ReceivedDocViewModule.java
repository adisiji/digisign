package nb.scode.digisign.injection;

import android.support.annotation.NonNull;
import dagger.Module;
import dagger.Provides;
import nb.scode.digisign.data.DataTask;
import nb.scode.digisign.interactor.ReceivedDocInteractor;
import nb.scode.digisign.interactor.impl.ReceivedDocInteractorImpl;
import nb.scode.digisign.presenter.ReceivedDocPresenter;
import nb.scode.digisign.presenter.impl.ReceivedDocPresenterImpl;
import nb.scode.digisign.presenter.loader.PresenterFactory;

@Module public final class ReceivedDocViewModule {
  @NonNull @Provides public ReceivedDocInteractor provideInteractor(DataTask dataTask) {
    return new ReceivedDocInteractorImpl(dataTask);
  }

  @NonNull @Provides public PresenterFactory<ReceivedDocPresenter> providePresenterFactory(
      @NonNull final ReceivedDocInteractor interactor) {
    return new PresenterFactory<ReceivedDocPresenter>() {
      @NonNull @Override public ReceivedDocPresenter create() {
        return new ReceivedDocPresenterImpl(interactor);
      }
    };
  }
}
