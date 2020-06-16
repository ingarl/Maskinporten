package no.il;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.util.Base64;

import no.il.dto.TokenResponse;
import no.il.utils.HttpCaller;
import no.il.utils.Print;
import no.il.utils.PropertiesReader;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.lang.reflect.Array;
import java.security.cert.CertificateEncodingException;
import java.security.interfaces.RSAPublicKey;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class Certificate extends Authentication{

    //private List<Base64> certList = new List<Base64>();
    private List<Base64> certList = new ArrayList();

    public Certificate(PropertiesReader props) {
        super(props);
    }

    public void goCertificate() {

        if (props.showCertificateInfo()) {
            showCertificateInfo(props.getCertificate(), "SVV Virksomhetssertifikat");
        }

        if (props.getCertificateUserRead() != null) {
            listRegisteredCertificates(props.getCertificateUserRead());
        }

        Print.JSON("test",getJWK(props.getCertificate()).toString());
        //System.out.println(getJWK(props.getCertificate()).toJSONString());


    }

    /**
     * Retrieve all registered certificates registered to the clients org.nr.
     * Requeire a client with access to the scope: idporten:dcr.read
     */
    public void listRegisteredCertificates(String clientId) {
        Print.out("\n\n===== Getting all certificates registered by client company =====");
        Print.out("Client ID: "+props.getCertificateUserRead());
        Print.out("---------------------------------------------");

        try {
            TokenResponse tokenResponse = super.getAdminAccessTokenFromMaskinporten(props.getCertificateUserRead(), "idporten:dcr.read");

            Print.out("\n\nStep 3: Calling Samarbeidsportalen to retrieve all certificates");
            Print.out("Certificate endpoint: "+ props.getCertificateEndpoint());

            String certs = HttpCaller.execute(tokenResponse.getAccess_token(), props.getCertificateEndpoint(),"GET");
            Print.JSON("***** Certificates retrieved from Maskinporten *****", certs);

        } catch(Exception e) {
            e.printStackTrace();
        }
        Print.out("---------------------------------------------\n\n");
    }

    public JSONObject getJWK(X509Certificate cert) {

        JSONArray array = new JSONArray();
        JSONObject object = new JSONObject();
        try {
            for (java.security.cert.Certificate oneCert:props.getCertificateChain()) {
                certList.add(Base64.encode(oneCert.getEncoded()));
            }

            RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) cert.getPublicKey())
                    .x509CertChain(certList)
                    .keyUse(KeyUse.SIGNATURE)
                    //.algorithm(JWSAlgorithm.RS256)
                    .keyID(cert.getSerialNumber().toString())
                    .build();
            //array.put(rsaKey.toJSONObject());
            object.append("keys", rsaKey.toJSONObject());
            return object;
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }



    /**
     * Prints out some information about the X509 certificate.
     * Mapping of key usages is based in information from these sites:
     * https://tools.ietf.org/html/rfc5280#section-4.2.1.3
     * https://tools.ietf.org/html/rfc3280#page-28
     * https://docs.oracle.com/javase/7/docs/api/java/security/cert/X509Certificate.html#getKeyUsage()
     *
     * Mapping of extended key usage is based on information from these sites:
     * https://tools.ietf.org/html/rfc5280
     * https://tools.ietf.org/html/rfc3280
     * http://javadoc.iaik.tugraz.at/iaik_jce/current/iaik/x509/extensions/ExtendedKeyUsage.html
     */
    private void showCertificateInfo(X509Certificate cert, String certificateType) {

        Print.out("\n\n===== Certificate information =====");
        Print.out("Certificate type: "+certificateType);
        Print.out("-----------------------------------");
        Print.out("Valid from : "+cert.getNotBefore());
        Print.out("Valid to   : "+cert.getNotAfter());
        Print.out("DN         : "+cert.getSubjectDN());
        Print.out("Issuer DN  : "+cert.getIssuerDN());
        Print.out("Key usage:");
        if (cert.getKeyUsage()[0]) { Print.out("* digitalSignature"); }
        if (cert.getKeyUsage()[1]) { Print.out("* nonRepudiation"); }
        if (cert.getKeyUsage()[2]) { Print.out("* keyEncipherment"); }
        if (cert.getKeyUsage()[3]) { Print.out("* dataEncipherment"); }
        if (cert.getKeyUsage()[4]) { Print.out("* keyAgreement"); }
        if (cert.getKeyUsage()[5]) { Print.out("* keyCertSign"); }
        if (cert.getKeyUsage()[6]) { Print.out("* cRLSign"); }
        if (cert.getKeyUsage()[7]) { Print.out("* encipherOnly"); }
        if (cert.getKeyUsage()[8]) { Print.out("* decipherOnly"); }

        Print.out("Extended usage:");
        try {
            for (String usage:cert.getExtendedKeyUsage()) {
                if (usage.equalsIgnoreCase("1.3.6.1.5.5.7.3.1")) { Print.out("* TLS Web server authentication"); }
                if (usage.equalsIgnoreCase("1.3.6.1.5.5.7.3.2")) { Print.out("* TLS Web client authentication"); }
                if (usage.equalsIgnoreCase("1.3.6.1.5.5.7.3.3")) { Print.out("* Code signing"); }
                if (usage.equalsIgnoreCase("1.3.6.1.5.5.7.3.4")) { Print.out("* E-mail protection"); }
                if (usage.equalsIgnoreCase("1.3.6.1.5.5.7.3.5")) { Print.out("* IP security end system"); }
                if (usage.equalsIgnoreCase("1.3.6.1.5.5.7.3.6")) { Print.out("* IP security tunnel termination"); }
                if (usage.equalsIgnoreCase("1.3.6.1.5.5.7.3.7")) { Print.out("* IP security user"); }
                if (usage.equalsIgnoreCase("1.3.6.1.5.5.7.3.8")) { Print.out("* Timestamping"); }
                if (usage.equalsIgnoreCase("1.3.6.1.5.5.7.3.9")) { Print.out("* OCSPstamping"); }
            }
        } catch (CertificateParsingException e) {
            e.printStackTrace();
        }

        // Comment in to show all of the certificate details.
        //System.out.println("\n\n\n"+cert.toString());

        Print.out("===================================\n\n");
    }
}
