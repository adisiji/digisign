package nb.scode.digisign.view.impl;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.io.File;
import java.io.IOException;
import javax.inject.Inject;
import mehdi.sakout.fancybuttons.FancyButton;
import nb.scode.digisign.R;
import nb.scode.digisign.injection.AppComponent;
import nb.scode.digisign.injection.DaggerPrepSignViewComponent;
import nb.scode.digisign.injection.PrepSignViewModule;
import nb.scode.digisign.presenter.ChoosePdfPresenter;
import nb.scode.digisign.presenter.loader.PresenterFactory;
import nb.scode.digisign.view.ChoosePdfView;
import timber.log.Timber;

import static nb.scode.digisign.view.impl.Constants.BUNDLE_FILE_NAME;
import static nb.scode.digisign.view.impl.Constants.URI_BUNDLE_KEY;

public final class ChoosePdfActivity extends BaseActivity<ChoosePdfPresenter, ChoosePdfView>
    implements ChoosePdfView {
  // A request code's purpose is to match the result of a "startActivityForResult" with
  // the type of the original request.  Choose any value.
  private static final int READ_REQUEST_CODE = 1337;
  private static final int PERMISSIONS_REQUEST_EXT_STORAGE = 212;
  @Inject PresenterFactory<ChoosePdfPresenter> mPresenterFactory;
  @Nullable @BindView(R.id.iv_file_preview) ImageView ivPreview;
  @Nullable @BindView(R.id.title_file_name) TextView tvFileName;
  @Nullable @BindView(R.id.title_size) TextView tvFileSize;
  @BindView(R.id.btn_add_signer) FancyButton btnReceiver;
  @Nullable @BindView(R.id.toolbar) Toolbar toolbar;
  @Nullable private String uripdf = null;
  @Nullable private String filename = null;
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
    setContentView(R.layout.activity_prep_sign);
    ButterKnife.bind(this);
    // Setup toolbar
    setSupportActionBar(toolbar);
    setupToolbar(getString(R.string.title_page_choose_signer));
    checkPermission();
    // Do not call mPresenter from here, it will be null! Wait for onStart or onPostCreate.
  }

  private void checkPermission() {
    int permissionCheck =
        ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
      {
        // No explanation needed, we can request the permission.
        ActivityCompat.requestPermissions(this,
            new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
            PERMISSIONS_REQUEST_EXT_STORAGE);
      }
    } else {
      performSearch();
    }
  }

  @Override protected void setupComponent(@NonNull AppComponent parentComponent) {
    DaggerPrepSignViewComponent.builder()
        .appComponent(parentComponent)
        .prepSignViewModule(new PrepSignViewModule())
        .build()
        .inject(this);
  }

  @NonNull @Override protected PresenterFactory<ChoosePdfPresenter> getPresenterFactory() {
    return mPresenterFactory;
  }

  @Override public void onRequestPermissionsResult(int requestCode, String permissions[],
      int[] grantResults) {
    switch (requestCode) {
      case PERMISSIONS_REQUEST_EXT_STORAGE: {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          performSearch();
        } else {
          // permission denied, boo! Disable the
          // functionality that depends on this permission.
          Timber.e("onRequestPermissionsResult(): failed let's finish it now!");
          onBackPressed();
        }
        return;
      }
    }
  }

  @Override public void onBackPressed() {
    super.onBackPressed();
    finish();
  }

  private void performSearch() {
    // BEGIN_INCLUDE (use_open_document_intent)
    // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file browser.
    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

    // Filter to only show results that can be "opened", such as a file (as opposed to a list
    // of contacts or timezones)
    intent.addCategory(Intent.CATEGORY_OPENABLE);

    // Filter to show only images, using the image MIME data type.
    // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
    // To search for all documents available via installed storage providers, it would be
    // "*/*".
    intent.setType("application/pdf");

    startActivityForResult(intent, READ_REQUEST_CODE);
    // END_INCLUDE (use_open_document_intent)
  }

  @Override public void onActivityResult(int requestCode, int resultCode,
      @Nullable Intent resultData) {
    // BEGIN_INCLUDE (parse_open_document_response)
    // The ACTION_OPEN_DOCUMENT intent was sent with the request code READ_REQUEST_CODE.
    // If the request code seen here doesn't match, it's the response to some other intent,
    // and the below code shouldn't run at all.
    if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
      mPresenter.onViewAttached(this);
      // The document selected by the user won't be returned in the intent.
      // Instead, a URI to that document will be contained in the return intent
      // provided to this method as a parameter.  Pull that uri using "resultData.getData()"
      if (resultData != null) {
        Uri uri = resultData.getData();
        uripdf = uri.toString();
      }
      // END_INCLUDE (parse_open_document_response)
    }
  }

  private void openRenderer(Uri uri) {
    mPresenter.getFilePdf(uripdf);
  }

  @Override protected void onResume() {
    super.onResume();
    Timber.d("onResume(): uripdf => " + uripdf);
    if (uripdf != null) {
      openRenderer(Uri.parse(uripdf));
    }
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

  @Override public void setFileName(String fileName) {
    tvFileName.setText(fileName);
    this.filename = fileName;
  }

  @Override public void setFileSize(String fileSize) {
    tvFileSize.setText(fileSize);
  }

  @OnClick(R.id.btn_choose_doc) void chooseDoc() {
    performSearch();
  }

  @OnClick(R.id.btn_add_signer) void addSigner() {
    Intent i = new Intent(this, AddSignerActivity.class);
    i.putExtra(URI_BUNDLE_KEY, uripdf);
    i.putExtra(BUNDLE_FILE_NAME, filename);
    startActivity(i);
  }

  @Override public void setEnableBtnReceiver() {
    btnReceiver.setEnabled(true);
  }

  @Override public void setDisableBtnReceiver() {
    btnReceiver.setEnabled(false);
  }
}
