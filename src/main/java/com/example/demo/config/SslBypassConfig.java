package com.example.demo.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;

// This configuration class disables SSL verification for all HTTPS connections only in local.

@Configuration
public class SslBypassConfig {

    @PostConstruct
    public void disableSslVerification() {
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] xcs, String authType) {}
                public void checkServerTrusted(X509Certificate[] xcs, String authType) {}
                public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
            }}, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
