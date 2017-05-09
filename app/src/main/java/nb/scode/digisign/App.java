package nb.scode.digisign;

import android.app.Application;
import android.support.annotation.NonNull;
import nb.scode.digisign.injection.AppComponent;
import nb.scode.digisign.injection.AppModule;
import nb.scode.digisign.injection.DaggerAppComponent;
import timber.log.Timber;

public final class App extends Application {
  private AppComponent mAppComponent;

  @Override public void onCreate() {
    super.onCreate();
    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
    }
    mAppComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
  }

  @NonNull public AppComponent getAppComponent() {
    return mAppComponent;
  }
}