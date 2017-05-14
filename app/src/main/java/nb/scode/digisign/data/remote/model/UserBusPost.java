package nb.scode.digisign.data.remote.model;

import android.net.Uri;

/**
 * Created by neobyte on 5/14/2017.
 */

public class UserBusPost {

  private Uri uri;
  private String email;
  private String displayName;

  public Uri getUri() {
    return uri;
  }

  public void setUri(Uri uri) {
    this.uri = uri;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }
}
