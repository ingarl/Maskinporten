package no.il.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Properties;

public class PropertiesReader {

    private String audAdmin;
    private String aud;
    private String resource;
    private String baseUrl;
    private String jwks_uri;
    private String accessTokenUser, accessTokenScope, certificateUserRead, clientUserRead;
    private String tokenEndpoint, tokenAdminEndpoint, scopesEndpoint, clientsEndpoint, certificateEndpoint;
    private String adminUser, accessTokenAdminUser, accessTokenAdminScope;

    // Propertiesd for administration of scope
    private boolean scopeList, scopeAccess, scopeAdd, scopeDelete;
    private String scopeListEndpoint;
    private String scopeAccessEndpoint, scopeAccessId;
    private String scopeAddEndpoint, scopeAddId, ScopeAddOrgnr;
    private String scopeDeleteEndpoint, scopeDeleteId, ScopeDeleteOrgnr;

    private boolean showAccessTokenDetails;
    private boolean prettyPrintJWT;
    private boolean saveResult;
    private boolean issueExampleToken;
    private boolean showCertificateInfo;
    private X509Certificate certificate;
    private PrivateKey privateKey;
    private Certificate[] certificateChain;

    public boolean prettyPrintJWT() {
        return prettyPrintJWT;
    }

    public void setPrettyPrintJWT(String prettyPrintJWTResult) {
        if (prettyPrintJWTResult != null && prettyPrintJWTResult.equalsIgnoreCase("true")) {
            prettyPrintJWT = true;
        }
    }

    public boolean getShowAccessTokenDetails() {
        return showAccessTokenDetails;
    }

