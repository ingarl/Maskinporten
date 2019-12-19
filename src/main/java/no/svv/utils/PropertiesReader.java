package no.svv.utils;

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
    private String tokenEndpoint, scopesEndpoint, clientsEndpoint, keysEndpoints;

    private X509Certificate certificate;
    private PrivateKey privateKey;


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

    public String getKeysEndpoints() {
        return keysEndpoints;
    }

    public void setKeysEndpoints(String keysEndpoints) {
        this.keysEndpoints = keysEndpoints;
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


        config.setAud(props.getProperty("audience"));
        config.setResource(props.getProperty("resource"));

        config.setKeysEndpoints(props.getProperty("keys.endpoint"));
        config.setClientsEndpoint(props.getProperty("clients.endpoint"));
        config.setScopesEndpoint(props.getProperty("scopes.endpoint"));
        config.setTokenEndpoint(props.getProperty("token.endpoint"));

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
