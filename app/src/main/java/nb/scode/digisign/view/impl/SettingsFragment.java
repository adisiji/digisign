package nb.scode.digisign.view.impl;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
  @BindView(R.id.content_cache_size) TextView tvCacheSize;
  @BindView(R.id.tv_content_photo_size) TextView tvPhotoSize;

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

  @OnClick(R.id.btn_clear_cache) void clearCache() {
    mPresenter.clearCache();
  }

  @OnClick(R.id.btn_clear_photo_cache) void clearPhotoCache() {
    mPresenter.clearPhotoCache();
  }

  @Override public void showToast(String message) {
    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
  }

  @Override public void setCacheSize(String cacheSize) {
    tvCacheSize.setText(cacheSize);
  }

  @Override public void setPhotoSize(String photoSize) {
    tvPhotoSize.setText(photoSize);
  }
}
