package nb.scode.digisign.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x500.X500NameBuilder;
import org.spongycastle.asn1.x500.style.BCStyle;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.AuthorityKeyIdentifier;
import org.spongycastle.asn1.x509.Extension;
import org.spongycastle.asn1.x509.SubjectKeyIdentifier;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cert.X509ExtensionUtils;
import org.spongycastle.cert.X509v3CertificateBuilder;
import org.spongycastle.cert.jcajce.JcaX509CertificateHolder;
import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.X509KeyUsage;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.jce.spec.ECParameterSpec;
import org.spongycastle.operator.ContentSigner;
import org.spongycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.spongycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.spongycastle.operator.DigestCalculator;
import org.spongycastle.operator.bc.BcDigestCalculatorProvider;
import org.spongycastle.operator.jcajce.JcaContentSignerBuilder;
import timber.log.Timber;

/**
 * The type Preferences helper.
 */
@Singleton public class LocalHelper implements LocalTask {

  private static final String PREF_FILE_NAME = "android_pref_file";
  private static final String PREF_FIRST_USE = "first_use";
  private static final String CERT_PATH_USER = "certuser.cer";
  private static final String CERT_PATH_ROOT = "certroot.cer";
  private static final String PRIVATE_KEY_CERT_ROOT = "privkeycert.ppk";
  private static final String KEY_CERT_NAME = "MyDigiSignCert";

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
    Security.removeProvider("BC");
    Security.addProvider(new BouncyCastleProvider());
    //createRootCert();
  }

  @Override public void clear() {
    mPref.edit().clear().apply();
  }

  private KeyPair generateKeyPair() throws Exception {
    ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp521r1");
    SecureRandom random = new SecureRandom();
    KeyPairGenerator g = KeyPairGenerator.getInstance("ECDSA", "SC");
    g.initialize(ecSpec, random);
    return g.generateKeyPair();
  }

  private KeyPair generateRSAKeyPair() throws Exception {
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    keyPairGenerator.initialize(1024);
    return keyPairGenerator.generateKeyPair();
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

  @Override public X509Certificate getRootCertificate() {
    File file = new File(context.getFilesDir(), CERT_PATH_ROOT);
    X509Certificate cert = null;

    if (file.exists()) {
      byte[] b = getBytesFromFile(file);

      CertificateFactory certificateFactory;
      InputStream in = new ByteArrayInputStream(b);
      try {
        certificateFactory = CertificateFactory.getInstance("X.509");
        cert = (X509Certificate) certificateFactory.generateCertificate(in);
        Timber.d("getCertificate(): issuer => " + cert.toString());
        in.close();
      } catch (CertificateException | IOException e) {
        e.printStackTrace();
      }
    }
    return cert;
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

  private void createKeyValuePair(X509Certificate root, X509Certificate userCert) throws Exception {
    // Get Android KeyStore
    KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
    keyStore.load(null, null);
    KeyPair pair = generateRSAKeyPair();
    PublicKey publicKey = pair.getPublic();
    Timber.d("My public key => " + publicKey.toString());
    PrivateKey privateKey = pair.getPrivate();
    Timber.d("My private key => " + privateKey.toString());
    keyStore.setKeyEntry(KEY_CERT_NAME, privateKey, null, new Certificate[] { userCert, root });
  }

  @Override public boolean isKeyStoreExist() {
    try {
      KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
      keyStore.load(null, null);
      Enumeration<String> enumeration = keyStore.aliases();
      while (enumeration.hasMoreElements()) {
        if (enumeration.nextElement().equals(KEY_CERT_NAME)) {
          return true;
        }
      }
    } catch (Exception e) {
      Timber.e("isKeyStoreExist(): " + e.getMessage());
    }
    return false;
  }

  /*
  @Override public void createRootCert() {
    Date startDate = Calendar.getInstance().getTime(); // time from which certificate is valid
    Calendar endDate = Calendar.getInstance();
    endDate.add(Calendar.YEAR, 5);
    Date expiryDate = endDate.getTime();             // time after which certificate is not valid
    SecureRandom random = new SecureRandom();
    KeyPair keyPair = null;             // EC public/private key pair
    try {
      keyPair = generateKeyPair();
    } catch (Exception e) {
      e.printStackTrace();
    }

    X500NameBuilder nameBuilder = new X500NameBuilder(BCStyle.INSTANCE);
    nameBuilder.addRDN(BCStyle.O, "SCODE Studio");
    nameBuilder.addRDN(BCStyle.OU, "root");
    nameBuilder.addRDN(BCStyle.L, "ID");

    X500Name x500Name = nameBuilder.build();
    SubjectPublicKeyInfo subjectPublicKeyInfo =
        SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded());
    X509v1CertificateBuilder v1CertGen =
        new X509v1CertificateBuilder(x500Name, BigInteger.valueOf(random.nextLong()), startDate,
            expiryDate, x500Name, subjectPublicKeyInfo);

    // Prepare Signature:
    ContentSigner sigGen = null;
    try {
      Security.addProvider(new BouncyCastleProvider());
      sigGen = new JcaContentSignerBuilder("SHA512WITHECDSA").setProvider("SC")
          .build(keyPair.getPrivate());
    } catch (OperatorCreationException e) {
      e.printStackTrace();
    }
    // Self sign :
    X509CertificateHolder x509CertificateHolder = v1CertGen.build(sigGen);
    try {
      byte[] bytes = x509CertificateHolder.getEncoded();
      byte[] privkey = keyPair.getPrivate().getEncoded();
      saveFile(privkey, PRIVATE_KEY_CERT_ROOT);
      saveFile(bytes, CERT_PATH_ROOT);
      Timber.d("createRootCertificate(): finished");
    } catch (IOException | CertificateEncodingException e) {
      e.printStackTrace();
    }
  }
  */

  @Override public void createUserCertificate(CommonListener listener) throws Exception {
    /**
     * Self Sign Certificate
     */
    final int VALIDITY_IN_YEARS = 5;
    final X509Certificate rootCertificate = getRootCertificate();
    X500Name issuer = new JcaX509CertificateHolder(rootCertificate).getSubject();

    Calendar calendar = Calendar.getInstance();
    Date startDate = calendar.getTime();
    calendar.add(Calendar.YEAR, VALIDITY_IN_YEARS);
    Date endDate = calendar.getTime();
    final String oTAG = "SCODE";
    final String ouTAG = "Digi Sign";
    final String lTAG = "ID";
    final String username = "User";
    X500NameBuilder nameBuilder = new X500NameBuilder(BCStyle.INSTANCE);
    nameBuilder.addRDN(BCStyle.O, oTAG);
    nameBuilder.addRDN(BCStyle.OU, ouTAG);
    nameBuilder.addRDN(BCStyle.L, lTAG);
    nameBuilder.addRDN(BCStyle.NAME, username);
    X500Name subject = nameBuilder.build();

    // Generate Key Pair
    KeyPair pair = generateRSAKeyPair();
    SecureRandom random = new SecureRandom();
    PublicKey publicKey = pair.getPublic();
    Timber.d("Certificate public key => " + publicKey.toString());
    PrivateKey privateKey = pair.getPrivate();
    Timber.d("Certificate private key => " + privateKey.toString());
    SubjectPublicKeyInfo subjectPublicKeyInfo =
        SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
    X509v3CertificateBuilder v3CertGen =
        new X509v3CertificateBuilder(issuer, BigInteger.valueOf(random.nextLong()), startDate,
            endDate, subject, subjectPublicKeyInfo);

    // Add Extension to Certificate
    String sigAlgName = rootCertificate.getSigAlgName();
    Timber.d("createUserCertificate(): sigALgId " + sigAlgName);
    AlgorithmIdentifier algorithmIdentifier =
        new DefaultSignatureAlgorithmIdentifierFinder().find(sigAlgName);
    AlgorithmIdentifier digAlg =
        new DefaultDigestAlgorithmIdentifierFinder().find(algorithmIdentifier);
    DigestCalculator digestCalculator = new BcDigestCalculatorProvider().get(digAlg);
    X509ExtensionUtils utils = new X509ExtensionUtils(digestCalculator);
    AuthorityKeyIdentifier authorityKeyIdentifier =
        utils.createAuthorityKeyIdentifier(new JcaX509CertificateHolder(rootCertificate));
    SubjectPublicKeyInfo subjectPublicKeyInfoRoot =
        SubjectPublicKeyInfo.getInstance(rootCertificate.getPublicKey().getEncoded());
    SubjectKeyIdentifier subjectKeyIdentifier =
        utils.createSubjectKeyIdentifier(subjectPublicKeyInfoRoot);
    // The Key Usage extension:
    X509KeyUsage keyuse = new X509KeyUsage(X509KeyUsage.digitalSignature
        | X509KeyUsage.nonRepudiation
        | X509KeyUsage.keyEncipherment
        | X509KeyUsage.dataEncipherment);
    Extension keyUsageExt = new Extension(Extension.keyUsage, true, keyuse.getEncoded());
    Extension authorityExt =
        new Extension(Extension.authorityKeyIdentifier, true, authorityKeyIdentifier.getEncoded());
    Extension subjectKeyExt =
        new Extension(Extension.subjectKeyIdentifier, true, subjectKeyIdentifier.getEncoded());
    v3CertGen.addExtension(keyUsageExt);
    v3CertGen.addExtension(authorityExt);
    v3CertGen.addExtension(subjectKeyExt);

    // Prepare Signature:
    ContentSigner sigGen =
        new JcaContentSignerBuilder("SHA512WithRSA").setProvider("SC").build(privateKey);

    // Self sign :
    X509CertificateHolder x509CertificateHolder = v3CertGen.build(sigGen);
    CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
    InputStream in = new ByteArrayInputStream(x509CertificateHolder.getEncoded());
    // Convert Certificate Holder to X509Certificate
    X509Certificate cert = (X509Certificate) certFactory.generateCertificate(in);
    saveFile(cert.getEncoded(), CERT_PATH_USER);
    createKeyValuePair(rootCertificate, cert);

    Timber.d("createUserCertificate(): finished");
  }

  private byte[] getBytesFromUri(Uri uri) throws IOException {
    InputStream inputStream = context.getContentResolver().openInputStream(uri);
    ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

    // this is storage overwritten on each iteration with bytes
    int bufferSize = 5 * 1024;
    byte[] buffer = new byte[bufferSize];

    // we need to know how may bytes were read to write them to the byteBuffer
    int len = 0;
    while ((len = inputStream.read(buffer)) != -1) {
      byteBuffer.write(buffer, 0, len);
    }
    inputStream.close();
    // and then we can return your byte array.
    return byteBuffer.toByteArray();
  }

  @Override public void createSignature(Uri uri, CommonListener listener) {
    try {
      listener.onProcess();
      byte[] source = getBytesFromUri(uri);
      KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
      keyStore.load(null);
      KeyStore.Entry entry = keyStore.getEntry(KEY_CERT_NAME, null);
      PrivateKey privateKey = ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
      PublicKey publicKey = keyStore.getCertificate(KEY_CERT_NAME).getPublicKey();

      Signature signature = Signature.getInstance("SHA512withECDSA", "SC");
      signature.initSign(privateKey);

      byte[] result;
      signature.update(source);
      result = signature.sign();
      Timber.d("createSignature(): => Signature : " + new BigInteger(1, result).toString(16));
      listener.onFinished();
    } catch (Exception e) {
      Timber.e("createSignature(): " + e.toString());
      listener.onError(e.getMessage());
    }
  }

  @Override public File getCertRootDest() {
    return new File(context.getFilesDir(), CERT_PATH_ROOT);
  }

  private PrivateKey getRootPrivateKey() {
    try {
      File file = new File(context.getFilesDir(), PRIVATE_KEY_CERT_ROOT);
      byte[] privateKeyBytes = getBytesFromFile(file);
      KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "SC");
      return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
    } catch (Exception e) {
      Timber.e("getRootPrivateKey(): " + e.getMessage());
    }
    return null;
  }
}
