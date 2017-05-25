package nb.scode.digisign.view.impl;

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
import java.util.List;
import javax.inject.Inject;
import nb.scode.digisign.R;
import nb.scode.digisign.injection.AllDocViewModule;
import nb.scode.digisign.injection.AppComponent;
import nb.scode.digisign.injection.DaggerAllDocViewComponent;
import nb.scode.digisign.presenter.AllDocPresenter;
import nb.scode.digisign.presenter.loader.PresenterFactory;
import nb.scode.digisign.view.AllDocView;
import nb.scode.digisign.view.adapter.AllDocAdapter;
import nb.scode.digisign.view.model.ItemAllDoc;
import timber.log.Timber;

public final class AllDocFragment extends BaseFragment<AllDocPresenter, AllDocView>
    implements AllDocView {
  @Inject PresenterFactory<AllDocPresenter> mPresenterFactory;
  @BindView(R.id.rv) RecyclerView recyclerView;

  private AllDocAdapter.AllDocAdpListener listener = new AllDocAdapter.AllDocAdpListener() {
    @Override public void onClick(String key) {
      Timber.d("onClick(): key => " + key);
    }
  };

  public AllDocFragment() {
    // Required empty public constructor
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_all_doc, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ButterKnife.bind(this, view);
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

  @Override public void showAllDocItems(List<ItemAllDoc> docList) {
    final AllDocAdapter adapter = new AllDocAdapter(docList, listener);
    RecyclerView.LayoutManager layoutManager =
        new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.setAdapter(adapter);
    recyclerView.invalidate();
  }
}
