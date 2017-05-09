package nb.scode.digisign.injection;

import dagger.Component;
import nb.scode.digisign.view.impl.AddSignerActivity;

@ActivityScope @Component(dependencies = AppComponent.class, modules = AddSignerViewModule.class)
public interface AddSignerViewComponent {
  void inject(AddSignerActivity activity);
}