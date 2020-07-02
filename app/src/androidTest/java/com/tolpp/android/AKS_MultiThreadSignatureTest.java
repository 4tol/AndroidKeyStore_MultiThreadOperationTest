package com.tolpp.android;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.tolpp.android.util.KeyStoreUtil;
import com.tolpp.android.util.RandomUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@RunWith(AndroidJUnit4.class)
public class AKS_MultiThreadSignatureTest {
    private static final String TAG = "MultiThreadSignatureTest";
    private static final int THREAD_COUNT = 8;
    private static final int SIGN_COUNT = 1000;


    @Before
    public void setup() {
    }

    @Test
    public void testMultiThreadSignatureWithDifferentAlias() throws Exception {
        KeyStoreUtil.removeAllKeys();

        List<String> randomAliasList = new ArrayList<>();
        Map<String, ExecutorService> keyExecutors = new HashMap<>();
        Log.d(TAG, "Generating aliases");
        for (int i = 0; i < THREAD_COUNT; i++) {
            String alias = RandomUtil.randomAlphaNumeric(15);
            randomAliasList.add(alias);
            keyExecutors.put(alias, Executors.newSingleThreadExecutor());
        }
        Log.d(TAG, "Generating keys");
        for (String alias : randomAliasList) {
            KeyStoreUtil.create2048RsaKeyForSigning(alias);
            Log.d(TAG, "Key generated with alias: " + alias);
        }
        final CountDownLatch cdl = new CountDownLatch(SIGN_COUNT);

        final byte[] valueToSign = RandomUtil.getRandomBytes(100);
        for (int i = 0; i < SIGN_COUNT; i++) {
            String alias = randomAliasList.get(i % randomAliasList.size());
            //noinspection ConstantConditions
            keyExecutors.get(alias).execute(() -> {
                try {
                    byte[] signedBytes = KeyStoreUtil.signSHA512withRSA(alias, valueToSign);
                    Log.d(TAG, "Signed with alias: " + alias);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                cdl.countDown();
            });
        }
        Log.d(TAG, "Scheduled signing task count: " + SIGN_COUNT);
        Log.d(TAG, "Waiting to complete: " + SIGN_COUNT);
        cdl.await();
    }

    @Test
    public void testMultiThreadSignatureWithSameAlias() throws Exception {
        KeyStoreUtil.removeAllKeys();

        String alias = RandomUtil.randomAlphaNumeric(15);
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        KeyStoreUtil.create2048RsaKeyForSigning(alias);

        final CountDownLatch cdl = new CountDownLatch(SIGN_COUNT);

        final byte[] valueToSign = RandomUtil.getRandomBytes(100);
        for (int i = 0; i < SIGN_COUNT; i++) {
            executorService.execute(() -> {
                try {
                    KeyStoreUtil.signSHA512withRSA(alias, valueToSign);
                    Log.d(TAG, "Signed with alias: " + alias);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                cdl.countDown();
            });
        }
        Log.d(TAG, "Scheduled signing task count: " + SIGN_COUNT);
        Log.d(TAG, "Waiting to complete: " + SIGN_COUNT);
        cdl.await();
    }
}