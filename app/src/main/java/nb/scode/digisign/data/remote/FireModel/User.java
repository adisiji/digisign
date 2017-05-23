package nb.scode.digisign.data.remote.FireModel;

/**
 * Created by neobyte on 5/19/2017.
 */

public class User {

  private String name;
  private String email;
  private String token;

  public User() {
  }

  public User(String name, String email, String token) {
    this.name = name;
    this.email = email;
    this.token = token;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
