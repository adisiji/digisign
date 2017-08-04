package nb.scode.digisign.interactor;

public interface SettingsInteractor extends BaseInteractor {

  String getCacheFolderSize();

  void clearCache();

  String getPhotoFolderSize();

  void clearCachePhoto();

}