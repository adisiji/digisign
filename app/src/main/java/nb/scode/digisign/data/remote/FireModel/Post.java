package nb.scode.digisign.data.remote.FireModel;

/**
 * Created by neobyte on 5/19/2017.
 */

public class Post {

  private String desc;
  private String type;
  private String to;
  private String from;
  private String receiverName;
  private String receiverEmail;
  private long timestamp;
  private String linkDownload;

  public Post() {
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public String getTo() {
    return to;
  }

  public void setTo(String to) {
    this.to = to;
  }

  public String getReceiverName() {
    return receiverName;
  }

  public void setReceiverName(String receiverName) {
    this.receiverName = receiverName;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public String getReceiverEmail() {
    return receiverEmail;
  }

  public void setReceiverEmail(String receiverEmail) {
    this.receiverEmail = receiverEmail;
  }

  public String getLinkDownload() {
    return linkDownload;
  }

  public void setLinkDownload(String linkDownload) {
    this.linkDownload = linkDownload;
  }
}
