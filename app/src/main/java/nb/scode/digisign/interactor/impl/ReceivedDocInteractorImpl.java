package nb.scode.digisign.interactor.impl;

import java.io.File;
import javax.inject.Inject;
import nb.scode.digisign.data.DataTask;
import nb.scode.digisign.data.local.LocalTask;
import nb.scode.digisign.data.remote.ApiTask;
import nb.scode.digisign.interactor.ReceivedDocInteractor;
import timber.log.Timber;

import static nb.scode.digisign.data.remote.ApiTask.PUBLIC_KEY;

public final class ReceivedDocInteractorImpl implements ReceivedDocInteractor {

  private final DataTask dataTask;
  private File zipFile;
  private File dirZip;
  private File sigFile;
  private File pubkey;
  private File oriFile;
  private String filename;
  private String fileType;

  @Inject public ReceivedDocInteractorImpl(DataTask dataTask) {
    this.dataTask = dataTask;
  }

  @Override public void setFileType(String type) {
    this.fileType = type;
  }

  @Override public void unzipFile(final CommonRListener listener) {
    dataTask.unZipFile(zipFile, dataTask.getCacheDir(), new LocalTask.CommonListener() {
      @Override public void onFinished() {
        listener.onSuccess();
      }

      @Override public void onError(String message) {
        listener.onFailed(message);
      }

      @Override public void onProcess() {
        listener.onProcess();
      }
    });
  }

  @Override public void downloadFile(String link, final CommonRListener listener) {
    String separator = "%2Fpublic%2F";
    String zip_ext = ".zip";
    int sepIndex = link.indexOf(separator);
    int zipIndex = link.indexOf(zip_ext);

    int sepLength = separator.length();

    filename = link.substring(sepIndex + sepLength, zipIndex);
    Timber.d("downloadFile(): filename => " + filename); // ex: 141241231 *without .zip extension
    zipFile = dataTask.createFileInCache(filename, "zip");
    dataTask.downloadFile(zipFile, link, new ApiTask.CommonAListener() {
      @Override public void onProcess() {
        listener.onProcess();
      }

      @Override public void onSuccess() {
        listener.onSuccess();
      }

      @Override public void onFailed(String message) {
        listener.onFailed(message);
      }
    });
  }

  @Override public void checkingFiles(CommonRListener listener) {
    dirZip = new File(dataTask.getCacheDir(), File.separator + filename);
    File[] files = dirZip.listFiles();
    String sig = "sig";

    for (File file : files) {
      String ext = getFileExt(file.getName());
      if (ext.equals(sig)) {
        sigFile = file;
      } else if (ext.equals(fileType)) {
        oriFile = file;
      } else {
        listener.onFailed("Data is not valid!");
        return;
      }
    }
    listener.onSuccess();
  }

  private String getFileExt(String filenamez) {
    int z = filenamez.length();
    int i = filenamez.lastIndexOf('.');
    return filenamez.substring(i + 1, z);
  }

  @Override public void downloadPublicKey(String senderkey, final CommonRListener listener) {
    pubkey = new File(dirZip, PUBLIC_KEY);
    dataTask.downloadPublicKey(pubkey, senderkey, new ApiTask.CommonAListener() {
      @Override public void onProcess() {
        listener.onProcess();
      }

      @Override public void onSuccess() {
        listener.onSuccess();
      }

      @Override public void onFailed(String message) {
        listener.onFailed(message);
      }
    });
  }

  @Override public void verifySign(final CommonRListener listener) {
    dataTask.verifySignature(pubkey, sigFile, oriFile, new LocalTask.CommonListener() {
      @Override public void onFinished() {
        listener.onSuccess();
      }

      @Override public void onError(String message) {
        listener.onFailed(message);
      }

      @Override public void onProcess() {
        listener.onProcess();
      }
    });
  }
}