package nb.scode.digisign.injection;

import dagger.Component;
import nb.scode.digisign.view.impl.ReceivedDocActivity;

@ActivityScope @Component(dependencies = AppComponent.class, modules = ReceivedDocViewModule.class)
public interface ReceivedDocViewComponent {
  void inject(ReceivedDocActivity activity);
}