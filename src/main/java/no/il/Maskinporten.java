package no.il;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import no.il.utils.decode;
import no.il.dto.TokenResponse;
import no.il.utils.HttpCaller;
import no.il.utils.PropertiesReader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 *
 */
public class Maskinporten {

    PropertiesReader props = null;

    public static void main(String args[]) {

        String path = "";

        if (args != null && args.length == 1 && args[0] != null) {
            path = args[0];
        } else {
            System.out.println("Usage: java -jar maskinporten.jar <property file name>");
            System.exit(0);
        }

        Maskinporten theProgram = new Maskinporten(path);
        theProgram.goProgram();
    }

    public Maskinporten(String path) {
        try {
            props = PropertiesReader.load(path);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void goProgram() {
        if (props.getAccessTokenUser() != null) {
            getAccessToken(props.getAccessTokenUser(), props.getAccessTokenScope());
        }

        if (props.getClientUserRead() != null) {
            listClients(props.getClientUserRead());
        }

        if (props.getScopeUserRead() != null) {
            listScopes(props.getScopeUserRead());
        }

        if (props.getCertificateUserRead() != null) {
            listRegisteredCertificates(props.getCertificateUserRead());
        }

    }

    /**
     * Retrieves a access_token for the provided scope. The client must be configured with the required scope in
     * Maskinporten.
     * @param clientId - The client with access to the scope
     * @param scope - The scope to be included in the access_token
     */
    public void getAccessToken(String clientId, String scope) {
        try {
            System.out.println("Getting access_token for client '" + clientId + "' with scope: " + scope);
            System.out.println("---------------------------------------------");

            String svvJWT = generateSignedJWT(clientId, scope);
            decode.JWT(svvJWT, "Virksomhet", props.prettyPrintJWT());

            TokenResponse tokenResponse = getTokenResponse(svvJWT);
            decode.JWT(tokenResponse.getAccess_token(), "AccessToken", props.prettyPrintJWT());

            System.out.println("---------------------------------------------\n\n");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * Retrieve all clients registered to the clients org.nr.
     * Require a client who has access to the scope: idporten:dcr.read
     */
    public void listClients(String clientId) {
        try {
            System.out.println("Getting all SVV clients");
            System.out.println("---------------------------------------------");

            String adminJWT = generateSignedJWT(clientId, "idporten:dcr.read");
            decode.JWT(adminJWT, "Virksomhet", props.prettyPrintJWT());

            TokenResponse tokenResponse = getTokenResponse(adminJWT);
            decode.JWT(tokenResponse.getAccess_token(), "AccessToken", props.prettyPrintJWT());

            String clients = getClient(tokenResponse.getAccess_token());
            decode.JSON("All clients:\n", clients);

            System.out.println("---------------------------------------------\n\n");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieve all scopes registered to the clients org.nr.
     * Requeire a client who has access to the scope: idporten:scopes.read
     */
    public void listScopes(String clientId) {
        try {
            System.out.println("Getting all scopes defined by clients company");
            System.out.println("---------------------------------------------");

            //Generate SVV JWT used to authenticate SVV against Maskinporten.
            String svvJWT = generateSignedJWT(clientId, "idporten:scopes.read");
            decode.JWT(svvJWT, "Virksomhet", props.prettyPrintJWT());

            TokenResponse tokenResponse = getTokenResponse(svvJWT);
            decode.JWT(tokenResponse.getAccess_token(), "AccessToken", props.prettyPrintJWT());

            String scopes = getScopes(tokenResponse.getAccess_token());
            decode.JSON("All scopes:\n", scopes);

            System.out.println("---------------------------------------------\n\n");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieve all registered certificates registered to the clients org.nr.
     * Requeire a client with access to the scope: idporten:dcr.read
     */
    public void listRegisteredCertificates(String clientId) {
        try {
            System.out.println("Getting all certificates registered by the clients company");
            System.out.println("---------------------------------------------");

            String svvJWT = generateSignedJWT(clientId, "idporten:dcr.read");
            decode.JWT(svvJWT, "Virksomhet", props.prettyPrintJWT());

            TokenResponse tokenResponse = getTokenResponse(svvJWT);
            decode.JWT(tokenResponse.getAccess_token(), "AccessToken", props.prettyPrintJWT());

            String certs = getClientCerts(tokenResponse.getAccess_token());
            decode.JSON("All certificates:\n", certs);

            System.out.println("---------------------------------------------\n\n");
        } catch(Exception e) {
            e.printStackTrace();
        }
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
    private  String generateSignedJWT(String clientId, String scope) throws Exception {

        List<Base64> certChain = new ArrayList<>();
        certChain.add(Base64.encode(props.getCertificate().getEncoded()));

        JWSHeader jwtHeader = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .x509CertChain(certChain)
                .build();

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .audience(props.getAud())
                //.claim("resource", props.getResource())
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
     * Retrieve the access_token based on the signed JWT.
     * @param svvJWT The signed JWT
     * @return TokenResponse with the access_token
     */
    private TokenResponse getTokenResponse(String svvJWT) {
        HttpURLConnection connection = null;
        TokenResponse tokenResponse = null;

        try {
            //Create connection
            URL url = new URL(props.getTokenEndpoint());
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
     * Retrieve the JSON with all scopes the access_token has access to.
     * @param accessToken The OAUTH access_token giving access to the /scopes endpoint.
     * @return The JSON with all scopes.
     */
    private String getScopes(String accessToken) {
        HttpURLConnection connection = null;
        String response = "";

        try {
            //Create connection
            URL url = new URL(props.getScopesEndpoint());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Setting Request Headers
            connection.setRequestProperty("Authorization", "Bearer "+accessToken);
            connection.setRequestProperty("Content-Type","application/json");

            response = HttpCaller.execute(connection);
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
        return response;
    }

    /**
     * Retrieve the JSON with information of all clients the access_token has access to.
     * @param accessToken The OAUTH access_token giving access to the /clients endpoint.
     * @return The JSON with all clients registered to the company.
     */
    private String getClient(String accessToken) {
        HttpURLConnection connection = null;
        String response = "";

        try {
            //Create connection
            URL url = new URL(props.getClientsEndpoint());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Setting Request Headers
            connection.setRequestProperty("Authorization", "Bearer "+accessToken);
            connection.setRequestProperty("Content-Type","application/json");

            response = HttpCaller.execute(connection);
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
        return response;
    }

    /**
     * Get alle certificates registered by the company
     * @param accessToken The OAUTH access_token giving access to the /jwks endpoint.
     * @return JSON with information of all certificates.
     */
    private String getClientCerts(String accessToken) {
        HttpURLConnection connection = null;
        String response = "";

        try {
            //Create connection
            URL url = new URL(props.getCertificateEndpoint());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Setting Request Headers
            connection.setRequestProperty("Authorization", "Bearer "+accessToken);
            connection.setRequestProperty("Content-Type","application/json");

            response = HttpCaller.execute(connection);
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
        return response;
    }

    /**
     * Convert the token response JSON to a object.
     * @param json JSON to convert
     * @return The TokenResponse with information about the JSON
     */
    public static TokenResponse convertToTokenResponse(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            TokenResponse token = objectMapper.readValue(json, TokenResponse.class);
            return token;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
