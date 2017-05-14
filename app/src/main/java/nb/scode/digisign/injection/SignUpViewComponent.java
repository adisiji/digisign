package nb.scode.digisign.injection;

import dagger.Component;
import nb.scode.digisign.view.impl.SignUpActivity;

@ActivityScope @Component(dependencies = AppComponent.class, modules = SignUpViewModule.class)
public interface SignUpViewComponent {
  void inject(SignUpActivity activity);
}