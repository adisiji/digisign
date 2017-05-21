package nb.scode.digisign.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import io.reactivex.Completable;
import io.reactivex.functions.Action;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

/**
 * The type Preferences helper.
 */
@Singleton public class LocalHelper implements LocalTask {

  private static final String PREF_FILE_NAME = "digi_sign_pref";
  private static final String PREF_FIRST_USE = "first_use";
  private static final String PRIVATE_KEY = "privkey.ppk";
  private static final String PUBLIC_KEY = "pubkey.pbk";

  private final SharedPreferences mPref;
  private final Context context;

  /*
  private KeyPair generateKeyPair() throws Exception {
    ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp521r1");
    SecureRandom random = new SecureRandom();
    KeyPairGenerator g = KeyPairGenerator.getInstance("ECDSA", "SC");
    g.initialize(ecSpec, random);
    return g.generateKeyPair();
  }

  private KeyPair generateRSAKeyPair() throws Exception {
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "SC");
    keyPairGenerator.initialize(2048, new SecureRandom());
    return keyPairGenerator.generateKeyPair();
  }
  */

  /**
   * Instantiates a new Preferences helper.
   *
   * @param context the context
   */
  @Inject public LocalHelper(Context context) {
    mPref = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    this.context = context;
    //testKunci();
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

  @Override public boolean isLocalKeyPairAvailable() {
    File file = new File(context.getFilesDir(), PRIVATE_KEY);
    File file1 = new File(context.getFilesDir(), PUBLIC_KEY);
    if (file.exists() && file1.exists()) {
      testKunci();
      return true;
    } else {
      return false;
    }
  }

  @Override public boolean isEmailSame(String email) {
    String res = mPref.getString("EMAIL", "A");
    return res.equals(email);
  }

  @Override public void setEmailPref(String email) {
    mPref.edit().putString("EMAIL", email).apply();
  }

  @Override public void createKey(final CommonListener listener) throws Exception {
    listener.onProcess();
    Completable.fromAction(new Action() {
      @Override public void run() throws Exception {
        // Setup for P-384 curve params
        BigInteger P_384_Q = new BigInteger("FFFFFFFF"
            + "FFFFFFFF"
            + "FFFFFFFF"
            + "FFFFFFFF"
            + "FFFFFFFF"
            + "FFFFFFFF"
            + "FFFFFFFF"
            + "FFFFFFFE"
            + "FFFFFFFF"
            + "00000000"
            + "00000000"
            + "FFFFFFFF", 16);
        BigInteger P_384_A = new BigInteger("FFFFFFFF"
            + "FFFFFFFF"
            + "FFFFFFFF"
            + "FFFFFFFF"
            + "FFFFFFFF"
            + "FFFFFFFF"
            + "FFFFFFFF"
            + "FFFFFFFE"
            + "FFFFFFFF"
            + "00000000"
            + "00000000"
            + "FFFFFFFC", 16);
        BigInteger P_384_B = new BigInteger("B3312FA7"
            + "E23EE7E4"
            + "988E056B"
            + "E3F82D19"
            + "181D9C6E"
            + "FE814112"
            + "0314088F"
            + "5013875A"
            + "C656398D"
            + "8A2ED19D"
            + "2A85C8ED"
            + "D3EC2AEF", 16);

        byte[] p384_seed = {
            (byte) 0xa3, (byte) 0x35, (byte) 0x92, (byte) 0x6a, (byte) 0xa3, (byte) 0x19,
            (byte) 0xa2, (byte) 0x7a, (byte) 0x1d, (byte) 0x00, (byte) 0x89, (byte) 0x6a,
            (byte) 0x67, (byte) 0x73, (byte) 0xa4, (byte) 0x82, (byte) 0x7a, (byte) 0xcd,
            (byte) 0xac, (byte) 0x73
        };

        // Base Point Gx
        BigInteger P_384_G_X = new BigInteger("AA87CA22"
            + "BE8B0537"
            + "8EB1C71E"
            + "F320AD74"
            + "6E1D3B62"
            + "8BA79B98"
            + "59F741E0"
            + "82542A38"
            + "5502F25D"
            + "BF55296C"
            + "3A545E38"
            + "72760AB7", 16);

        // Base Point Gy
        BigInteger P_384_G_Y = new BigInteger("3617DE4A"
            + "96262C6F"
            + "5D9E98BF"
            + "9292DC29"
            + "F8F41DBD"
            + "289A147C"
            + "E9DA3113"
            + "B5F0B8C0"
            + "0A60B1CE"
            + "1D7E819D"
            + "7A431D7C"
            + "90EA0E5F", 16);

        // The order n of G
        BigInteger P_384_N = new BigInteger("FFFFFFFF"
            + "FFFFFFFF"
            + "FFFFFFFF"
            + "FFFFFFFF"
            + "FFFFFFFF"
            + "FFFFFFFF"
            + "C7634D81"
            + "F4372DDF"
            + "581A0DB2"
            + "48B0A77A"
            + "ECEC196A"
            + "CCC52973", 16);

        // Construct prime field
        ECFieldFp p384_field = new ECFieldFp(P_384_Q);

        // Construct curve from parameters
        EllipticCurve p384 = new EllipticCurve(p384_field, P_384_A, P_384_B, p384_seed);

        // Construct base point for curve
        ECPoint p384_base = new ECPoint(P_384_G_X, P_384_G_Y);

        // Construct curve parameter specifications object
        ECParameterSpec p384spec =
            new ECParameterSpec(p384, p384_base, P_384_N, 1); // Co-factor 1 for prime curves

        // ------------------------------------------------------------- //
        // Generate KeyPair
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
        keyPairGenerator.initialize(p384spec, new SecureRandom());

        KeyPair keyPair = keyPairGenerator.genKeyPair();
        byte[] privKey = keyPair.getPrivate().getEncoded();
        byte[] publicKey = keyPair.getPublic().getEncoded();
        saveFile(privKey, PRIVATE_KEY);
        saveFile(publicKey, PUBLIC_KEY);
      }
    }).subscribe(new Action() {
      @Override public void run() throws Exception {
        listener.onFinished();
      }
    });
  }

  @Override public File getPublicKey() {
    return new File(context.getFilesDir(), PUBLIC_KEY);
  }

  @Override public File getPrivateKey() {
    return new File(context.getFilesDir(), PRIVATE_KEY);
  }

  @Override public void getPrepFilePdf(String uripdf, ListenerPrepPdf listenerPrepPdf) {
    Uri uri = Uri.parse(uripdf);

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
      OutputStream output = new FileOutputStream(file);
      byte[] buffer = new byte[4 * 1024]; // or other buffer size
      int read;
      while ((read = input.read(buffer)) != -1) {
        output.write(buffer, 0, read);
      }
      output.flush();
      output.close();
      input.close();
      listenerPrepPdf.onComplete(file);
    } catch (IOException e) {
      listenerPrepPdf.onError(e.getMessage());
      e.printStackTrace();
    }
  }

