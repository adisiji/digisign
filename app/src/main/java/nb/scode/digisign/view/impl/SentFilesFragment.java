package nb.scode.digisign.view.impl;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import javax.inject.Inject;
import nb.scode.digisign.R;
import nb.scode.digisign.injection.AppComponent;
import nb.scode.digisign.injection.DaggerSentFilesViewComponent;
import nb.scode.digisign.injection.SentFilesViewModule;
import nb.scode.digisign.presenter.SentFilesPresenter;
import nb.scode.digisign.presenter.loader.PresenterFactory;
import nb.scode.digisign.view.SentFilesView;

public final class SentFilesFragment extends BaseFragment<SentFilesPresenter, SentFilesView>
    implements SentFilesView {
  @Inject PresenterFactory<SentFilesPresenter> mPresenterFactory;

  // Your presenter is available using the mPresenter variable

  public SentFilesFragment() {
    // Required empty public constructor
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_sent_files, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    // Your code here
    // Do not call mPresenter from here, it will be null! Wait for onStart
  }

  @Override protected void setupComponent(@NonNull AppComponent parentComponent) {
    DaggerSentFilesViewComponent.builder()
        .appComponent(parentComponent)
        .sentFilesViewModule(new SentFilesViewModule())
        .build()
        .inject(this);
  }

  @NonNull @Override protected PresenterFactory<SentFilesPresenter> getPresenterFactory() {
    return mPresenterFactory;
  }
}
