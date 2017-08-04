package nb.scode.digisign.view.impl;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.List;
import javax.inject.Inject;
import nb.scode.digisign.R;
import nb.scode.digisign.injection.AddSignerViewModule;
import nb.scode.digisign.injection.AppComponent;
import nb.scode.digisign.injection.DaggerAddSignerViewComponent;
import nb.scode.digisign.presenter.AddSignerPresenter;
import nb.scode.digisign.presenter.loader.PresenterFactory;
import nb.scode.digisign.view.AddSignerView;
import timber.log.Timber;

import static nb.scode.digisign.view.impl.Constants.BUNDLE_FILE_NAME;
import static nb.scode.digisign.view.impl.Constants.URI_BUNDLE_KEY;

public final class AddSignerActivity extends BaseActivity<AddSignerPresenter, AddSignerView>
    implements AddSignerView {
  @Inject PresenterFactory<AddSignerPresenter> mPresenterFactory;
  @Nullable @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.spinner_email) Spinner spinner;
  @Nullable @BindView(R.id.et_name_signer) EditText etName;
  @Nullable @BindView(R.id.et_desc_doc) EditText etDesc;
  private String uri;
  private String filenamez;
  private ProgressDialog progressDialog;

  // Your presenter is available using the mPresenter variable

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_signer);
    ButterKnife.bind(this);
    Intent intent = getIntent();
    if (intent != null) {
      uri = intent.getStringExtra(URI_BUNDLE_KEY);
      filenamez = intent.getStringExtra(BUNDLE_FILE_NAME);
      Timber.d("onCreate(): uri => " + uri);
    } else {
      onBackPressed();
    }
    setSupportActionBar(toolbar);
    setupToolbar(getString(R.string.title_page_add_signer));
    setupLoading();
    // Do not call mPresenter from here, it will be null! Wait for onStart or onPostCreate.
  }

  private void setupLoading() {
    progressDialog = new ProgressDialog(this);
    progressDialog.setIndeterminate(true);
    progressDialog.setMessage("Loading...");
    progressDialog.setCancelable(false);
  }

  @Override public void onBackPressed() {
    super.onBackPressed();
    finish();
  }

  @Override protected void setupComponent(@NonNull AppComponent parentComponent) {
    DaggerAddSignerViewComponent.builder()
        .appComponent(parentComponent)
        .addSignerViewModule(new AddSignerViewModule())
        .build()
        .inject(this);
  }

  @NonNull @Override protected PresenterFactory<AddSignerPresenter> getPresenterFactory() {
    return mPresenterFactory;
  }

  @OnClick(R.id.btn_send_doc) void sendDoc() {
    mPresenter.sendDocWithSign();
  }

  @Override public String getUriFile() {
    return uri;
  }

  @Override public void showListEmailUser(List<String> listEmail) {
    //AddSignerAdapter adapter = new AddSignerAdapter(this, listEmail);
    ArrayAdapter<String> adapter =
        new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listEmail);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        mPresenter.getUserName(i);
      }

      @Override public void onNothingSelected(AdapterView<?> adapterView) {

      }
    });
    spinner.setAdapter(adapter);
    mPresenter.getUserName(0);
  }

  private void clearText() {
    etName.setText(null);
  }

  @Override public void showOwnerName(String name) {
    etName.setText(name);
  }

  @NonNull @Override public String getEmail() {
    return spinner.getSelectedItem().toString();
  }

  @NonNull @Override public String getName() {
    return etName.getText().toString();
  }

  @NonNull @Override public String getDesc() {
    return etDesc.getText().toString();
  }

  @Override public String getFilename() {
    return filenamez;
  }

  @Override public void clearErrorEditText() {
    etName.setError(null);
  }

  @Override public void showErrorName(String message) {
    etName.setError(message);
  }

  @Override public void showLoading() {
    progressDialog.show();
  }

  @Override public void hideLoading() {
    progressDialog.dismiss();
  }

  @Override public void showToast(String message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }

  @Override public void gotoMain() {
    Intent intent = new Intent(this, MainActivity.class);
    startActivity(intent);
    finish();
  }
}
