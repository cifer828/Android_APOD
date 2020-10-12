package edu.cmu.ds.project4;

import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;

/******************************************************************************
 * Servlet of Web Server.
 * Response to http://host/task2/api?date=YYYY-MM-DD&device=device
 * Call NASA API to get Astronomy Picture of the Day!
 *
 * @link https://api.nasa.gov/
 *
 * @author Qiuchen Z.
 * @date 4/3/20
 ******************************************************************************/

@WebServlet(name = "NASAServlet", urlPatterns = {"/api"})
public class NASAServlet extends javax.servlet.http.HttpServlet {

    /**
     * Deal with get request
     *
     * @param request  http request
     * @param response http response
     * @throws javax.servlet.ServletException http exception
     * @throws IOException                    json exception
     */
    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        // get the parameter from request if it exists
        String date = request.getParameter("date");
        String device = request.getHeader("User-Agent");
        JSONObject responseToNASA = NASAModel.getResponse(date);
        // generate http response
        response.getWriter().append(responseToNASA.toString());
        // logging
        NASAModel.writeLog(responseToNASA, date, device);
    }


}
