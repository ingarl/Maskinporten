package no.il.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenResponse {
    private String access_token;
    private String token_type;
    private String expires_in;
    private String refresh_token;
    private String scope;
    private Date generated;
    private String tokenResponseSource;

    //Error attributes
    private String error_description;
    private String error;

    public TokenResponse() {
        generated = new Date();
    }

    public Date getGenerated() {
        return generated;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getAccess_token() {
        return access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public Long getExpires_in() {
        return new Long(expires_in);
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public String getScope() {
        return scope;
    }

    public String getError_description() {
        return error_description;
    }

    public void setError_description(String error_description) {
        this.error_description = error_description;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getTokenResponseSource() { return tokenResponseSource; }

    public void setTokenResponseSource(String tokenResponseSource) { this.tokenResponseSource = tokenResponseSource; }

    @Override
    public String toString() {
        return "TokenResponse {" +
                "\n access_token='" + access_token + '\'' +
                ",\n token_type='" + token_type + '\'' +
                ",\n expires_in='" + expires_in + '\'' +
                ",\n refresh_token='" + refresh_token + '\'' +
                ",\n scope='" + scope + '\'' +
                ",\n error='" + error + '\'' +
                ",\n error_description='" + error_description + '\'' +
                "\n"+'}';
    }
}
