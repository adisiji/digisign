package nb.scode.digisign.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * The type Preferences helper.
 */
@Singleton public class LocalHelper implements LocalTask {

  private static final String PREF_FILE_NAME = "android_pref_file";
  private static final String PREF_FIRST_USE = "first_use";

  private final SharedPreferences mPref;
  private final Context context;

  /**
   * Instantiates a new Preferences helper.
   *
   * @param context the context
   */
  @Inject public LocalHelper(Context context) {
    mPref = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    this.context = context;
  }

  @Override public void clear() {
    mPref.edit().clear().apply();
  }

  @Override public boolean isFirstUse() {
    return mPref.getBoolean(PREF_FIRST_USE, true);
  }

  @Override public void setNotFirstUse() {
    mPref.edit().putBoolean(PREF_FIRST_USE, false).apply();
  }

  @Override public void getPrepFilePdf(Uri uri, ListenerPrepPdf listenerPrepPdf) {

    // The query, since it only applies to a single document, will only return one row.
    // no need to filter, sort, or select fields, since we want all fields for one
    // document.
    Cursor cursor = context.getContentResolver().query(uri, null, null, null, null, null);

    try {
      // moveToFirst() returns false if the cursor has 0 rows.  Very handy for
      // "if there's anything to look at, look at it" conditionals.
      if (cursor != null && cursor.moveToFirst()) {

        // Note it's called "Display Name".  This is provider-specific, and
        // might not necessarily be the file name.
        String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
        listenerPrepPdf.setFileName(displayName);

        int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
        // If the size is unknown, the value stored is null.  But since an int can't be
        // null in java, the behavior is implementation-specific, which is just a fancy
        // term for "unpredictable".  So as a rule, check if it's null before assigning
        // to an int.  This will happen often:  The storage API allows for remote
        // files, whose size might not be locally known.
        String size = null;
        if (!cursor.isNull(sizeIndex)) {
          // Technically the column stores an int, but cursor.getString will do the
          // conversion automatically.
          size = cursor.getString(sizeIndex);
          int zx = Integer.valueOf(size) / 1024;
          String sizeType = " KB";
          if (zx > 1024) { //file size is more than 1 mb
            zx = zx / 1024;
            sizeType = " MB";
          }
          size = String.valueOf(zx) + sizeType;
        } else {
          size = "Unknown";
        }
        listenerPrepPdf.setFileSize(size);
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    // END_INCLUDE (dump_metadata)

    try {
      InputStream input = context.getContentResolver().openInputStream(uri);
      File file = new File(context.getCacheDir(), "cache.pdf");
      try {
        OutputStream output = new FileOutputStream(file);
        try {
          try {
            byte[] buffer = new byte[4 * 1024]; // or other buffer size
            int read;

            while ((read = input.read(buffer)) != -1) {
              output.write(buffer, 0, read);
            }
            output.flush();
          } finally {
            output.close();
          }
        } catch (NullPointerException e) {
          e.printStackTrace();
        }
      } finally {
        input.close();
      }
      listenerPrepPdf.onComplete(file);
    } catch (IOException e) {
      listenerPrepPdf.onError(e.getMessage());
      e.printStackTrace();
    }
  }
}