    public void setShowAccessTokenDetails(String showAccessTokenDetails) {
        if (showAccessTokenDetails != null && showAccessTokenDetails.equalsIgnoreCase("true")) {
            this.showAccessTokenDetails = true;
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


    public boolean issueExampleToken() { return issueExampleToken; }

    public void setIssueExampleToken(String issueExampleToken) {
        if (issueExampleToken != null && issueExampleToken.equalsIgnoreCase("true")) {
            this.issueExampleToken = true;
        }
    }

    public boolean showCertificateInfo() {
        return showCertificateInfo;
    }

    public void setShowCertificateInfo(String showCertificateInfo) {
        if (showCertificateInfo != null && showCertificateInfo.equalsIgnoreCase("true")) {
            this.showCertificateInfo = true;
        }
    }


    // Getter and setter for administration of scopes
    public String getScopeListEndpoint() { return scopeListEndpoint; }
    public void setScopeListEndpoint(String scopeListEndpoint) { this.scopeListEndpoint = scopeListEndpoint; }
    public boolean getScopeList() { return scopeList; }
    public void setScopeList(String scopeList) {
        if (scopeList != null && scopeList.equalsIgnoreCase("true")) {
            this.scopeList = true;
        }
    }

    public String getScopeAddEndpoint() { return scopeAddEndpoint; }
    public void setScopeAddEndpoint(String scopeAddEndpoint) { this.scopeAddEndpoint = scopeAddEndpoint; }
    public boolean getScopeAdd() { return scopeAdd; }
    public void setScopeAdd(String scopeAdd) {
        if (scopeAdd != null && scopeAdd.equalsIgnoreCase("true")) {
            this.scopeAdd = true;
        }
    }
    public String getScopeAddId() { return scopeAddId; }
    public void setScopeAddId(String scopeAddId) { this.scopeAddId = scopeAddId; }
    public String getScopeAddOrgnr() { return ScopeAddOrgnr; }
    public void setScopeAddOrgnr(String scopeAddOrgnr) { ScopeAddOrgnr = scopeAddOrgnr; }

    public String getScopeDeleteEndpoint() { return scopeDeleteEndpoint; }
    public void setScopeDeleteEndpoint(String scopeDeleteEndpoint) { this.scopeDeleteEndpoint = scopeDeleteEndpoint; }
    public boolean getScopeDelete() { return scopeDelete; }
    public void setScopeDelete(String scopeDelete) {
        if (scopeDelete != null && scopeDelete.equalsIgnoreCase("true")) {
            this.scopeDelete = true;
        }
    }
    public String getScopeDeleteId() { return scopeDeleteId; }
    public void setScopeDeleteId(String scopeDeleteId) { this.scopeDeleteId = scopeDeleteId; }
    public String getScopeDeleteOrgnr() { return ScopeDeleteOrgnr; }
    public void setScopeDeleteOrgnr(String scopeDeleteOrgnr) { ScopeDeleteOrgnr = scopeDeleteOrgnr; }

    public boolean getScopeAccess() { return scopeAccess; }
    public void setScopeAccess(String scopeAccess) {
        if (scopeAccess != null && scopeAccess.equalsIgnoreCase("true")) {
            this.scopeAccess = true;
        }
    }
    public String getScopeAccessEndpoint() { return scopeAccessEndpoint; }
    public void setScopeAccessEndpoint(String scopeAccessEndpoint) { this.scopeAccessEndpoint = scopeAccessEndpoint; }
    public String getScopeAccessId() { return scopeAccessId; }
    public void setScopeAccessId(String scopeAccessId) { this.scopeAccessId = scopeAccessId; }




    public String getAdminUser() {
        return adminUser;
    }

    public void setAdminUser(String adminUser) {
        this.adminUser = adminUser;
    }

    public String getAccessTokenAdminUser() { return accessTokenAdminUser; }

    public void setAccessTokenAdminUser(String accessTokenAdminUser) { this.accessTokenAdminUser = accessTokenAdminUser; }

    public String getAccessTokenAdminScope() { return accessTokenAdminScope; }

    public void setAccessTokenAdminScope(String accessTokenAdminScope) { this.accessTokenAdminScope = accessTokenAdminScope; }

    public String getJwks_uri() {
        return jwks_uri;
    }

    public void setJwks_uri(String jwks_uri) {
        this.jwks_uri = jwks_uri;
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

    public String getAudAdmin() {
        return audAdmin;
    }

    public void setAudAdmin(String audAdmin) {
        this.audAdmin = audAdmin;
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

    public String getTokenAdminEndpoint() {
        return tokenAdminEndpoint;
    }

    public void setTokenAdminEndpoint(String tokenAdminEndpoint) {
        this.tokenAdminEndpoint = tokenAdminEndpoint;
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

    public X509Certificate getCertificate() { return certificate; }
    public void setCertificate(X509Certificate certificate) {
        this.certificate = certificate;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }
    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public Certificate[] getCertificateChain() { return certificateChain; }
    public void setCertificateChain(Certificate[] certificateChain) { this.certificateChain = certificateChain; }

    public static PropertiesReader load(String path) throws Exception {
        PropertiesReader config = new PropertiesReader();

        Properties props = readPropertyFile(path);

        config.setPrettyPrintJWT(props.getProperty("prettyPrintJWT"));
        config.setSaveResult(props.getProperty("saveResult"));
        config.setIssueExampleToken(props.getProperty("issueExampleToken"));
        config.setShowCertificateInfo(props.getProperty("showCertificateInfo"));
        config.setShowAccessTokenDetails(props.getProperty("showAccessTokenDetails"));


        config.setAudAdmin(props.getProperty("audience.admin"));
        config.setAud(props.getProperty("audience"));
        config.setResource(props.getProperty("resource"));

        config.setJwks_uri(props.getProperty("jwks_uri"));

        config.setTokenEndpoint(props.getProperty("token.endpoint"));
        config.setTokenAdminEndpoint(props.getProperty("token.endpoint.admin"));
        config.setAccessTokenUser(props.getProperty("accesstoken.user"));
        config.setAccessTokenScope(props.getProperty("accesstoken.scope"));

        config.setScopesEndpoint(props.getProperty("scopes.endpoint"));
        config.setAdminUser(props.getProperty("admin.user"));
        config.setAccessTokenAdminUser(props.getProperty("accesstoken.admin.user"));
        config.setAccessTokenAdminScope(props.getProperty("accesstoken.admin.scope"));



        config.setCertificateEndpoint(props.getProperty("certificate.endpoint"));
        config.setCertificateUserRead(props.getProperty("certificate.read.user"));

        config.setClientsEndpoint(props.getProperty("clients.endpoint"));
        config.setClientUserRead(props.getProperty("client.read.user"));

        String keystoreFile = props.getProperty("keystore.file");
        String keystorePassword = props.getProperty("keystore.password");
        String keystoreAlias = props.getProperty("keystore.alias");
        String keystoreAliasPassword = props.getProperty("keystore.alias.password");


        config.setScopeListEndpoint(props.getProperty("scope.list.endpoint"));
        config.setScopeList(props.getProperty("scope.list"));

        config.setScopeAccessEndpoint(props.getProperty("scope.access.endpoint"));
        config.setScopeAccess(props.getProperty("scope.access"));
        config.setScopeAccessId(props.getProperty("scope.access.id"));

        config.setScopeAddEndpoint(props.getProperty("scope.add.endpoint"));
        config.setScopeAdd(props.getProperty("scope.add"));
        config.setScopeAddId(props.getProperty("scope.add.id"));
        config.setScopeAddOrgnr(props.getProperty("scope.add.orgnr"));

        config.setScopeDeleteEndpoint(props.getProperty("scope.delete.endpoint"));
        config.setScopeDelete(props.getProperty("scope.delete"));
        config.setScopeDeleteId(props.getProperty("scope.delete.id"));
        config.setScopeDeleteOrgnr(props.getProperty("scope.delete.orgnr"));

        loadCertificateAndKeyFromFile(config, keystoreFile, keystorePassword, keystoreAlias, keystoreAliasPassword);

        return config;
    }

    private static void loadCertificateAndKeyFromFile(PropertiesReader config, String keyStoreFile, String keyStorePassword, String alias, String keyPassword) throws Exception {
        InputStream is = new FileInputStream(keyStoreFile);
        loadCertificate(config, is, keyStorePassword, alias, keyPassword);

    }

    private static void loadCertificate(PropertiesReader config, InputStream is, String keystorePassword, String alias, String keyPassword) throws Exception {
        //KeyStore keyStore = KeyStore.getInstance("JKS");
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(is, keystorePassword.toCharArray());

        PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, keyPassword.toCharArray()); // Read from KeyStore
        X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);
        Certificate[] certificateChain = keyStore.getCertificateChain(alias);

        config.setCertificate(certificate);
        config.setPrivateKey(privateKey);
        config.setCertificateChain(certificateChain);
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
