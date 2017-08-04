package nb.scode.digisign.data;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import java.io.File;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import nb.scode.digisign.data.local.LocalTask;
import nb.scode.digisign.data.remote.ApiTask;
import nb.scode.digisign.data.remote.BusModel.UserBusPost;
import nb.scode.digisign.data.remote.FireModel.KeyUser;
import nb.scode.digisign.data.remote.FireModel.Post;

/**
 * Created by neobyte on 4/28/2017.
 */
@Singleton public class DataManager implements DataTask {

  private final LocalTask localTask;
  private final ApiTask apiTask;

  /**
   * Instantiates a new Data manager.
   *
   * @param localTask the preferences task
   * @param apiTask the api task
   */
  @Inject DataManager(LocalTask localTask, ApiTask apiTask) {
    this.localTask = localTask;
    this.apiTask = apiTask;
  }

  @Override public void clear() {

  }

  @Override public String getEmailUser() {
    return apiTask.getEmailUser();
  }

  @Override public boolean isEmailSame(String email) {
    return localTask.isEmailSame(email);
  }

  @Override public void setEmailPref(String email) {
    localTask.setEmailPref(email);
  }

  @Override public String getUserToken() {
    return localTask.getUserToken();
  }

  @Override public void setUserToken(String token) {
    localTask.setUserToken(token);
  }

  @Override public boolean isLocalKeyPairAvailable() {
    return localTask.isLocalKeyPairAvailable();
  }

  @Override public void createKey(final CommonListener listener) throws Exception {
    localTask.createKey(listener);
  }

  @Override public boolean isFirstUse() {
    return localTask.isFirstUse();
  }

  @Override public void setNotFirstUse() {
    localTask.setNotFirstUse();
  }

  @Override public void getPrepFilePdf(final String uripdf, final ListenerPrepPdf listenerPrepPdf) {
    localTask.getPrepFilePdf(uripdf, listenerPrepPdf);
  }

  @Override public File getPublicKey() {
    return localTask.getPublicKey();
  }

  @Override public File getPrivateKey() {
    return localTask.getPrivateKey();
  }

  @Override public void register(String email, String pass, CommonAListener listener) {
    apiTask.register(email, pass, listener);
  }

  @Override public void login(String email, String pass, CommonAListener listener) {
    apiTask.login(email, pass, listener);
  }

  @Override public void firebaseAuthWithGoogle(GoogleSignInAccount account,
      CommonAListener aListener) {
    apiTask.firebaseAuthWithGoogle(account, aListener);
  }

  @Override public void firebaseReAuth(String token, CommonAListener aListener) {
    apiTask.firebaseReAuth(token, aListener);
  }

  @Override public boolean isUserSignedIn() {
    return apiTask.isUserSignedIn();
  }

  @Override public UserBusPost getUserProfile() {
    return apiTask.getUserProfile();
  }

  @Override public void logout() {
    apiTask.logout();
  }

  @Override public void uploadKeyPair(File publickey, File privatekey, CommonAListener listener) {
    apiTask.uploadKeyPair(publickey, privatekey, listener);
  }

  @Override public void getUserPost() {
    apiTask.getUserPost();
  }

  @Override public void checkRemoteKeyPair(CommonAListener listener) {
    apiTask.checkRemoteKeyPair(listener);
  }

  @Override public void downloadKeyPair(File publickey, File privatekey, CommonAListener listener) {
    apiTask.downloadKeyPair(publickey, privatekey, listener);
  }

  @Override public KeyUser getOwnerKey() {
    return apiTask.getOwnerKey();
  }

  @Override public List<KeyUser> getListUser() {
    return apiTask.getListUser();
  }

  @Override public void createZip(CommonListener listener) {
    localTask.createZip(listener);
  }

  @Override public void unZipFile(File zipfile, File targetDir, CommonListener listener) {
    localTask.unZipFile(zipfile, targetDir, listener);
  }

  @Override public void createSignFile(String uripdf, String filename, String filetype,
      CommonListener listener) {
    localTask.createSignFile(uripdf, filename, filetype, listener);
  }

  @Override public File getFileToSend() {
    return localTask.getFileToSend();
  }

  @Override public void uploadSignFile(File signFile, UploadSignListener listener) {
    apiTask.uploadSignFile(signFile, listener);
  }

  @Override public void insertPostData(Post postData, CommonAListener listener) {
    apiTask.insertPostData(postData, listener);
  }

  @Override public void saveToken(String token) {
    apiTask.saveToken(token);
    localTask.setUserToken(token);
  }

  @Override public void initListUser(CommonAListener listener) {
    apiTask.initListUser(listener);
  }

  @Override public File createFileInCache(String filename, String ext) {
    return localTask.createFileInCache(filename, ext);
  }

  @Override public File getCacheDir() {
    return localTask.getCacheDir();
  }

  @Override public void downloadFile(File filezip, String url, CommonAListener listener) {
    apiTask.downloadFile(filezip, url, listener);
  }

  @Override public void downloadPublicKey(File filepub, String senderkey,
      CommonAListener listener) {
    apiTask.downloadPublicKey(filepub, senderkey, listener);
  }

  @Override public void verifySignature(File pubkey, File sigFile, File oriFile,
      CommonListener listener) {
    localTask.verifySignature(pubkey, sigFile, oriFile, listener);
  }

  @Override public void getPostReceived(GetListPostListener listener) {
    apiTask.getPostReceived(listener);
  }

  @Override public void getPostSent(GetListPostListener listener) {
    apiTask.getPostSent(listener);
  }

  @Override public void getAllPost(GetListPostListener listener) {
    apiTask.getAllPost(listener);
  }

  @Override public void createCacheImage(String photoPath, CommonListener listener) {
    localTask.createCacheImage(photoPath, listener);
  }

  @Override public void clearCacheFolder() {
    localTask.clearCacheFolder();
  }

  @Override public String getCacheFolderSize() {
    return localTask.getCacheFolderSize();
  }

  @Override public void clearPhotoFolder() {
    localTask.clearPhotoFolder();
  }

  @Override public String getPhotoFolderSize() {
    return localTask.getPhotoFolderSize();
  }
}
