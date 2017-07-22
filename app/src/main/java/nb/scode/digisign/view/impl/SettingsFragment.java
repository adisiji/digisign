package nb.scode.digisign.view.impl;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import butterknife.BindView;
import butterknife.ButterKnife;
import javax.inject.Inject;
import nb.scode.digisign.R;
import nb.scode.digisign.injection.AppComponent;
import nb.scode.digisign.injection.DaggerSettingsViewComponent;
import nb.scode.digisign.injection.SettingsViewModule;
import nb.scode.digisign.presenter.SettingsPresenter;
import nb.scode.digisign.presenter.loader.PresenterFactory;
import nb.scode.digisign.view.SettingsView;

public final class SettingsFragment extends BaseFragment<SettingsPresenter, SettingsView>
    implements SettingsView {
  @Inject PresenterFactory<SettingsPresenter> mPresenterFactory;
  @Nullable @BindView(R.id.wv_credits) WebView webView;
  // Your presenter is available using the mPresenter variable

  public SettingsFragment() {
    // Required empty public constructor
  }

  @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_settings, container, false);
  }

  @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ButterKnife.bind(this, view);
    webView.loadUrl("file:///android_asset/webs/Credits.html");
    // Do not call mPresenter from here, it will be null! Wait for onStart
  }

  @Override protected void setupComponent(@NonNull AppComponent parentComponent) {
    DaggerSettingsViewComponent.builder()
        .appComponent(parentComponent)
        .settingsViewModule(new SettingsViewModule())
        .build()
        .inject(this);
  }

  @NonNull @Override protected PresenterFactory<SettingsPresenter> getPresenterFactory() {
    return mPresenterFactory;
  }
}
