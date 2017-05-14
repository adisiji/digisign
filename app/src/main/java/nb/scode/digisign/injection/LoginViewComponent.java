package nb.scode.digisign.injection;

import dagger.Component;
import nb.scode.digisign.view.impl.LoginActivity;

@ActivityScope @Component(dependencies = AppComponent.class, modules = {
    LoginViewModule.class, GoogleApiModule.class
}) public interface LoginViewComponent {
  void inject(LoginActivity activity);
}