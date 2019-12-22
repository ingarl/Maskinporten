package no.il.utils;

import no.il.dto.JWT;
import org.json.JSONObject;

public class decode {

    /**
     * Decoder en Base64 encoded JWT.
     * @param jwtToken
     * @return
     */
    public static JWT JWT(String jwtToken, String title, boolean showResult) {

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

        System.out.println("~~~~~~~~~ "+title+" JWT ~~~~~~~");
        System.out.println(jwtToken);
        if (showResult) {

            System.out.println("~~~~~~~~~ "+title+" JWT Header ~~~~~~~");
            //System.out.println(header);
            JSONObject headerJson = new JSONObject(header); // Convert text to object
            System.out.println(headerJson.toString(4)); // Print it with specified indentation

            System.out.println("~~~~~~~~~ "+title+" JWT Body ~~~~~~~");
            JSONObject bodyJson = new JSONObject(body); // Convert text to object
            System.out.println(bodyJson.toString(4)); // Print it with specified indentation

        }

        return new no.il.dto.JWT(header,body);
    }

    /**
     * Pretty print a JSON string
     * @param title Title descibing the content of the JSON
     * @param json The JSON to format
     */
    public static void JSON(String title, String json) {
        if (json.startsWith("[")) {
            json = json.substring(1,json.length()-2); // Responsen fra Difi er ikke helt JSON, så litt magi må på plass
        }

        JSONObject scopeJson = new JSONObject(json); // Convert text to object
        System.out.println(title+scopeJson.toString(4)); // Print it with specified indentation
    }
}

