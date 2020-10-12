package com.example.project4;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;


/******************************************************************************
 *
 * @author Qiuchen Z.
 * @date 4/2/20
 ******************************************************************************/
public class NASAImage {
    //  heroku hosts for different tasks
    private final static String LOCAL_HOST = "http://10.0.2.2:8080/api";
    private final static String HEROKU_HOST1 = "https://hidden-everglades-87782.herokuapp.com/api";
    private final static String HEROKU_HOST2 = "https://floating-mesa-72146.herokuapp.com/api";
    private String host = HEROKU_HOST2;
    NASAApplication na = null;

    /**
     * Provides a callback path such that the pictureReady method in that object is called
     * when the picture is available from the search.
     *
     * @param searchDate date to search for apod
     * @param na         this NASAApplication object
     */
    public void request(String searchDate, NASAApplication na) {
        this.na = na;
        new AsyncNASASearch().execute(searchDate);
    }

    /**
     * AsyncTask provides a simple way to use a thread separate from the UI thread in which to do network operations.
     */
    private class AsyncNASASearch extends AsyncTask<String, Void, ApodObj> {
        /**
         * Run in the helper thread.
         *
         * @param params date to search
         * @return ApodObj to display on phone
         */
        protected ApodObj doInBackground(String... params) {
            return getApod(params[0]);
        }

        /**
         * Run in the UI thread, allowing for safe UI updates.
         *
         * @param apof ApodObj to display on phone
         */
        protected void onPostExecute(ApodObj apof) {
            na.pictureReady(apof);
        }

        /**
         * Send request to web-server and return a ApodObj
         * that includes data to display on phone
         *
         * @param date date to search for apod
         * @return ApodObj
         */
        private ApodObj getApod(String date) {
            String requestUrl = String.format("%s?date=%s", host, date);
            JSONObject responseJson = getRemoteJson(requestUrl);

            try {
                System.out.println(responseJson.toString(4));
                // server cannot get apod from NASA
                // apod may return a media type other than image
                if (responseJson.getString("status").equals("no image found"))
                    return new ApodObj("", "", "", date, null);
                String title = responseJson.has("title") ? responseJson.getString("title") : "";
                String author = responseJson.has("copyright") ? responseJson.getString("copyright") : "";
                String description = responseJson.has("explanation") ? responseJson.getString("explanation") : "";
                String imageUrl = responseJson.has("url") ? responseJson.getString("url") : "";
                Bitmap image = getRemoteImage(imageUrl);
                return new ApodObj(title, author, description, date, image);
            } catch (Exception e) {
                e.printStackTrace();
                return new ApodObj("", "", "", date, null);
            }
        }

        /**
         * Send request to call web-server api
         *
         * @param url request url
         * @return apod info in json format
         */
        private JSONObject getRemoteJson(String url) {
            try {
                URL request = new URL(url);
                URLConnection con = request.openConnection();
                InputStream in = con.getInputStream();
                Scanner s = new Scanner(in).useDelimiter("\\A");
                String response = s.hasNext() ? s.next() : "";
                return new JSONObject(response);
            } catch (Exception e) {
                System.out.print("Error: when getting response from " + host + ", " + e);
                return new JSONObject();
            }
        }

        /**
         * Download picture
         *
         * @param imgUrl image url
         * @return image in Bitmap
         */
        private Bitmap getRemoteImage(String imgUrl) {
            // no image url
            if (imgUrl.length() == 0) return null;
            try {
                URL request = new URL(imgUrl);
                final URLConnection conn = request.openConnection();
                conn.connect();
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                Bitmap bm = BitmapFactory.decodeStream(bis);
                bis.close();
                return bm;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

}
