package nb.scode.digisign.interactor;

/**
 * Created by neobyte on 5/14/2017.
 */

public interface CommonDListener {

  void onProccess();

  void onSuccess();

  void onFailed(String message);
}
