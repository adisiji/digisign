package nb.scode.digisign.injection;

import dagger.Component;
import nb.scode.digisign.view.impl.HomeFragment;

@FragmentScope @Component(dependencies = AppComponent.class, modules = HomeViewModule.class)
public interface HomeViewComponent {
  void inject(HomeFragment fragment);
}