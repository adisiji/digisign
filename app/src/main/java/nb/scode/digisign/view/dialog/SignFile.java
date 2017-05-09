package nb.scode.digisign.view.dialog;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import nb.scode.digisign.R;
import timber.log.Timber;

/**
 * Created by neobyte on 4/30/2017.
 */

public class SignFile extends DialogFragment {

  private CallbackSendFile callbacksendfile;
  private Unbinder unbinder;

  /**
   * Empty Constructor fon default Fragment
   */
  public SignFile() {

  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    try {
      callbacksendfile = (CallbackSendFile) getTargetFragment();
      Timber.i("onCreate(): callback created");
    } catch (ClassCastException e) {
      throw new ClassCastException("Calling fragment must implement Callback interface");
    }
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater,
      @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_sign_file, container);
    unbinder = ButterKnife.bind(this, view);
    //TODO: inject glide image here
    return view;
  }

  @Override public void onResume() {
    super.onResume();
    getDialog().getWindow()
        .setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
  }

  @OnClick(R.id.btn_select_file) void selectFile() {
    callbacksendfile.selectFile();
    dismiss();
  }

  @OnClick(R.id.btn_take_photo) void takePhoto() {
    callbacksendfile.takePhoto();
    dismiss();
  }

  @Override public void onDetach() {
    super.onDetach();
    callbacksendfile = null;
    unbinder.unbind();
  }

  public interface CallbackSendFile {
    void selectFile();

    void takePhoto();
  }
}
