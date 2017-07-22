package nb.scode.digisign.view.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import nb.scode.digisign.R;

/**
 * Created by neobyte on 4/30/2017.
 */

public class InfoFile extends DialogFragment {

  @Nullable @BindView(R.id.content_file_name) TextView name;
  @Nullable @BindView(R.id.content_file_from) TextView from;
  @Nullable @BindView(R.id.content_file_desc) TextView desc;
  @Nullable @BindView(R.id.content_sign_time) TextView time;
  @Nullable @BindView(R.id.content_file_size) TextView size;
  @Nullable private CallbackInfoFile callbacksendfile;
  private Unbinder unbinder;

  /**
   * Empty Constructor fon default Fragment
   */
  public InfoFile() {

  }

  @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_info_file, container);
    unbinder = ButterKnife.bind(this, view);
    name.setText(callbacksendfile.fileName());
    from.setText(callbacksendfile.fileFrom());
    desc.setText(callbacksendfile.fileDesc());
    time.setText(callbacksendfile.fileTime());
    size.setText(callbacksendfile.fileSize());
    return view;
  }

  @Override public void onAttach(Context context) {
    callbacksendfile = (CallbackInfoFile) context;
    super.onAttach(context);
  }

  @Override public void onResume() {
    super.onResume();
    getDialog().getWindow()
        .setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
  }

  @OnClick(R.id.btn_ok_info) void okInfo() {
    dismiss();
  }

  @Override public void onDetach() {
    super.onDetach();
    callbacksendfile = null;
    unbinder.unbind();
  }

  public interface CallbackInfoFile {
    String fileName();

    String fileDesc();

    String fileTime();

    String fileFrom();

    String fileSize();
  }
}
