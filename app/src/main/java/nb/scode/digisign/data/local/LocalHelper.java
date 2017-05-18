package nb.scode.digisign.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
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

  private static final String PREF_FILE_NAME = "android_pref_file";
  private static final String PREF_FIRST_USE = "first_use";
  private static final String PRIVATE_KEY = "privkey.ppk";
  private static final String PUBLIC_KEY = "pubkey.pbk";
  /*
  private static final String CERT_PATH_USER = "certuser.cer";
  private static final String CERT_PATH_ROOT = "certroot.cer";
  private static final String PRIVATE_KEY_CERT_ROOT = "privkeycert.ppk";
  private static final String KEY_CERT_NAME = "MyDigiSignCert";
  */

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
    //testKunci();
  }

  @Override public void clear() {
    mPref.edit().clear().apply();
  }

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

  @Override public boolean isFirstUse() {
    return mPref.getBoolean(PREF_FIRST_USE, true);
  }

  @Override public void setNotFirstUse() {
    mPref.edit().putBoolean(PREF_FIRST_USE, false).apply();
  }

  @Override public boolean isKeyPairAvailable() {
    File file = new File(context.getFilesDir(), PRIVATE_KEY);
    if (file.exists()) {
      testKunci();
    }
    return file.exists();
  }

  @Override public void createKey(CommonListener listener) throws Exception {
    listener.onProcess();

    // Setup for P-384 curve params
    BigInteger p384_p = new BigInteger("ffffffff"
        + "ffffffff"
        + "ffffffff"
        + "ffffffff"
        + "ffffffff"
        + "ffffffff"
        + "fffffffe"
        + "ffffffff"
        + "ffffffff"
        + "00000000"
        + "00000000"
        + "ffffffff", 16);

    BigInteger p384_a = new BigInteger("ffffffff"
        + "ffffffff"
        + "ffffffff"
        + "ffffffff"
        + "ffffffff"
        + "ffffffff"
        + "fffffffe"
        + "ffffffff"
        + "ffffffff"
        + "00000000"
        + "00000000"
        + "fffffffc", 16);
    BigInteger p384_b = new BigInteger("b3312fa7"
        + "e23ee734"
        + "988e056b"
        + "e3f82d19"
        + "181d9c6e"
        + "fe814112"
        + "0314088f"
        + "5013875a"
        + "c656398d"
        + "8a2ed19d"
        + "2a85c8ed"
        + "d3ec2aef", 16);
    byte[] p384_seed = {
        (byte) 0xa3, (byte) 0x35, (byte) 0x92, (byte) 0x6a, (byte) 0xa3, (byte) 0x19, (byte) 0xa2,
        (byte) 0x7a, (byte) 0x1d, (byte) 0x00, (byte) 0x89, (byte) 0x6a, (byte) 0x67, (byte) 0x73,
        (byte) 0xa4, (byte) 0x82, (byte) 0x7a, (byte) 0xcd, (byte) 0xac, (byte) 0x73
    };

    // Base Point xG
    BigInteger p384_xg = new BigInteger("aa87ca22"
        + "be8b0537"
        + "8eb1c71e"
        + "f320ad74"
        + "6e1d3b62"
        + "8ba79b98"
        + "59f741e0"
        + "82542a38"
        + "5502f25d"
        + "bf55296c"
        + "3a545e38"
        + "72760ab7", 16);
    // Base Point yG
    BigInteger p384_yg = new BigInteger("3617de4a"
        + "96262c6f"
        + "5d9e98bf"
        + "9292dc29"
        + "f8f41dbd"
        + "289a147c"
        + "e9da3113"
        + "b5f0b8c0"
        + "0a60b1ce"
        + "1d7e819d"
        + "7a431d7c"
        + "90ea0e5f", 16);
    // The order n of G
    BigInteger p384_n = new BigInteger("ffffffff"
        + "ffffffff"
        + "ffffffff"
        + "ffffffff"
        + "ffffffff"
        + "ffffffff"
        + "c7634d81"
        + "f4372ddf"
        + "581a0db2"
        + "48b0a77a"
        + "ecec196a"
        + "ccc52973", 16);

    // Construct prime field
    ECFieldFp p384_field = new ECFieldFp(p384_p);

    // Construct curve from parameters
    EllipticCurve p384 = new EllipticCurve(p384_field, p384_a, p384_b, p384_seed);

    // Construct base point for curve
    ECPoint p384_base = new ECPoint(p384_xg, p384_yg);

    // Construct curve parameter specifications object
    ECParameterSpec p384spec =
        new ECParameterSpec(p384, p384_base, p384_n, 1); // Co-factor 1 for prime curves

    // ------------------------------------------------------------- //

    // Generate KeyPair
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
    keyPairGenerator.initialize(p384spec, new SecureRandom());

    KeyPair keyPair = keyPairGenerator.genKeyPair();
    PrivateKey privateKey = keyPair.getPrivate();
    byte[] privKey = privateKey.getEncoded();
    /*
    byte[] publicKey = keyPair.getPublic().getEncoded();
    saveFile(privKey, PRIVATE_KEY);
    saveFile(publicKey, PUBLIC_KEY);
    */
    listener.onFinished();
  }

  @Override public File getPublicKey() {
    return null;
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
