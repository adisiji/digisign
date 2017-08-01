package nb.scode.digisign.view.impl;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import javax.inject.Inject;
import nb.scode.digisign.R;
import nb.scode.digisign.injection.AppComponent;
import nb.scode.digisign.injection.DaggerHomeViewComponent;
import nb.scode.digisign.injection.HomeViewModule;
import nb.scode.digisign.presenter.HomePresenter;
import nb.scode.digisign.presenter.loader.PresenterFactory;
import nb.scode.digisign.view.HomeView;
import nb.scode.digisign.view.dialog.SignFile;
import timber.log.Timber;

public final class HomeFragment extends BaseFragment<HomePresenter, HomeView>
    implements HomeView, SignFile.CallbackSendFile {
  @Inject PresenterFactory<HomePresenter> mPresenterFactory;
  private Unbinder unbinder;

  // Your presenter is available using the mPresenter variable

  public HomeFragment() {
    // Required empty public constructor
  }

  @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_home, container, false);
    unbinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    // Your code here
    // Do not call mPresenter from here, it will be null! Wait for onStart
  }

  @Override protected void setupComponent(@NonNull AppComponent parentComponent) {
    DaggerHomeViewComponent.builder()
        .appComponent(parentComponent)
        .homeViewModule(new HomeViewModule())
        .build()
        .inject(this);
  }

  @NonNull @Override protected PresenterFactory<HomePresenter> getPresenterFactory() {
    return mPresenterFactory;
  }

  @Override public void selectFile() {
    Intent i = new Intent(getContext(), ChoosePdfActivity.class);
    startActivity(i);
  }

  @Override public void takePhoto() {
    Intent intent = new Intent(getContext(), TakePhotoActivity.class);
    startActivity(intent);
  }

  @OnClick(R.id.btn_sign_file) void sign() {
    FragmentManager fm = getFragmentManager();
    SignFile signFile = new SignFile();
    signFile.setTargetFragment(this, 300);
    signFile.show(fm, "fragment_sign");
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
    Timber.d("onDestroyView(): ok");
  }
}
