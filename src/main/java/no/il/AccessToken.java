package no.il;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import no.il.dto.JWT;
import no.il.dto.TokenResponse;
import no.il.utils.Print;
import no.il.utils.PropertiesReader;
import org.json.JSONObject;

import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AccessToken extends Authentication{

    public AccessToken(PropertiesReader props) {
        super(props);
    }

    public void goAccessToken() {

        if (props.getAccessTokenUser() != null) {
            getAccessToken();
        }

        if (props.getAccessTokenAdminUser() != null) {
            super.getAdminAccessTokenFromMaskinporten(props.getAccessTokenAdminUser(), props.getAccessTokenAdminScope());
        }


    }

    /**
     * Retrieves an access_token for the provided scope. The client must be configured with the required scope in
     * Maskinporten.

     */
    public void getAccessToken() {
        Print.out("\n\n===== Getting access_token from Maskinporten =====");
        Print.out("Client ID: "+props.getAccessTokenUser());
        Print.out("Scope: "+props.getAccessTokenScope());
        Print.out("----------------------------------");
        try {
            TokenResponse tokenResponse = getConsumerAccessTokenFromMaskinporten(props.getAccessTokenUser(), props.getAccessTokenScope());

            if (props.issueExampleToken()) {
                Print.out("\n\nStep 3: Generating example SVVSecurityToken based om access_token from Maskinporten");
                String exampelToken = generateExampleSecurityToken(tokenResponse);
                Print.JWT(exampelToken, "Example security token", props);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        Print.out("---------------------------------------------\n\n");
    }



    private String generateExampleSecurityToken(TokenResponse tokenResponse) throws Exception {

        String[] split_string = tokenResponse.getAccess_token().split("\\.");
        if (split_string.length != 3) {
            Print.out("ERROR! Not a valid access_token. Reference/opaque token?");
            return null;
        }
        String base64EncodedBody = split_string[1];

        byte[] decodedBody = java.util.Base64.getMimeDecoder().decode(base64EncodedBody);
        String body = new String(decodedBody);

        JSONObject maskinportenJSONObject = new JSONObject(body);

        List<Base64> certChain = new ArrayList<>();
        certChain.add(Base64.encode(props.getCertificate().getEncoded()));

        JWSHeader jwtHeader = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .x509CertChain(certChain) // Hvis man ønsker at sertifikate som skal benyttes for å sjekke signatur skal være en del av JWT så skal denne med. For alle andre så benyttes keyID for å hente sertifikatet fra JWK endepunktet.
                .keyID(props.getCertificate().getSerialNumber().toString())
                .build();

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .audience("SVV internt")
                .claim("client_amr", maskinportenJSONObject.get("client_amr"))
                .claim("scope", maskinportenJSONObject.get("scope"))
                .claim("token_type", "Bearer")
                .claim("client_id", maskinportenJSONObject.get("client_id"))
                .claim("client_orgno", maskinportenJSONObject.getJSONObject("consumer").get("ID"))
                .issuer("Maskinporten klienten")
                .jwtID(UUID.randomUUID().toString()) // Must be unique for each grant
                .issueTime(new java.util.Date(java.lang.System.currentTimeMillis() )) // Use UTC time!
                .expirationTime(new java.util.Date(java.lang.System.currentTimeMillis()  + 120000)) // Expiration time is 120 sec.
                .build();

        JWSSigner signer = new RSASSASigner(props.getPrivateKey());

        SignedJWT signedJWT = new SignedJWT(jwtHeader, claims);
        signedJWT.sign(signer);

        return signedJWT.serialize();
    }
}
