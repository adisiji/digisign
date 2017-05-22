package nb.scode.digisign.data.remote.FireModel;

/**
 * Created by neobyte on 5/22/2017.
 */

public class KeyUser {
  private String key;
  private User user;

  public KeyUser(String key, User user) {
    this.key = key;
    this.user = user;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
