package nb.scode.digisign.presenter.impl;

import android.support.annotation.NonNull;
import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import javax.inject.Inject;
import nb.scode.digisign.interactor.ReceivedDocInteractor;
import nb.scode.digisign.presenter.ReceivedDocPresenter;
import nb.scode.digisign.view.ReceivedDocView;
import timber.log.Timber;

public final class ReceivedDocPresenterImpl extends BasePresenterImpl<ReceivedDocView>
    implements ReceivedDocPresenter {
  /**
   * The interactor
   */
  @NonNull private final ReceivedDocInteractor mInteractor;

  // The view is available using the mView variable

  @Inject public ReceivedDocPresenterImpl(@NonNull ReceivedDocInteractor interactor) {
    mInteractor = interactor;
  }

  @Override public void onStart(boolean viewCreated) {
    super.onStart(viewCreated);
    initReceivedDoc();
    // Your code here. Your view is available using mView and will not be null until next onStop()
  }

  @Override public void onStop() {
    // Your code here, mView will be null after this method until next onStart()

    super.onStop();
  }

  @Override public void onPresenterDestroyed() {
        /*
         * Your code here. After this method, your presenter (and view) will be completely destroyed
         * so make sure to cancel any HTTP call or database connection
         */

    super.onPresenterDestroyed();
  }

  private void initReceivedDoc() {
    if (!mView.isIntentEmpty()) {
      mView.showLoading();
      String downlink = mView.getDownloadLink();
      mInteractor.setFileType(mView.getTypeIntent());
      mInteractor.downloadFile(downlink, new ReceivedDocInteractor.CommonRListener() {
        @Override public void onProcess() {

        }

        @Override public void onSuccess() {
          Timber.d("onSuccess(): downloadfile");
          unzipFile();
        }

        @Override public void onFailed(String message) {

        }
      });
    }
  }

  private void unzipFile() {
    mInteractor.unzipFile(new ReceivedDocInteractor.CommonRListener() {
      @Override public void onProcess() {

      }

      @Override public void onSuccess() {
        Timber.d("onSuccess(): unzip ^^");
        checkFiles();
      }

      @Override public void onFailed(String message) {

      }
    });
  }

  private void checkFiles() {
    mInteractor.checkingFiles(new ReceivedDocInteractor.CommonRListener() {
      @Override public void onProcess() {

      }

      @Override public void onSuccess() {
        downloadPublicKey();
        Timber.d("onSuccess(): OK ^^");
      }

      @Override public void onFailed(String message) {
        Timber.e("onFailed(): checking => " + message);
      }
    });
  }

  private void downloadPublicKey() {
    mInteractor.downloadPublicKey(mView.getSenderKeyIntent(),
        new ReceivedDocInteractor.CommonRListener() {
          @Override public void onProcess() {
            Timber.d("onProcess(): download pubkey");
          }

          @Override public void onSuccess() {
            Timber.d("onSuccess(): download pubkey");
            verifySignature();
          }

          @Override public void onFailed(String message) {
            Timber.d("onFailed(): download pubkey " + message);
          }
        });
  }

  private void verifySignature() {
    mInteractor.verifySign(new ReceivedDocInteractor.CommonRListener() {
      @Override public void onProcess() {

      }

      @Override public void onSuccess() {
        Timber.d("onSuccess(): File is valid");
        mView.setFileStatus("This Doc has been verified");
        mView.setGreenStatus();
        setupUI();
      }

      @Override public void onFailed(String message) {
        Timber.e("onFailed(): " + message);
        mView.setFileStatus("This Doc can not be verified");
        mView.setRedStatus();
        setupUI();
      }
    });
  }

  private void setupUI() {
    mInteractor.showingPdf(new ReceivedDocInteractor.showPdfListener() {
      @Override public void onProcess() {

      }

      @Override public void onSuccess(File file) {
        mView.setPdfRenderer(file);
        mView.hideLoading();
      }

      @Override public void onFailed(String message) {
        mView.hideLoading();
      }
    });
  }

  @Override public String getTimefromIntent() {
    Long xx = Long.decode(mView.getTimeIntent());
    Date date = new Date(xx);
    return DateFormat.getDateInstance(DateFormat.LONG).format(date);
  }

  @Override public String getFileName() {
    return mInteractor.getOriFileName();
  }

  @Override public String getFileSize() {
    return mInteractor.getOriFileSize();
  }
}