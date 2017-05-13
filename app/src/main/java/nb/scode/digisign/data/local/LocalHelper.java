package nb.scode.digisign.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x500.X500NameBuilder;
import org.spongycastle.asn1.x500.style.BCStyle;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cert.X509v3CertificateBuilder;
import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.jce.spec.ECParameterSpec;
import org.spongycastle.operator.ContentSigner;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.operator.jcajce.JcaContentSignerBuilder;
import timber.log.Timber;

/**
 * The type Preferences helper.
 */
@Singleton public class LocalHelper implements LocalTask {

  private static final String PREF_FILE_NAME = "android_pref_file";
  private static final String PREF_FIRST_USE = "first_use";
  private static final String CERT_PATH = "certxxzz.cer";

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

  @Override public void getCert(final CommonListener listener) {
    listener.onProcess();
    Completable.fromAction(new Action() {
      @Override public void run() throws Exception {

        Security.removeProvider("BC");
        Security.addProvider(new BouncyCastleProvider());

        ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp521r1");
        ECNamedCurveTable.getNames();
        KeyPairGenerator g = KeyPairGenerator.getInstance("ECDSA", "SC");
        g.initialize(ecSpec, new SecureRandom());
        KeyPair pair = g.generateKeyPair();
        PublicKey publicKey = pair.getPublic();
        Timber.d("public => " + publicKey.toString());
        PrivateKey privateKey = pair.getPrivate();
        Timber.d("private => " + privateKey.toString());

        Signature signature = Signature.getInstance("SHA256withECDSA", "SC");
        try {
          signature.initSign(privateKey);
        } catch (InvalidKeyException e) {
          e.printStackTrace();
        }
        String str = "This is string to sign";
        byte[] strByte = new byte[0];
        byte[] result = new byte[0];
        try {
          strByte = str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
        }
        try {
          signature.update(strByte);
          result = signature.sign();
        } catch (SignatureException e) {
          e.printStackTrace();
        }
        System.out.println("Signature: " + new BigInteger(1, result).toString(16));

        try {
          signature.initVerify(publicKey);
        } catch (InvalidKeyException e) {
          e.printStackTrace();
        }

        try {
          signature.update(strByte);
          if (signature.verify(result)) {
            String s = new String(strByte);
            Timber.d("testName(): its correct");
            Timber.d("testName(): origin => " + s);
          } else {
            Timber.e("testName(): false");
          }
        } catch (SignatureException e) {
          e.printStackTrace();
        }
      }
    }).subscribeOn(Schedulers.computation()).subscribe(new CompletableObserver() {
      @Override public void onSubscribe(@NonNull Disposable d) {

      }

      @Override public void onComplete() {
        if (getCertificate() == null) {
          try {
            setCertificate();
          } catch (Exception e) {
            e.printStackTrace();
          }
        } else {
          Timber.d("onComplete(): certificate udah ada tong");
        }
        listener.onFinished();
      }

      @Override public void onError(@NonNull Throwable e) {
        e.printStackTrace();
        listener.onError(e.getMessage());
      }
    });
  }

  private Certificate getCertificate() {
    File file = new File(context.getFilesDir(), CERT_PATH);
    X509Certificate cert = null;

    if (file.exists()) {
      byte[] b = getBytesFromFile(file);

      CertificateFactory certificateFactory;
      InputStream in = new ByteArrayInputStream(b);
      try {
        certificateFactory = CertificateFactory.getInstance("X.509");
        cert = (X509Certificate) certificateFactory.generateCertificate(in);
        Timber.d("getCertificate(): issuer => " + cert.toString());
      } catch (CertificateException e) {
        e.printStackTrace();
      }
    }
    return cert;
  }

  private void setCertificate() throws Exception {
    /**
     * Self Sign Certificate
     */
    ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp521r1");
    final int VALIDITY_IN_DAYS = 1000;
    Date startDate = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
    Date endDate = new Date(System.currentTimeMillis() + VALIDITY_IN_DAYS * 24 * 60 * 60 * 1000);

    final String oTAG = "SCODE";
    final String ouTAG = "Android Sign";
    final String lTAG = "ID";
    X500NameBuilder nameBuilder = new X500NameBuilder(BCStyle.INSTANCE);
    nameBuilder.addRDN(BCStyle.O, oTAG);
    nameBuilder.addRDN(BCStyle.OU, ouTAG);
    nameBuilder.addRDN(BCStyle.L, lTAG);

    X500Name x500Name = nameBuilder.build();
    SecureRandom random = new SecureRandom();

    // Key generator for Cerfiticate
    KeyPairGenerator g = KeyPairGenerator.getInstance("ECDSA", "SC");
    g.initialize(ecSpec, random);
    KeyPair pair = g.generateKeyPair();
    PublicKey publicKey = pair.getPublic();
    PrivateKey privateKey = pair.getPrivate();

    SubjectPublicKeyInfo subjectPublicKeyInfo =
        SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
    X509v3CertificateBuilder v1CertGen =
        new X509v3CertificateBuilder(x500Name, BigInteger.valueOf(random.nextLong()), startDate,
            endDate, x500Name, subjectPublicKeyInfo);

    // Prepare Signature:
    ContentSigner sigGen = null;
    try {
      sigGen = new JcaContentSignerBuilder("SHA256WithECDSA").setProvider("SC").build(privateKey);
    } catch (OperatorCreationException e) {
      e.printStackTrace();
    }
    // Self sign :
    X509CertificateHolder x509CertificateHolder = v1CertGen.build(sigGen);
    CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
    InputStream in = new ByteArrayInputStream(x509CertificateHolder.getEncoded());
    // Convert Certificate Holder to X509Certificate
    X509Certificate cert = (X509Certificate) certFactory.generateCertificate(in);
    saveFile(cert.getEncoded(), CERT_PATH);
    Timber.d("setCertificate(): finished");
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

  private void saveFile(byte[] bytes, String path)
      throws IOException, CertificateEncodingException {
    FileOutputStream fos = new FileOutputStream(new File(context.getFilesDir(), path));
    fos.write(bytes);
    fos.close();
  }


}
