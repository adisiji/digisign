package nb.scode.digisign.data.remote.FireModel;

/**
 * Created by neobyte on 5/19/2017.
 */

public class Post {

  private String desc;
  private String type;
  private String senderKey;
  private String receiverKey;
  private String receiverName;
  private String filename;
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

  public String getSenderKey() {
    return senderKey;
  }

  public void setSenderKey(String senderKey) {
    this.senderKey = senderKey;
  }

  public String getReceiverKey() {
    return receiverKey;
  }

  public void setReceiverKey(String receiverKey) {
    this.receiverKey = receiverKey;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public String getLinkDownload() {
    return linkDownload;
  }

  public void setLinkDownload(String linkDownload) {
    this.linkDownload = linkDownload;
  }

  public String getReceiverName() {
    return receiverName;
  }

  public void setReceiverName(String receiverName) {
    this.receiverName = receiverName;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }
}
