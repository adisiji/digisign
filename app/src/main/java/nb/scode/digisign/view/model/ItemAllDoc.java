package nb.scode.digisign.view.model;

/**
 * Created by neobyte on 5/25/2017.
 */

public class ItemAllDoc {

  private String filename;
  private String filedate;
  private String fromname;
  private String filekey;
  private boolean isSent = false;

  public ItemAllDoc() {

  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getFiledate() {
    return filedate;
  }

  public void setFiledate(String filedate) {
    this.filedate = filedate;
  }

  public String getFromname() {
    return fromname;
  }

  public void setFromname(String fromname) {
    this.fromname = fromname;
  }

  public String getFilekey() {
    return filekey;
  }

  public void setFilekey(String filekey) {
    this.filekey = filekey;
  }

  public boolean isSent() {
    return isSent;
  }

  public void setSent(boolean sent) {
    isSent = sent;
  }
}
