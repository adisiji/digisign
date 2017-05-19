package nb.scode.digisign.view.impl;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import javax.inject.Inject;
import nb.scode.digisign.R;
import nb.scode.digisign.injection.AppComponent;
import nb.scode.digisign.injection.DaggerLoginViewComponent;
import nb.scode.digisign.injection.LoginViewModule;
import nb.scode.digisign.presenter.LoginPresenter;
import nb.scode.digisign.presenter.loader.PresenterFactory;
import nb.scode.digisign.view.LoginView;
import timber.log.Timber;

public final class LoginActivity extends BaseActivity<LoginPresenter, LoginView>
    implements LoginView, GoogleApiClient.OnConnectionFailedListener {
  private final int RC_SIGN_IN = 9909;
  @Inject PresenterFactory<LoginPresenter> mPresenterFactory;
  @BindView(R.id.sign_in_google_button) SignInButton signInButton;
  @BindView(R.id.email) EditText etEmail;
  @BindView(R.id.password) EditText etPassword;
  @BindView(R.id.progressBar) ProgressBar progressBar;
  private GoogleApiClient googleApiClient;
  private ProgressDialog progressDialog;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    ButterKnife.bind(this);

    // Configure sign-in to request the user's ID, email address, and basic
    // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
    GoogleSignInOptions gso =
        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(
            getString(R.string.default_web_client_id)).requestEmail().build();
    googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this)
        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
        .build();
    setupProgress();
    // Do not call mPresenter from here, it will be null! Wait for onStart or onPostCreate.
  }

  private void setupProgress() {
    progressDialog = new ProgressDialog(this);
    progressDialog.setIndeterminate(true);
    progressDialog.setMessage("Loading...");
  }

  @Override protected void onStart() {
    super.onStart();
    mPresenter.checkUserSignedIn();
  }

  @Override public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    Timber.e("onConnectionFailed(): " + connectionResult.getErrorMessage());
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

  @OnClick(R.id.btn_signup) void register() {
    Intent i = new Intent(this, SignUpActivity.class);
    startActivity(i);
  }

  @OnClick(R.id.btn_login) void login() {
    mPresenter.login();
  }

  @OnClick(R.id.sign_in_google_button) void signInGoogle() {
    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
    startActivityForResult(signInIntent, RC_SIGN_IN);
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    mPresenter.onViewAttached(this);
    // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
    if (requestCode == RC_SIGN_IN) {
      GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
      if (result.isSuccess()) {
        mPresenter.firebaseAuthWithGoogle(result.getSignInAccount());
      } else {
        showToast("Failed to use this account");
      }
    }
  }

  @Override public void gotoMain() {
    Intent i = new Intent(this, MainActivity.class);
    startActivity(i);
    finish();
  }

  @Override public void showToast(String message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }

  @Override public String getEmail() {
    return etEmail.getText().toString().trim();
  }

  @Override public String getPassword() {
    return etPassword.getText().toString().trim();
  }

  @Override public void showProgress() {
    progressDialog.show();
  }

  @Override public void hideProgress() {
    progressDialog.dismiss();
  }
}
