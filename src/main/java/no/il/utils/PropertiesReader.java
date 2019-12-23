package no.il.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Properties;

public class PropertiesReader {

    private String aud;
    private String resource;
    private String baseUrl;
    private String accessTokenUser, accessTokenScope, scopeUserRead, certificateUserRead, clientUserRead;
    private String tokenEndpoint, scopesEndpoint, clientsEndpoint, certificateEndpoint;

    private boolean prettyPrintJWT;
    private boolean saveResult;
    private X509Certificate certificate;
    private PrivateKey privateKey;

    public boolean prettyPrintJWT() {
        return prettyPrintJWT;
    }

    public void setPrettyPrintJWT(String prettyPrintJWTResult) {
        if (prettyPrintJWTResult != null && prettyPrintJWTResult.equalsIgnoreCase("true")) {
            prettyPrintJWT = true;
        }
    }

    public boolean saveResult() {
        return saveResult;
    }

    public void setSaveResult(String saveTheResult) {
        if (saveTheResult != null && saveTheResult.equalsIgnoreCase("true")) {
            saveResult = true;
        }
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getAccessTokenUser() {
        return accessTokenUser;
    }

    public void setAccessTokenUser(String accessTokenUser) {
        this.accessTokenUser = accessTokenUser;
    }

    public String getAccessTokenScope() {
        return accessTokenScope;
    }

    public void setAccessTokenScope(String accessTokenScope) {
        this.accessTokenScope = accessTokenScope;
    }

    public String getScopeUserRead() {
        return scopeUserRead;
    }

    public void setScopeUserRead(String scopeUserRead) {
        this.scopeUserRead = scopeUserRead;
    }

    public String getCertificateUserRead() {
        return certificateUserRead;
    }

    public void setCertificateUserRead(String certificateUserRead) {
        this.certificateUserRead = certificateUserRead;
    }

    public String getClientUserRead() {
        return clientUserRead;
    }

    public void setClientUserRead(String clientUserRead) {
        this.clientUserRead = clientUserRead;
    }

    public String getAud() {
        return aud;
    }

    public void setAud(String aud) {
        this.aud = aud;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getTokenEndpoint() {
        return tokenEndpoint;
    }

    public void setTokenEndpoint(String tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }

    public String getScopesEndpoint() {
        return scopesEndpoint;
    }

    public void setScopesEndpoint(String scopesEndpoint) {
        this.scopesEndpoint = scopesEndpoint;
    }

    public String getClientsEndpoint() {
        return clientsEndpoint;
    }

    public void setClientsEndpoint(String clientsEndpoint) {
        this.clientsEndpoint = clientsEndpoint;
    }

    public String getCertificateEndpoint() {
        return certificateEndpoint;
    }

    public void setCertificateEndpoint(String certificateEndpoint) {
        this.certificateEndpoint = certificateEndpoint;
    }

    public X509Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(X509Certificate certificate) {
        this.certificate = certificate;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }


    public static PropertiesReader load(String path) throws Exception {
        PropertiesReader config = new PropertiesReader();

        Properties props = readPropertyFile(path);

        config.setPrettyPrintJWT(props.getProperty("prettyPrintJWT"));
        config.setSaveResult(props.getProperty("saveResult"));

        config.setAud(props.getProperty("audience"));
        config.setResource(props.getProperty("resource"));

        config.setTokenEndpoint(props.getProperty("token.endpoint"));
        config.setAccessTokenUser(props.getProperty("accesstoken.user"));
        config.setAccessTokenScope(props.getProperty("accesstoken.scope"));

        config.setScopesEndpoint(props.getProperty("scopes.endpoint"));
        config.setScopeUserRead(props.getProperty("scope.read.user"));

        config.setCertificateEndpoint(props.getProperty("certificate.endpoint"));
        config.setCertificateUserRead(props.getProperty("certificate.read.user"));

        config.setClientsEndpoint(props.getProperty("clients.endpoint"));
        config.setClientUserRead(props.getProperty("client.read.user"));

        String keystoreFile = props.getProperty("keystore.file");
        String keystorePassword = props.getProperty("keystore.password");
        String keystoreAlias = props.getProperty("keystore.alias");
        String keystoreAliasPassword = props.getProperty("keystore.alias.password");

        loadCertificateAndKeyFromFile(config, keystoreFile, keystorePassword, keystoreAlias, keystoreAliasPassword);

        return config;
    }

    private static void loadCertificateAndKeyFromFile(PropertiesReader config, String keyStoreFile, String keyStorePassword, String alias, String keyPassword) throws Exception {
        InputStream is = new FileInputStream(keyStoreFile);
        loadCertificate(config, is, keyStorePassword, alias, keyPassword);

    }

    private static void loadCertificate(PropertiesReader config, InputStream is, String keystorePassword, String alias, String keyPassword) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(is, keystorePassword.toCharArray());

        PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, keyPassword.toCharArray()); // Read from KeyStore
        X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);

        config.setCertificate(certificate);
        config.setPrivateKey(privateKey);
    }

    private static Properties readPropertyFile(String filename) throws Exception {
        Properties props = new Properties();

        InputStream inputStream = new FileInputStream(filename);
        if (inputStream != null) {
            props.load(inputStream);
        } else {
            throw new FileNotFoundException("property file '" + filename + "' not found in the classpath");
        }

        return props;
    }
}
