package nb.scode.digisign.view.impl;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import butterknife.ButterKnife;
import javax.inject.Inject;
import nb.scode.digisign.R;
import nb.scode.digisign.injection.AppComponent;
import nb.scode.digisign.injection.DaggerReceivedDocViewComponent;
import nb.scode.digisign.injection.ReceivedDocViewModule;
import nb.scode.digisign.presenter.ReceivedDocPresenter;
import nb.scode.digisign.presenter.loader.PresenterFactory;
import nb.scode.digisign.view.ReceivedDocView;

public final class ReceivedDocActivity extends BaseActivity<ReceivedDocPresenter, ReceivedDocView>
    implements ReceivedDocView {
  @Inject PresenterFactory<ReceivedDocPresenter> mPresenterFactory;
  private Intent intent;
  // Your presenter is available using the mPresenter variable

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_received_doc);
    ButterKnife.bind(this);
    intent = getIntent();
    // Your code here
    // Do not call mPresenter from here, it will be null! Wait for onStart or onPostCreate.
  }

  @Override protected void setupComponent(@NonNull AppComponent parentComponent) {
    DaggerReceivedDocViewComponent.builder()
        .appComponent(parentComponent)
        .receivedDocViewModule(new ReceivedDocViewModule())
        .build()
        .inject(this);
  }

  @NonNull @Override protected PresenterFactory<ReceivedDocPresenter> getPresenterFactory() {
    return mPresenterFactory;
  }

  @Override public boolean isIntentEmpty() {
    return (intent == null);
  }

  @Override public String getDescIntent() {
    return intent.getStringExtra("desc");
  }

  @Override public String getDownloadLink() {
    return intent.getStringExtra("linkdown");
  }

  @Override public String getFromIntent() {
    return intent.getStringExtra("origin");
  }

  @Override public String getTimeIntent() {
    return intent.getStringExtra("times");
  }

  @Override public String getTypeIntent() {
    return intent.getStringExtra("type");
  }

  @Override public String getSenderKeyIntent() {
    return intent.getStringExtra("senderkey");
  }

  @Override public void showLoading() {

  }

  @Override public void hideLoading() {

  }
}
