package nb.scode.digisign.view.impl;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import java.io.File;
import java.io.IOException;
import javax.inject.Inject;
import nb.scode.digisign.R;
import nb.scode.digisign.injection.AppComponent;
import nb.scode.digisign.injection.DaggerReceivedDocViewComponent;
import nb.scode.digisign.injection.ReceivedDocViewModule;
import nb.scode.digisign.presenter.ReceivedDocPresenter;
import nb.scode.digisign.presenter.loader.PresenterFactory;
import nb.scode.digisign.view.ReceivedDocView;
import nb.scode.digisign.view.dialog.InfoFile;
import timber.log.Timber;

public final class ReceivedDocActivity extends BaseActivity<ReceivedDocPresenter, ReceivedDocView>
    implements ReceivedDocView, InfoFile.CallbackInfoFile {
  @Inject PresenterFactory<ReceivedDocPresenter> mPresenterFactory;
  @Nullable @BindView(R.id.status_verified) TextView tvStatus;
  @Nullable @BindView(R.id.iv_file_preview) ImageView ivPreview;
  @Nullable @BindView(R.id.toolbar) Toolbar toolbar;
  private Intent intent;
  private ProgressDialog progressDialog;
  /**
   * File descriptor of the PDF.
   */
  private ParcelFileDescriptor mFileDescriptor;

  /**
   * {@link android.graphics.pdf.PdfRenderer} to render the PDF.
   */
  private PdfRenderer mPdfRenderer;

  /**
   * Page that is currently shown on the screen.
   */
  private PdfRenderer.Page mCurrentPage;
  // Your presenter is available using the mPresenter variable

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_received_doc);
    ButterKnife.bind(this);
    intent = getIntent();
    setProgressBar();
    setSupportActionBar(toolbar);
    setupToolbar(getString(R.string.title_page_received_doc));
    // Do not call mPresenter from here, it will be null! Wait for onStart or onPostCreate.
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.received_doc, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        onBackPressed();
        break;
      case R.id.menu_info: {
        showInfoDialog();
        break;
      }
    }
    return true;
  }

  @Override public void onBackPressed() {
    super.onBackPressed();
    Intent i = new Intent(this, MainActivity.class);
    startActivity(i);
    finish();
  }

  private void setProgressBar() {
    progressDialog = new ProgressDialog(this);
    progressDialog.setMessage("Loading");
    progressDialog.setIndeterminate(true);
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

  @Override public void setPdfRenderer(File file) {
    try {
      mFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
      // This is the PdfRenderer we use to render the PDF.
      mPdfRenderer = new PdfRenderer(mFileDescriptor);
    } catch (IOException e) {
      Timber.e("setPdfRenderer(): " + e.getMessage());
    }

    // Make sure to close the current page before opening another one.
    if (null != mCurrentPage) {
      mCurrentPage.close();
    }
    mCurrentPage = mPdfRenderer.openPage(0);
    final Bitmap bitmap = Bitmap.createBitmap(mCurrentPage.getWidth(), mCurrentPage.getHeight(),
        Bitmap.Config.ARGB_8888);
    // Here, we render the page onto the Bitmap.
    // To render a portion of the page, use the second and third parameter. Pass nulls to get
    // the default result.
    // Pass either RENDER_MODE_FOR_DISPLAY or RENDER_MODE_FOR_PRINT for the last parameter.
    mCurrentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
    // We are ready to show the Bitmap to user.
    ivPreview.setImageBitmap(bitmap);
  }

  /**
   * Closes the {@link android.graphics.pdf.PdfRenderer} and related resources.
   *
   * @throws java.io.IOException When the PDF file cannot be closed.
   */
  private void closeRenderer() throws IOException {
    if (null != mCurrentPage) {
      mCurrentPage.close();
    }

    if (mPdfRenderer != null) {
      mPdfRenderer.close();
    }

    if (mFileDescriptor != null) {
      mFileDescriptor.close();
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    try {
      closeRenderer();
    } catch (IOException e) {
      Timber.e("onDestroy(): " + e.getMessage());
    }
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

  @Override public String fileName() {
    return mPresenter.getFileName();
  }

  @Override public String fileDesc() {
    return getDescIntent();
  }

  @Override public String fileTime() {
    return mPresenter.getTimefromIntent();
  }

  @Override public String fileFrom() {
    return getFromIntent();
  }

  @Override public String fileSize() {
    return mPresenter.getFileSize();
  }

  @Override public void showLoading() {
    progressDialog.show();
  }

  @Override public void hideLoading() {
    progressDialog.dismiss();
  }

  @Override public void setFileStatus(String fileStatus) {
    tvStatus.setText(fileStatus);
  }

  private void showInfoDialog() {
    FragmentManager fm = getSupportFragmentManager();
    InfoFile signFile = new InfoFile();
    signFile.show(fm, "fragment_sign");
  }

  @Override public void setGreenStatus() {
    tvStatus.setTextColor(ContextCompat.getColor(this, R.color.green_status));
  }

  @Override public void setRedStatus() {
    tvStatus.setTextColor(ContextCompat.getColor(this, R.color.red_status));
  }
}
