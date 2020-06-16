package no.il.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import no.il.dto.JWT;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Print {

    private static BufferedWriter bufferedWriter = null;

    /**
     * Decoder en Base64 encoded JWT.
     * @param jwtToken
     * @return
     */
    public static void JWT(String jwtToken, String title, PropertiesReader props) {

        if (jwtToken == null) {
            System.out.println("JWT token er null!");
            return;
        }
        String[] split_string = jwtToken.split("\\.");
        if (split_string.length != 3) {
            out("ERROR! Not a valid access_token. Reference/opaque token?");
            return;
        }
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
        out("***** " + title + " *****");
        out(jwtToken);
        if (props.prettyPrintJWT()) {
            out("***** Decoded JWT *****");
            out("***** JWT Header *****");
            //System.out.println(header);
            JSONObject headerJson = new JSONObject(header); // Convert text to object
            out(headerJson.toString(4)); // Print it with specified indentation

            out("***** JWT Body *****");
            JSONObject bodyJson = new JSONObject(body); // Convert text to object
            out(bodyJson.toString(4)); // Print it with specified indentation

            System.out.println("***** JWT Signature *****");
            out(base64EncodedSignature);

        }


        //return new JWT(header,body);
    }

    /**
     * Pretty print a JSON string
     * @param title Title describing the content of the JSON
     * @param json The JSON to format
     */
    public static void JSON(String title, String json) {
out(json);
        out(title);
        if (json.startsWith("[")) {
            out("[");
            JSONArray jsonArray = new JSONArray(json); // Convert text to array of JSON objects
            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                out(jsonObj.toString(4)); // Print it with specified indentation
            }
            out("]");
        } else if (json.startsWith("{")) {
            JSONObject scopeJson = new JSONObject(json); // Convert text to object
            out(scopeJson.toString(4)); // Print it with specified indentation
        } else {
            out("Not a JSON String: "+json);
        }
    }



    public static void initiateFile() {
        try {
            FileOutputStream outputStream = new FileOutputStream("logs/maskinporten."+getCurrentTime()+".log");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
            bufferedWriter = new BufferedWriter(outputStreamWriter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Generate the current time in a printable format.
     * @return The current time in the format Year.Month.Day.Hour.Minute.Second
     */
    public static String getCurrentTime() {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm.ss");
        LocalDateTime currentTime = LocalDateTime.now();
        return dateFormat.format(currentTime);

    }

    public static void closeFile() {
        try {
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void out(String output) {
        System.out.println(output);
        try {
            if (bufferedWriter != null) {
                bufferedWriter.write(output);
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

