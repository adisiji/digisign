package nb.scode.digisign.view.impl;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import javax.inject.Inject;
import nb.scode.digisign.R;
import nb.scode.digisign.injection.AddSignerViewModule;
import nb.scode.digisign.injection.AppComponent;
import nb.scode.digisign.injection.DaggerAddSignerViewComponent;
import nb.scode.digisign.presenter.AddSignerPresenter;
import nb.scode.digisign.presenter.loader.PresenterFactory;
import nb.scode.digisign.view.AddSignerView;

public final class AddSignerActivity extends BaseActivity<AddSignerPresenter, AddSignerView>
    implements AddSignerView {
  @Inject PresenterFactory<AddSignerPresenter> mPresenterFactory;
  @BindView(R.id.toolbar) Toolbar toolbar;

  // Your presenter is available using the mPresenter variable

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_signer);
    ButterKnife.bind(this);

    setSupportActionBar(toolbar);
    setupToolbar(getString(R.string.title_page_add_signer));
    // Do not call mPresenter from here, it will be null! Wait for onStart or onPostCreate.
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
}
