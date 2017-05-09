package nb.scode.digisign.injection;

import android.content.Context;
import android.support.annotation.NonNull;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import nb.scode.digisign.App;
import nb.scode.digisign.data.DataManager;
import nb.scode.digisign.data.DataTask;
import nb.scode.digisign.data.local.LocalHelper;
import nb.scode.digisign.data.local.LocalTask;
import nb.scode.digisign.data.remote.ApiHelper;
import nb.scode.digisign.data.remote.ApiService;
import nb.scode.digisign.data.remote.ApiTask;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * The type App module.
 */
@Module public final class AppModule {
  @NonNull private final App mApp;

  /**
   * Instantiates a new App module.
   *
   * @param app the app
   */
  public AppModule(@NonNull App app) {
    mApp = app;
  }

  /**
   * Provide app context context.
   *
   * @return the context
   */
  @Provides @Singleton Context provideAppContext() {
    return mApp;
  }

  /**
   * Provide app app.
   *
   * @return the app
   */
  @Provides @Singleton App provideApp() {
    return mApp;
  }

  /**
   * Provide api service api service.
   *
   * @return the api service
   */
  @Provides @Singleton ApiService provideApiService() {
    /*
    Gson gson = new GsonBuilder()
        .registerTypeAdapterFactory(MyGsonTypeAdapterFactory.create())
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        */
    Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiService.ENDPOINT)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build();
    return retrofit.create(ApiService.class);
  }

  /**
   * Provide preferences task preferences task.
   *
   * @param localHelper the preferences helper
   * @return the preferences task
   */
  @Provides @Singleton LocalTask providePreferencesTask(LocalHelper localHelper) {
    return localHelper;
  }

  /**
   * Provide api task api task.
   *
   * @param apiHelper the api helper
   * @return the api task
   */
  @Provides @Singleton ApiTask provideApiTask(ApiHelper apiHelper) {
    return apiHelper;
  }

  /**
   * Provide data task data task.
   *
   * @param dataManager the data manager
   * @return the data task
   */
  @Provides @Singleton DataTask provideDataTask(DataManager dataManager) {
    return dataManager;
  }
}
