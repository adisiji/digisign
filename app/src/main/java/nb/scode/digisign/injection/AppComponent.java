package nb.scode.digisign.injection;

import android.content.Context;
import dagger.Component;
import javax.inject.Singleton;
import nb.scode.digisign.App;
import nb.scode.digisign.data.DataTask;

@Singleton @Component(modules = { AppModule.class }) public interface AppComponent {
  Context getAppContext();

  App getApp();

  DataTask getDataTask();
}