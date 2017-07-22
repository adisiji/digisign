package nb.scode.digisign.custom_font;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by User on 12/21/2016.
 */

public class TextViewMagneto extends AppCompatTextView {

  public TextViewMagneto(@NonNull Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    applyFont(context, attrs);
  }

  public TextViewMagneto(@NonNull Context context, AttributeSet attrs) {
    super(context, attrs);
    applyFont(context, attrs);
  }

  public TextViewMagneto(@NonNull Context context) {
    super(context);
    applyFont(context, null);
  }

  private void applyFont(@NonNull Context context, AttributeSet attrs) {
    setTypeface(FontCache.get("magneto_bold.ttf", context));
  }
}
