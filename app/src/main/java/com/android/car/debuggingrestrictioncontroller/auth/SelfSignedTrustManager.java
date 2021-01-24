package com.android.car.debuggingrestrictioncontroller.auth;

import android.util.Log;
import com.android.car.debuggingrestrictioncontroller.BuildConfig;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.UUID;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public final class SelfSignedTrustManager implements X509TrustManager {

  private static final String TAG = SelfSignedTrustManager.class.getSimpleName();
  private static final String ROOT_CA = BuildConfig.ROOT_CA_CERT;
  private static SelfSignedTrustManager instance;
  private X509TrustManager trustManager;

  private SelfSignedTrustManager() throws GeneralSecurityException {
    KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
    try {
      keyStore.load(null, null);
    } catch (IOException e) {
      Log.e(TAG, "Creating an empty KeyStore and this error should not happen");
      throw new GeneralSecurityException(e);
    }

    X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X.509")
        .generateCertificate(new ByteArrayInputStream(ROOT_CA.getBytes()));
    keyStore.setCertificateEntry(UUID.randomUUID().toString(), cert);

    TrustManagerFactory tmf = TrustManagerFactory
        .getInstance(TrustManagerFactory.getDefaultAlgorithm());
    tmf.init(keyStore);
    this.trustManager = (X509TrustManager) tmf.getTrustManagers()[0];
  }

  synchronized public static SelfSignedTrustManager getInstance()
      throws GeneralSecurityException {
    if (instance == null) {
      instance = new SelfSignedTrustManager();
    }
    return instance;
  }

  public void checkClientTrusted(X509Certificate[] chain, String authType)
      throws CertificateException {
    trustManager.checkClientTrusted(chain, authType);
  }

  public void checkServerTrusted(X509Certificate[] chain, String authType)
      throws CertificateException {
    trustManager.checkServerTrusted(chain, authType);
  }

  public X509Certificate[] getAcceptedIssuers() {
    return trustManager.getAcceptedIssuers();
  }
}
