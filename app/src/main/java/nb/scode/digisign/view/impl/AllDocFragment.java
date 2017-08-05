package nb.scode.digisign.view.impl;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import javax.inject.Inject;
import nb.scode.digisign.R;
import nb.scode.digisign.injection.AllDocViewModule;
import nb.scode.digisign.injection.AppComponent;
import nb.scode.digisign.injection.DaggerAllDocViewComponent;
import nb.scode.digisign.presenter.AllDocPresenter;
import nb.scode.digisign.presenter.loader.PresenterFactory;
import nb.scode.digisign.view.AllDocView;
import nb.scode.digisign.view.adapter.AllDocAdapter;
import nb.scode.digisign.view.busmodel.SpinnerMenu;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public final class AllDocFragment extends BaseFragment<AllDocPresenter, AllDocView>
    implements AllDocView {
  @Inject PresenterFactory<AllDocPresenter> mPresenterFactory;
  @Nullable @BindView(R.id.rv) RecyclerView recyclerView;
  private ProgressDialog progressDialog;
  private AllDocAdapter adapter;

  public AllDocFragment() {
    // Required empty public constructor
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EventBus.getDefault().register(this);
  }

  @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_all_doc, container, false);
  }

  @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ButterKnife.bind(this, view);
    progressDialog = new ProgressDialog(getContext());
    progressDialog.setIndeterminate(true);
    progressDialog.setMessage("Loading...");
    progressDialog.setCancelable(false);
    // Do not call mPresenter from here, it will be null! Wait for onStart
  }

  @Override public void onStart() {
    super.onStart();
    mPresenter.getAllPost();
  }

  @Subscribe(threadMode = ThreadMode.MAIN) public void MenuEvent(@NonNull SpinnerMenu spinnerMenu) {
    int i = spinnerMenu.getMenu();
    mPresenter.processSpinnerMenu(i);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().unregister(this);
    }
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

  @Override public void showFirstData() {
    adapter = new AllDocAdapter(mPresenter);
    RecyclerView.LayoutManager layoutManager =
        new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.setAdapter(adapter);
    recyclerView.invalidate();
  }

  @Override public void showLoading() {
    progressDialog.show();
  }

  @Override public void hideLoading() {
    progressDialog.dismiss();
  }

  @Override public void notifyAdapter() {
    adapter.notifyDataSetChanged();
  }
}
