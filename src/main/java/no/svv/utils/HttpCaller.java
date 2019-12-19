package no.svv.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Map;

public class HttpCaller {

    /**
     * Execute the HTTP call. If it's a POST request the parameters to include in the request must be included in the
     * params parameter. If it's a GET request or no attributes are required set the params parameter to null.
     * @param connection The connection with information about the endpoint.
     * @param params The list of parameters to include in the request.
     * @return
     */
    public static String execute(HttpURLConnection connection, Map<String,Object> params) {
        try {
            if (params != null) {
                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                byte[] postDataBytes = postData.toString().getBytes("UTF-8");

                connection.getOutputStream().write(postDataBytes);

            }

            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\n');
                }
                rd.close();
            } else if (responseCode == 401) { //Unauthorized
                System.out.println("User not authenticated");
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\n');
                }
                rd.close();
                //response.append("{\"error\":\"401\",\"error_massage\":\"message\"}");
            } else {
                InputStream is = connection.getErrorStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                //StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
                String line;
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\n');
                }
                rd.close();
            }

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }


    public static String execute(HttpURLConnection connection) {
        return execute(connection,null);
    }
}
