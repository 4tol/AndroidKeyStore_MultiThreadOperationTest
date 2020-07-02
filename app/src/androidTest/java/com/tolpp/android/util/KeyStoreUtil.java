package com.tolpp.android.util;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Enumeration;

public class KeyStoreUtil {

    public static void removeAllKeys() throws Exception {
        KeyStore aks = KeyStore.getInstance("AndroidKeyStore");
        aks.load(null);

        Enumeration<String> aliases = aks.aliases();
        while (aliases.hasMoreElements()) {
            aks.deleteEntry(aliases.nextElement());
        }
    }

    public static void create2048RsaKeyForSigning(String alias) throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA,
                "AndroidKeyStore");
        KeyGenParameterSpec.Builder specBuilder = new KeyGenParameterSpec.Builder(
                alias,
                KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
                .setDigests(KeyProperties.DIGEST_SHA512)
                .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                .setKeySize(2048)
                .setUserAuthenticationRequired(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            specBuilder.setAttestationChallenge("12345".getBytes());
        }

        kpg.initialize(specBuilder.build());
        KeyPair kp = kpg.generateKeyPair();
    }

    public static byte[] signSHA512withRSA(String keyAlias, byte[] valueToSign) throws Exception {
        KeyStore aks = KeyStore.getInstance("AndroidKeyStore");
        aks.load(null);
        String algorithm = "SHA512withRSA";

        PrivateKey privateKey = ((KeyStore.PrivateKeyEntry) aks.getEntry(keyAlias, null)).getPrivateKey();

        Signature s = Signature.getInstance(algorithm);
        s.initSign(privateKey);
        s.update(valueToSign);
        return s.sign();
    }
}
