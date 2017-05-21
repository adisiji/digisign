package nb.scode.digisign.data.remote.FireModel;

/**
 * Created by neobyte on 5/19/2017.
 */

public class ListUid {

  private String email;
  private String uid;

  public ListUid() {
  }

  public ListUid(String email, String uid) {
    this.email = email;
    this.uid = uid;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }
}
