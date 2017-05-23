package nb.scode.digisign.data.service;

/**
 * Created by neobyte on 5/23/2017.
 */

public class TokenPost {
  private String token;

  public TokenPost(String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
