package nb.scode.digisign.injection;

import dagger.Component;
import nb.scode.digisign.view.impl.PrepSignActivity;

@ActivityScope @Component(dependencies = AppComponent.class, modules = PrepSignViewModule.class)
public interface PrepSignViewComponent {
  void inject(PrepSignActivity activity);
}