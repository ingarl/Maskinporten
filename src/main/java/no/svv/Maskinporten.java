package no.svv;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import no.svv.dto.JWT;
import no.svv.dto.TokenResponse;
import no.svv.utils.HttpCaller;
import no.svv.utils.PropertiesReader;
import org.json.JSONObject;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * Dokumentasjon
 * https://www.mkyong.com/java/how-to-send-http-request-getpost-in-java/ (Er kanskje ikke så bra alikevel.
 * https://www.baeldung.com/java-http-request
 *
 * Lage nøkkelpar og konvertere til Java format
 * https://stackoverflow.com/questions/11410770/load-rsa-public-key-from-file
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

        try {
            Maskinporten theProgram = new Maskinporten(path);
            theProgram.getForerkortToken();
            /*theProgram.listSVVScope();
            theProgram.listSVVClients();
            theProgram.listRegisteredCertificates();
*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Maskinporten(String path) throws Exception {
        props = PropertiesReader.load(path);

    }

    /**
     * Retrieve all clients registered to the clients org.nr.
     * Requeire a client who has access to the scope: idporten:dcr.read
     * @throws Exception
     */
    private void listSVVClients() throws Exception {
        System.out.println("Getting all SVV clients");
        System.out.println("---------------------------------------------");
        String adminJWT = generateSignedJWT("oidc_svv_api2","idporten:dcr.read");
        decodeJWT(adminJWT,"SVV", true);
        TokenResponse tokenResponse = getTokenResponse(adminJWT);
        decodeJWT(tokenResponse.getAccess_token(),"AccessToken",true);
        prettyPrintJson("All SVV clients:\n",getClient(tokenResponse.getAccess_token()));
        //System.out.println("\nGetting clients:\n"+getClient(tokenResponse.getAccess_token()));
        System.out.println("---------------------------------------------\n\n");
    }

    /**
     * Retrieve all scopes registered to the clients org.nr.
     * Requeire a client who has access to the scope: idporten:scopes.read
     * @throws Exception
     */
    private void listSVVScope() throws Exception {
        System.out.println("Getting all scopes defined by SVV");
        System.out.println("---------------------------------------------");

        //Generate SVV JWT used to authenticate SVV against Maskinporten.
        String svvJWT = generateSignedJWT("oidc_svv_api2","idporten:scopes.read");
        decodeJWT(svvJWT,"SVV", true);

        TokenResponse tokenResponse = getTokenResponse(svvJWT);
        decodeJWT(tokenResponse.getAccess_token(),"AccessToken", true);

        String scopes = getScopes(tokenResponse.getAccess_token());
        prettyPrintJson("All SVV scopes:\n",scopes);

        System.out.println("---------------------------------------------\n\n");
    }

    /**
     * Retrieve all registered certificates registered to the clients org.nr.
     * Requeire a client who has access to the scope: idporten:dcr.read
     * @throws Exception
     */
    private void listRegisteredCertificates() throws Exception {
        System.out.println("Getting all certificates registered by SVV");
        System.out.println("---------------------------------------------");

        String svvJWT = generateSignedJWT("oidc_svv_api2","idporten:dcr.read");
        decodeJWT(svvJWT,"SVV", true);

        TokenResponse tokenResponse = getTokenResponse(svvJWT);
        String certs = getClientCerts(tokenResponse.getAccess_token());
        prettyPrintJson("All SVV keys:\n", certs);

        System.out.println("---------------------------------------------\n\n");
    }


    /**
     * Retrieves a access_token with the scope svv:forerkort.
     * Requeire a client who has access to the scope: svv:forerkort
     * @throws Exception
     */
    private void getForerkortToken() throws Exception {
        System.out.println("Getting access_token for svv:forerkort");
        System.out.println("---------------------------------------------");

        //String svvJWT = generateSignedJWT("e83bc4e7-d89f-4d23-bafd-dfdab5ed4c19","svv:forerkort");
        String svvJWT = generateSignedJWT("oidc_svv_api2","skatteetaten:medhjemmel");
        decodeJWT(svvJWT,"SVV", true);

        TokenResponse tokenResponse = getTokenResponse(svvJWT);
        decodeJWT(tokenResponse.getAccess_token(),"AccessToken", true);

        System.out.println("---------------------------------------------\n\n");
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
     * Returns all clients registered to the company
     * @param accessToken The access_token with client information.
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
     * @param accessToken
     * @return JSON with information of all certificates.
     */
    private String getClientCerts(String accessToken) {
        HttpURLConnection connection = null;
        String response = "";

        try {
            //Create connection
            URL url = new URL(props.getKeysEndpoints());
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


    /**
     * Decoder en Base64 encoded JWT.
     * @param jwtToken
     * @return
     */
    private JWT decodeJWT(String jwtToken, String title, boolean showResult) {

        if (jwtToken == null) {
            System.out.println("JWT token er null!");
            return null;
        }
        String[] split_string = jwtToken.split("\\.");
        String base64EncodedHeader = split_string[0];
        String base64EncodedBody = split_string[1];
        String base64EncodedSignature = split_string[2];

        byte[] decodedHeader = java.util.Base64.getMimeDecoder().decode(base64EncodedHeader);
        String header = new String(decodedHeader);


        byte[] decodedBody = java.util.Base64.getMimeDecoder().decode(base64EncodedBody);
        String body = new String(decodedBody);

        //System.out.println("~~~~~~~~~ JWT Signature ~~~~~~~");
        //byte[] decodedSignature = java.util.Base64.getMimeDecoder().decode(base64EncodedSignature);
        //String signature = new String(decodedSignature);
        //System.out.println("JWT signature : "+signature);

        //System.out.println("-------------------------");

        if (showResult) {
            System.out.println("~~~~~~~~~ "+title+" JWT ~~~~~~~");
            System.out.println(jwtToken);

            System.out.println("~~~~~~~~~ "+title+" JWT Header ~~~~~~~");
            //System.out.println(header);
            JSONObject headerJson = new JSONObject(header); // Convert text to object
            System.out.println(headerJson.toString(4)); // Print it with specified indentation

            System.out.println("~~~~~~~~~ "+title+" JWT Body ~~~~~~~");
            JSONObject bodyJson = new JSONObject(body); // Convert text to object
            System.out.println(bodyJson.toString(4)); // Print it with specified indentation

        }

        return new JWT(header,body);
    }

    private void prettyPrintJson(String title, String json) {
        //String scopes = getScopes(tokenResponse.getAccess_token());
        if (json.startsWith("[")) {
            json = json.substring(1,json.length()-2); // Responsen fra Difi er ikke helt JSON, så litt magi må på plass
        }
        //String scopesFixed = scopes.substring(1,scopes.length()-2); // Responsen fra Difi er ikke helt JSON, så litt magi må på plass

        JSONObject scopeJson = new JSONObject(json); // Convert text to object
        System.out.println(title+scopeJson.toString(4)); // Print it with specified indentation
    }
}
