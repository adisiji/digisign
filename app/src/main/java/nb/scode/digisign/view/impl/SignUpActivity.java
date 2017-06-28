package nb.scode.digisign.view.impl;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.transition.Slide;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import javax.inject.Inject;
import nb.scode.digisign.R;
import nb.scode.digisign.injection.AppComponent;
import nb.scode.digisign.injection.DaggerSignUpViewComponent;
import nb.scode.digisign.injection.SignUpViewModule;
import nb.scode.digisign.presenter.SignUpPresenter;
import nb.scode.digisign.presenter.loader.PresenterFactory;
import nb.scode.digisign.view.SignUpView;

public final class SignUpActivity extends BaseActivity<SignUpPresenter, SignUpView>
    implements SignUpView {
  @Inject PresenterFactory<SignUpPresenter> mPresenterFactory;
  @BindView(R.id.email) EditText etEmail;
  @BindView(R.id.password) EditText etPassword;
  @BindView(R.id.progressBar) ProgressBar progressBar;
  @BindView(R.id.logo_header) View logo;
  private View vEmail;
  private View vPass;
  // Your presenter is available using the mPresenter variable

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sign_up);
    ButterKnife.bind(this);
    vEmail = findViewById(R.id.email);
    vPass = findViewById(R.id.password);
    setupWindowAnimations();
    // Do not call mPresenter from here, it will be null! Wait for onStart or onPostCreate.
  }

  @Override protected void setupComponent(@NonNull AppComponent parentComponent) {
    DaggerSignUpViewComponent.builder()
        .appComponent(parentComponent)
        .signUpViewModule(new SignUpViewModule())
        .build()
        .inject(this);
  }

  @NonNull @Override protected PresenterFactory<SignUpPresenter> getPresenterFactory() {
    return mPresenterFactory;
  }

  @Override public String getPassword() {
    return etPassword.getText().toString().trim();
  }

  @Override public String getEmail() {
    return etEmail.getText().toString().trim();
  }

  @OnClick(R.id.btn_register) void register() {
    mPresenter.register();
  }

  @Override public void onBackPressed() {
    super.onBackPressed();
    login();
  }

  @OnClick(R.id.btn_login) void login() {
    Intent intent = new Intent(this, LoginActivity.class);
    ActivityOptions options =
        ActivityOptions.makeSceneTransitionAnimation(this, Pair.create(logo, "logo_header"),
            Pair.create(vEmail, "email"), Pair.create(vPass, "password"));
    startActivity(intent, options.toBundle());
    finishAfterTransition();
  }

  private void setupWindowAnimations() {
    Slide fade = new Slide();
    fade.setDuration(1000);
    fade.setSlideEdge(Gravity.END);
    getWindow().setEnterTransition(fade);
    getWindow().setExitTransition(fade);
  }

  @Override public void showProgress() {
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override public void showToast(String message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }

  @Override public void gotoMain() {
    Intent i = new Intent(this, MainActivity.class);
    startActivity(i);
    supportFinishAfterTransition();
  }

  @Override public void hideProgress() {
    progressBar.setVisibility(View.GONE);
  }
}
