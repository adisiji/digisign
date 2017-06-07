package nb.scode.digisign.injection;

import dagger.Component;
import nb.scode.digisign.view.impl.SettingsFragment;

@FragmentScope @Component(dependencies = AppComponent.class, modules = SettingsViewModule.class)
public interface SettingsViewComponent {
  void inject(SettingsFragment fragment);
}