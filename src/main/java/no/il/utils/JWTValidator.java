package no.il.utils;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

public class JWTValidator {



    public static JWTClaimsSet validate(String jwtToken, String jwks_uri) {

        // Set up a JWT processor to parse the tokens and then check their signature and validity time window (bounded by the "iat", "nbf" and "exp" claims)
        ConfigurableJWTProcessor jwtProcessor = new DefaultJWTProcessor();

        // The public RSA keys to validate the signatures will be sourced from the OAuth 2.0 server's JWK set, published at a well-known URL. The RemoteJWKSet
        // object caches the retrieved keys to speed up subsequent look-ups and can also gracefully handle key-rollover
        JWKSource keySource = null;
        try {
            keySource = new RemoteJWKSet(new URL(jwks_uri));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.out.println("Url til OIDC provider JWK er ikke gyldig: "+jwks_uri);
            return null;
            //return "Url til IDPorten er ikke gyldig";
        }

        // The expected JWS algorithm of the access tokens (agreed out-of-band)
        JWSAlgorithm expectedJWSAlg = JWSAlgorithm.RS256;

        // Configure the JWT processor with a key selector to feed matching public RSA keys sourced from the JWK set URL
        JWSKeySelector keySelector = new JWSVerificationKeySelector(expectedJWSAlg, keySource);
        jwtProcessor.setJWSKeySelector(keySelector);

        // Process the token
        SecurityContext ctx = null; // optional context parameter, not required here
        JWTClaimsSet claimsSet = null;
        try {
            claimsSet = jwtProcessor.process(jwtToken, ctx);
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("ParseException "+e.getMessage());
            //return "ParseException "+e.getMessage();
        } catch (BadJOSEException e) {
            e.printStackTrace();
            System.out.println("BadJOSEException "+e.getMessage());
            //return "BadJOSEException "+e.getMessage();
        } catch (JOSEException e) {
            e.printStackTrace();
            System.out.println("JOSEException "+e.getMessage());
            //return "JOSEException "+e.getMessage();
        }



// Print out the token claims set
        //System.out.println(claimsSet.toJSONObject());
        return null;
        //return claimsSet.toJSONObject().toJSONString();
    }
}

