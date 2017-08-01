package nb.scode.digisign.injection;

import dagger.Component;
import nb.scode.digisign.view.impl.TakePhotoActivity;

@ActivityScope @Component(dependencies = AppComponent.class, modules = TakePhotoViewModule.class)
public interface TakePhotoViewComponent {
  void inject(TakePhotoActivity activity);
}