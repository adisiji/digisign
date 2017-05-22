package nb.scode.digisign.view.impl;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.List;
import javax.inject.Inject;
import mehdi.sakout.fancybuttons.FancyButton;
import nb.scode.digisign.R;
import nb.scode.digisign.injection.AddSignerViewModule;
import nb.scode.digisign.injection.AppComponent;
import nb.scode.digisign.injection.DaggerAddSignerViewComponent;
import nb.scode.digisign.presenter.AddSignerPresenter;
import nb.scode.digisign.presenter.loader.PresenterFactory;
import nb.scode.digisign.view.AddSignerView;
import nb.scode.digisign.view.adapter.AddSignerAdapter;
import timber.log.Timber;

import static nb.scode.digisign.view.impl.Constants.URI_BUNDLE_KEY;

public final class AddSignerActivity extends BaseActivity<AddSignerPresenter, AddSignerView>
    implements AddSignerView {
  @Inject PresenterFactory<AddSignerPresenter> mPresenterFactory;
  @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.btn_other_signature) FancyButton btnOther;
  @BindView(R.id.btn_self_signature) FancyButton btnSelf;
  @BindView(R.id.et_email_signer) AutoCompleteTextView etEmail;
  @BindView(R.id.et_name_signer) EditText etName;

  // Your presenter is available using the mPresenter variable

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_signer);
    ButterKnife.bind(this);
    Intent intent = getIntent();
    if (intent != null) {
      String uri = intent.getStringExtra(URI_BUNDLE_KEY);
      Timber.d("onCreate(): uri => " + uri);
    } else {
      onBackPressed();
    }
    setSupportActionBar(toolbar);
    setupToolbar(getString(R.string.title_page_add_signer));
    // Do not call mPresenter from here, it will be null! Wait for onStart or onPostCreate.
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

  @OnClick(R.id.btn_self_signature) void showSelfSign() {
    clearText();
    btnSelf.setVisibility(View.GONE);
    btnOther.setVisibility(View.VISIBLE);
    mPresenter.getOwnerSignature();
  }

  @OnClick(R.id.btn_other_signature) void showOther() {
    clearText();
    btnOther.setVisibility(View.GONE);
    btnSelf.setVisibility(View.VISIBLE);
    mPresenter.getUserList();
  }

  @Override public void showListEmailUser(List<String> listEmail) {
    AddSignerAdapter adapter = new AddSignerAdapter(this, listEmail);
    etEmail.setAdapter(adapter);
    etEmail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Timber.d("onItemClick(): pos => " + i);
        mPresenter.getUserName(i);
      }
    });
    etEmail.setThreshold(1);
  }

  private void clearText() {
    etEmail.setText(null);
    etName.setText(null);
  }

  @Override public void showOwnerEmail(String email) {
    etEmail.setAdapter(null);
    etEmail.setText(email);
  }

  @Override public void showOwnerName(String name) {
    etName.setText(name);
  }
}
