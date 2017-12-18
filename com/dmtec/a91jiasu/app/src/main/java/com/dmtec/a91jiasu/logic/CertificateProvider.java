package com.dmtec.a91jiasu.logic;

import android.content.Context;
import android.util.Log;

import com.dmtec.a91jiasu.common.Constants;
import com.dmtec.a91jiasu.common.CustomSharePreference;
import com.dmtec.a91jiasu.security.TrustedCertificateEntry;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by dmtec on 2017/9/4.
 * prepare certificate
 */

public class CertificateProvider {

    public static  void prepareCertificate(Context context,InputStream in,CertificateResultCallback callback)
    {

        int cn = CustomSharePreference.getInt(context, Constants.Flags.SP_CERTIFICATE_NUM);
        if(cn == -1){
            try
            {
                X509Certificate certificate;
                CertificateFactory factory = CertificateFactory.getInstance("X.509");
                certificate = (X509Certificate)factory.generateCertificate(in);
                KeyStore store = KeyStore.getInstance("LocalCertificateStore");
                store.load(null, null);
                store.setCertificateEntry(null, certificate);
                TrustedCertificateManager.getInstance().reset();
                TrustedCertificateManager.getInstance().load();
                CustomSharePreference.putInteger(context,Constants.Flags.SP_CERTIFICATE_NUM,cn+1);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        TrustedCertificateManager certman = TrustedCertificateManager.getInstance().load();
        Hashtable<String, X509Certificate> certificates = certman.getCACertificates(TrustedCertificateManager.TrustedCertificateSource.LOCAL);
        List<TrustedCertificateEntry> selected= new ArrayList<TrustedCertificateEntry>();
        for (Map.Entry<String, X509Certificate> entry : certificates.entrySet())
        {
            selected.add(new TrustedCertificateEntry(entry.getKey(), entry.getValue()));
        }
        Collections.sort(selected);
        String alias = null;
        if(selected.size() > 0){
            alias = selected.get(selected.size()-1).getAlias();
        }
        callback.onCertificateReady(alias);
    }

    public interface CertificateResultCallback{
        void onCertificateReady(String alias);
    }
}
