package nb.scode.digisign.injection;

import dagger.Component;
import nb.scode.digisign.view.impl.SentFilesFragment;

@FragmentScope @Component(dependencies = AppComponent.class, modules = SentFilesViewModule.class)
public interface SentFilesViewComponent {
  void inject(SentFilesFragment fragment);
}