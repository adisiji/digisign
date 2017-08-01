package nb.scode.digisign.view.impl;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.inject.Inject;
import nb.scode.digisign.R;
import nb.scode.digisign.injection.AppComponent;
import nb.scode.digisign.injection.DaggerTakePhotoViewComponent;
import nb.scode.digisign.injection.TakePhotoViewModule;
import nb.scode.digisign.presenter.TakePhotoPresenter;
import nb.scode.digisign.presenter.loader.PresenterFactory;
import nb.scode.digisign.view.TakePhotoView;
import timber.log.Timber;

import static nb.scode.digisign.view.impl.Constants.BUNDLE_FILE_NAME;
import static nb.scode.digisign.view.impl.Constants.URI_BUNDLE_KEY;

public final class TakePhotoActivity extends BaseActivity<TakePhotoPresenter, TakePhotoView>
    implements TakePhotoView {
  private static final int REQUEST_IMAGE_CAPTURE = 1;
  @Inject PresenterFactory<TakePhotoPresenter> mPresenterFactory;
  @BindView(R.id.iv_file_preview) ImageView ivPhoto;
  @BindView(R.id.title_size) TextView fileSize;
  @BindView(R.id.title_file_name) EditText etFileName;
  @Nullable @BindView(R.id.toolbar) Toolbar toolbar;
  private String mCurrentPhotoPath;

  // Your presenter is available using the mPresenter variable

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_take_photo);
    ButterKnife.bind(this);
    // Setup toolbar
    setSupportActionBar(toolbar);
    setupToolbar(getString(R.string.title_page_choose_signer));
    // Do not call mPresenter from here, it will be null! Wait for onStart or onPostCreate.
  }

  @Override protected void setupComponent(@NonNull AppComponent parentComponent) {
    DaggerTakePhotoViewComponent.builder()
        .appComponent(parentComponent)
        .takePhotoViewModule(new TakePhotoViewModule())
        .build()
        .inject(this);
  }

  @NonNull @Override protected PresenterFactory<TakePhotoPresenter> getPresenterFactory() {
    return mPresenterFactory;
  }

  private File createImageFile() throws IOException {
    // Create an image file name
    @SuppressLint("SimpleDateFormat") String timeStamp =
        new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String imageFileName = "JPEG_" + timeStamp + "_";
    File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    File image = File.createTempFile(imageFileName,  /* prefix */
        ".jpg",         /* suffix */
        storageDir      /* directory */);

    // Save a file: path for use with ACTION_VIEW intents
    mCurrentPhotoPath = image.getAbsolutePath();
    return image;
  }

  @OnClick(R.id.btn_take_photo) void dispatchTakePictureIntent() {
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
      // Create the File where the photo should go
      File photoFile = null;
      try {
        photoFile = createImageFile();
      } catch (IOException ex) {
        // Error occurred while creating the File
        ex.printStackTrace();
      }
      if (photoFile != null) {
        Uri photoURI =
            FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
      }
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
      //Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
      ivPhoto.setImageBitmap(getBitmap(mCurrentPhotoPath));
    }
  }

  /**
   * Method to scale down the image after taking photo, to showing in ImageView
   *
   * @param path The photo path
   * @return bitmap image
   */
  private Bitmap getBitmap(String path) {

    Uri uri = Uri.fromFile(new File(path));
    InputStream in = null;
    try {
      final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
      in = getContentResolver().openInputStream(uri);

      // Decode image size
      BitmapFactory.Options o = new BitmapFactory.Options();
      o.inJustDecodeBounds = true;
      BitmapFactory.decodeStream(in, null, o);
      in.close();

      int scale = 1;
      while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
        scale++;
      }
      Timber.d("getBitmap(): scale = "
          + scale
          + ", orig-width: "
          + o.outWidth
          + ", orig-height: "
          + o.outHeight);

      Bitmap b = null;
      in = getContentResolver().openInputStream(uri);
      if (scale > 1) {
        scale--;
        // scale to max possible inSampleSize that still yields an image
        // larger than target
        o = new BitmapFactory.Options();
        o.inSampleSize = scale;
        b = BitmapFactory.decodeStream(in, null, o);

        // resize to desired dimensions
        int height = b.getHeight();
        int width = b.getWidth();
        Timber.d(
            "getBitmap(): 1th scale operation dimenions - width: " + width + ", height: " + height);

        double y = Math.sqrt(IMAGE_MAX_SIZE / (((double) width) / height));
        double x = (y / height) * width;

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x, (int) y, true);
        b.recycle();
        b = scaledBitmap;

        System.gc();
      } else {
        b = BitmapFactory.decodeStream(in);
      }
      in.close();

      Timber.d("getBitmap(): bitmap size - width: " + b.getWidth() + ", height: " + b.getHeight());
      return b;
    } catch (IOException e) {
      Timber.e("getBitmap(): " + e.getMessage());
      return null;
    }
  }

  @OnClick(R.id.btn_add_signer) void addSigner() {
    if (mPresenter != null) {
      mPresenter.addSigner();
    }
  }

  @Override public String getPhotoPath() {
    return mCurrentPhotoPath;
  }

  @Override public String getFileName() {
    return etFileName.getText().toString();
  }

  @Override public void sendFile(String photoPath, String fieleName) {
    Intent i = new Intent(this, AddSignerActivity.class);
    i.putExtra(URI_BUNDLE_KEY, photoPath);
    i.putExtra(BUNDLE_FILE_NAME, fieleName);
    startActivity(i);
  }
}
