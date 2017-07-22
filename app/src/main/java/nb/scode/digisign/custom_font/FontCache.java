package nb.scode.digisign.custom_font;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import java.util.Hashtable;

/**
 * Created by User on 12/26/2016.
 */

public class FontCache {

  @NonNull private static Hashtable<String, Typeface> fontCache = new Hashtable<String, Typeface>();

  public static Typeface get(String name, @NonNull Context context) {
    Typeface tf = fontCache.get(name);
    if (tf == null) {
      try {
        tf = Typeface.createFromAsset(context.getAssets(), "fonts/" + name);
      } catch (Exception e) {
        return null;
      }
      fontCache.put(name, tf);
    }
    return tf;
  }
}
