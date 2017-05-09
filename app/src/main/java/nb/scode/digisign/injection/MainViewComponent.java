package nb.scode.digisign.injection;

import dagger.Component;
import nb.scode.digisign.view.impl.MainActivity;

@ActivityScope @Component(dependencies = AppComponent.class, modules = MainViewModule.class)
public interface MainViewComponent {
  void inject(MainActivity activity);
}