  private byte[] getBytesFromFile(File file) {
    byte[] b = new byte[(int) file.length()];
    try {
      FileInputStream fileInputStream = new FileInputStream(file);
      fileInputStream.read(b);
      fileInputStream.close();
    } catch (FileNotFoundException e) {
      System.out.println("File Not Found.");
      e.printStackTrace();
    } catch (IOException e1) {
      System.out.println("Error Reading The File.");
      e1.printStackTrace();
    }
    return b;
  }

  private void saveFile(byte[] bytes, String path) throws IOException {
    FileOutputStream fos = new FileOutputStream(new File(context.getFilesDir(), path));
    fos.write(bytes);
    fos.close();
  }

  private void testKunci() {
    try {
      byte[] privBytes = getBytesFromFile(new File(context.getFilesDir(), PRIVATE_KEY));
      PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(privBytes);
      KeyFactory kf = KeyFactory.getInstance("EC");
      PrivateKey pvt = kf.generatePrivate(ks);

      String test = "Ini message untuk di signBytes";
      byte[] bytesDigest = test.getBytes();

      Signature signature = Signature.getInstance("SHA384withECDSA");
      // Initialize Signature with private key
      signature.initSign(pvt);
      // Update the signature with digest content
      signature.update(bytesDigest);

      // Signing
      byte[] signBytes = signature.sign();

      // Get Public Key
      byte[] pubBytes = getBytesFromFile(new File(context.getFilesDir(), PUBLIC_KEY));
      X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(pubBytes);
      PublicKey publicKey = kf.generatePublic(pubKeySpec);
      // Prepare signature for verify
      signature.initVerify(publicKey);
      signature.update(bytesDigest);
      boolean x = signature.verify(signBytes);
      Timber.d("testKunci(): hasil verifiy => " + x);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
