package nb.scode.digisign.view.impl;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import javax.inject.Inject;
import nb.scode.digisign.R;
import nb.scode.digisign.injection.AllDocViewModule;
import nb.scode.digisign.injection.AppComponent;
import nb.scode.digisign.injection.DaggerAllDocViewComponent;
import nb.scode.digisign.presenter.AllDocPresenter;
import nb.scode.digisign.presenter.loader.PresenterFactory;
import nb.scode.digisign.view.AllDocView;

public final class AllDocFragment extends BaseFragment<AllDocPresenter, AllDocView>
    implements AllDocView {
  @Inject PresenterFactory<AllDocPresenter> mPresenterFactory;

  // Your presenter is available using the mPresenter variable

  public AllDocFragment() {
    // Required empty public constructor
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_all_doc, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    // Your code here
    // Do not call mPresenter from here, it will be null! Wait for onStart
  }

  @Override protected void setupComponent(@NonNull AppComponent parentComponent) {
    DaggerAllDocViewComponent.builder()
        .appComponent(parentComponent)
        .allDocViewModule(new AllDocViewModule())
        .build()
        .inject(this);
  }

  @NonNull @Override protected PresenterFactory<AllDocPresenter> getPresenterFactory() {
    return mPresenterFactory;
  }
}
