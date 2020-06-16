package no.il;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import no.il.dto.JWT;
import no.il.dto.TokenResponse;
import no.il.utils.HttpCaller;
import no.il.utils.Print;
import no.il.utils.PropertiesReader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class Authentication {

    PropertiesReader props = null;

    public Authentication(PropertiesReader props) {
        this.props = props;
    }

    public TokenResponse getConsumerAccessTokenFromMaskinporten(String clientID, String scope) {
        return getAccessTokenFromMaskinporten(clientID, scope, false);
    }

    public TokenResponse getAdminAccessTokenFromMaskinporten(String clientID, String scope) {
        return getAccessTokenFromMaskinporten(clientID, scope, true);
    }

    private TokenResponse getAccessTokenFromMaskinporten(String clientID, String scope, boolean adminUser) {
        TokenResponse tokenResponse = null;

        try {
            Print.out("\nStep 1: Generation signed JWT used to authenticate "+(adminUser?"Administrator":"Company"));
            String signedAuthJWT = generateSignedJWT(clientID, scope, adminUser);
            //String signedAuthJWT = generateSignedAdminJWT(clientID, scope);
            if (props.getShowAccessTokenDetails()) {
                Print.JWT(signedAuthJWT, "Base 64 encoded company JWT", props);
            }

            Print.out("\n\nStep 2: Calling Maskinporten to retrieve access_token");
            Print.out("Token endpoint: "+ (adminUser?props.getTokenAdminEndpoint():props.getTokenEndpoint()));
            tokenResponse = getTokenResponse(signedAuthJWT, adminUser);

            if (props.getShowAccessTokenDetails()) {
                Print.JSON("***** Response from Maskinporten *****", tokenResponse.getTokenResponseSource());
                Print.JWT(tokenResponse.getAccess_token(), "Base64 encoded Maskinporten access_token", props);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return tokenResponse;
    }


    /**
     * Retrieve the access_token based on the signed JWT.
     * @param svvJWT The signed JWT
     * @return TokenResponse with the access_token
     */
    private TokenResponse getTokenResponse(String svvJWT, boolean admin) {
        HttpURLConnection connection = null;
        TokenResponse tokenResponse = null;

        try {
            //Create connection
            URL url = new URL(admin?props.getTokenAdminEndpoint():props.getTokenEndpoint());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setDoOutput(true);

            // Setting Request Headers
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

            Map<String,Object> params = new LinkedHashMap<String,Object>();
            params.put("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer");
            params.put("assertion", svvJWT);

            String httpResponse = HttpCaller.execute(connection, params);
            tokenResponse = convertToTokenResponse(httpResponse);

        } catch (Exception e) {
            System.out.println("HTTP request to /token failed!");
            System.out.println(tokenResponse.getError());
            System.out.println(tokenResponse.getError_description());
            e.printStackTrace();
            return new TokenResponse();
        }
        if (tokenResponse.getError() != null) {
            System.out.println("Error requesting /token endpoint!");
            System.out.println("Error code: "+tokenResponse.getError());
            System.out.println("Error description: "+tokenResponse.getError_description());
        }
        return tokenResponse;
    }



    /**
     * Generate a signed JWT which can be used to authenticate SVV against Maskinporten.
     * The JWT must be signed with SVV virksomhetssertifikat to make Maskinporten abel to identify SVV.
     * The provided scope describe the required Maskinporten API the access_token shall be used for.
     *
     * @param clientId The client id
     * @param scope The scope/API the client is requesting access to
     * @return Signed JWT which need to be included in the call to Maskinporten /token API to authenticate SVV.
     * @throws Exception
     */

    private String generateSignedJWT(String clientId, String scope, boolean admin) throws Exception {

        List<Base64> certChain = new ArrayList<>();
        certChain.add(Base64.encode(props.getCertificate().getEncoded()));

        JWSHeader jwtHeader = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .x509CertChain(certChain)
                .build();

        String audience = (admin?props.getAudAdmin():props.getAud());

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .audience(admin?props.getAudAdmin():props.getAud())
                .claim("resource", props.getResource())
                .issuer(clientId)
                .claim("scope", scope)
                .jwtID(UUID.randomUUID().toString()) // Must be unique for each grant
                .issueTime(new java.util.Date(java.lang.System.currentTimeMillis() )) // Use UTC time!
                .expirationTime(new java.util.Date(java.lang.System.currentTimeMillis()  + 120000)) // Expiration time is 120 sec.
                .build();

        JWSSigner signer = new RSASSASigner(props.getPrivateKey());

        SignedJWT signedJWT = new SignedJWT(jwtHeader, claims);
        signedJWT.sign(signer);

        return signedJWT.serialize();
    }

    /**
     * Convert the token response JSON to a object.
     * @param json JSON to convert
     * @return The TokenResponse with information about the JSON
     */
    private TokenResponse convertToTokenResponse(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            //Print.JSON("====== TokenResponse from /token endpoint ======\n",json);
            TokenResponse token = objectMapper.readValue(json, TokenResponse.class);
            token.setTokenResponseSource(json);
            return token;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



}
