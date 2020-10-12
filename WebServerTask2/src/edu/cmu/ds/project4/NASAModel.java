package edu.cmu.ds.project4;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

/******************************************************************************
 * Controller of web server.
 *
 * @author Qiuchen Z.
 * @date 4/3/20
 ******************************************************************************/
public class NASAModel {
    final static MongoConn mongoConn = MongoConn.getInstance();

    /**
     * Edit and forward request from Android device to NASA API
     *
     * @param date   apod date to search image
     * @return JSON response from NASA API
     * @throws IOException json excepton
     */
    public static JSONObject getResponse(String date) {
        // not a correct api format
        JSONObject jsonObject = new JSONObject();
        if (date == null) {
            jsonObject.put("status", "bad request");
            return jsonObject;
        }

        // send request to NASA api and get response json
        String requestUrl = String.format("https://api.nasa.gov/planetary/apod?date=%s&api_key=QdnH2FNtAOzJS3rrmfJ4dRbgntCQaOeyTGIrXndI", date);
        try {
            URL requestNASA = new URL(requestUrl);
            URLConnection con = requestNASA.openConnection();
            InputStream in = con.getInputStream();
            String responseNASA = IOUtils.toString(in, StandardCharsets.UTF_8);
            jsonObject = new JSONObject(responseNASA);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // cannot get apod from NASA
        if (jsonObject.has("code") && (jsonObject.getInt("code") == 500 || jsonObject.getInt("code") == 400)) {
            System.out.println(jsonObject.toString());
            jsonObject = new JSONObject();
            jsonObject.put("status", "bad date");
        } else {
            // remove redundant keys
            jsonObject.remove("hdurl");
            jsonObject.remove("service_version");
            jsonObject.remove("date");
            // apof may return other media type rather than image
            // in this project, I only process image
            if (jsonObject.has("media_type") && jsonObject.getString("media_type").equals("image"))
                jsonObject.put("status", "good");
            else
                jsonObject.put("status", "no image found");
            jsonObject.remove("media_type");
        }

        return jsonObject;
    }

    /**
     * Write log
     *
     * @param responseToNASA response json
     * @param date           apod day
     * @param device         androd device
     */
    public static void writeLog(JSONObject responseToNASA, String date, String device, int latency) {
        // process response json and write log
        responseToNASA.put("imgDate", date);
        responseToNASA.put("device", device);
        responseToNASA.put("timestamp", System.currentTimeMillis());
        responseToNASA.put("latency", latency);
        responseToNASA.put("requestToNASA", String.format("https://api.nasa.gov/planetary/apod?date=%s&api_key=QdnH2FNtAOzJS3rrmfJ4dRbgntCQaOeyTGIrXndI", date));
        mongoConn.insertLog(responseToNASA);
    }

}
