package nb.scode.digisign.injection;

import dagger.Component;
import nb.scode.digisign.view.impl.AllDocFragment;

@FragmentScope @Component(dependencies = AppComponent.class, modules = AllDocViewModule.class)
public interface AllDocViewComponent {
  void inject(AllDocFragment fragment);
}