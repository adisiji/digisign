package nb.scode.digisign.view.impl;

import android.os.Bundle;
import android.support.annotation.NonNull;
import javax.inject.Inject;
import nb.scode.digisign.R;
import nb.scode.digisign.injection.AppComponent;
import nb.scode.digisign.injection.DaggerLoginViewComponent;
import nb.scode.digisign.injection.LoginViewModule;
import nb.scode.digisign.presenter.LoginPresenter;
import nb.scode.digisign.presenter.loader.PresenterFactory;
import nb.scode.digisign.view.LoginView;

public final class LoginActivity extends BaseActivity<LoginPresenter, LoginView>
    implements LoginView {
  @Inject PresenterFactory<LoginPresenter> mPresenterFactory;

  // Your presenter is available using the mPresenter variable

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    // Your code here
    // Do not call mPresenter from here, it will be null! Wait for onStart or onPostCreate.
  }

  @Override protected void setupComponent(@NonNull AppComponent parentComponent) {
    DaggerLoginViewComponent.builder()
        .appComponent(parentComponent)
        .loginViewModule(new LoginViewModule())
        .build()
        .inject(this);
  }

  @NonNull @Override protected PresenterFactory<LoginPresenter> getPresenterFactory() {
    return mPresenterFactory;
  }
}
