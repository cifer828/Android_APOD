<%@ page import="edu.cmu.ds.project4.MongoConn" %>
<%@ page import="org.json.JSONArray" %>
<%@ page import="org.json.JSONObject" %>
<%@ page import="java.util.List" %>
<%--
  Created by IntelliJ IDEA.
  User: qiuchenzhang
  Date: 4/3/20
  Time: 2:37 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css"
          integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
    <title>Dashboard</title>

</head>
<body>
<% MongoConn mongoConn = MongoConn.getInstance();
    mongoConn.retrieveLog();%>

<%-- Dispply all logs--%>
<div class="container-fluid">
    <h3>Server Log</h3>
    <p class="info">Sorted by timestamp in descending order. Only show part of Explanation.</p>
    <table class="table table-striped">
        <thead>
        <tr>
            <th scope="col">Time</th>
            <th scope="col">Android Type</th>
            <th scope="col">Request Image Date</th>
            <th scope="col">Latency</th>
            <th scope="col">Status</th>
            <th scope="col">Request To NASA</th>
            <th scope="col">Title</th>
            <th scope="col">Copyright</th>
            <th scope="col">Explanation</th>
            <th scope="col">Image URL</th>
        </tr>
        </thead>
        <tbody>
        <% JSONArray logs = mongoConn.getLogs();
            for (Object logObj : logs) {
                JSONObject log = (JSONObject) logObj; %>
        <tr>
            <td><%= log.getString("time") %>
            </td>
            <td><%= log.has("device") ? log.getString("device") : "" %>
            </td>
            <td><%= log.has("imgDate") ? log.getString("imgDate") : "" %>
            </td>
            <td><%= log.getInt("latency") %>
            </td>
            <td><%= log.getString("status") %>
            </td>
            <td><%= log.has("requestToNASA") ? log.getString("requestToNASA") : "" %>
            </td>
            <td><%= log.has("title") ? log.getString("title") : "" %>
            </td>
            <td><%= log.has("copyright") ? log.getString("copyright") : "" %>
            </td>
            <td><%= log.has("explanation") ? log.getString("explanation").substring(0, 100) + "..." : "" %>
            </td>
            <td><%= log.has("url") ? log.getString("url") : "" %>
            </td>
        </tr>
        <% } %>
        </tbody>
    </table>
</div>

<%--Analytics--%>
<%--Google Chart Reference: https://developers.google.com/chart/interactive/docs/gallery/barchart --%>
<div class="container-fluid">
    <h3>Operations Analytics</h3>
    <h4>1. Android Request status</h4>
    <p class="warning">Note: APOF API may return other media type rather than image. </p>
    <% List<List<String>> sortedStatus = mongoConn.filedCount("status");
        List<List<String>> sortedDevice = mongoConn.filedCount("device");
        List<List<String>> sortedDate = mongoConn.filedCount("imgDate");
    %>
<%--    Pie chart for status analytics  --%>
    <div id="piechart" class="container" >
        <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
        <script type="text/javascript">
            // Load google charts
            google.charts.load('current', {'packages': ['corechart']});
            google.charts.setOnLoadCallback(drawChart);

            // Draw the chart and set the chart values
            function drawChart() {
                var list = [['Status', 'Frequency']];
                <% for (int i = 0; i < Math.min(10, sortedStatus.size()); i++) { %>
                list.push(["<%= sortedStatus.get(i).get(0) %>", <%= Integer.parseInt(sortedStatus.get(i).get(1)) %>])
                <% } %>
                console.log(list);
                var data = google.visualization.arrayToDataTable(list);

                // Optional; add a title and set the width and height of the chart
                var options = {'title': 'API Request Status', 'width': 550, 'height': 400};

                // Display the chart inside the <div> element with id="piechart"
                var chart = new google.visualization.PieChart(document.getElementById('piechart'));
                chart.draw(data, options);
            }
        </script>
    </div>
<%--    Table for status analytics    --%>
    <table class="table table-striped">
        <thead>
        <tr>
            <th scope="col">Status</th>
            <th scope="col">Frequency</th>
        </tr>
        </thead>
        <tbody>
        <% for (int i = 0; i < Math.min(10, sortedStatus.size()); i++) { %>
        <tr>
            <td><%= sortedStatus.get(i).get(0) %>
            </td>
            <td><%= sortedStatus.get(i).get(1)  %>
            </td>
        </tr>
        <% } %>
        </tbody>
    </table>

    <h4>2. Top 10 Searched Image Date of APOD</h4>
    <p class="info">Show all dates if these are less than 10 records</p>
<%--    Bar chart for date analytics    --%>
    <div class="container" id="barchart_date">
        <script type="text/javascript">
            google.charts.load("current", {packages: ["corechart"]});
            google.charts.setOnLoadCallback(drawChart);

            function drawChart() {
                var list = [["Element", "Density", {role: "style"}]]
                <% for (int i = 0; i < Math.min(10, sortedDate.size()); i++) { %>
                list.push(["<%= sortedDate.get(i).get(0) %>", <%= Integer.parseInt(sortedDate.get(i).get(1)) %>, "blue"])
                <% } %>
                var data = google.visualization.arrayToDataTable(list);

                var view = new google.visualization.DataView(data);
                view.setColumns([0, 1,
                    {
                        calc: "stringify",
                        sourceColumn: 1,
                        type: "string",
                        role: "annotation"
                    },
                    2]);

                var options = {
                    title: "Top 10 searched date APOD",
                    width: 600,
                    height: 400,
                    bar: {groupWidth: "95%"},
                    legend: {position: "none"},
                };
                var chart = new google.visualization.BarChart(document.getElementById("barchart_date"));
                chart.draw(view, options);
            }
        </script>
    </div>
<%--    Table for date analytics    --%>
    <div class="container-fluid">
        <table class="table table-striped">
            <thead>
            <tr>
                <th scope="col">Image Date</th>
                <th scope="col">Frequency</th>
            </tr>
            </thead>
            <tbody>
            <% for (int i = 0; i < Math.min(10, sortedDate.size()); i++) { %>
            <tr>
                <td><%= sortedDate.get(i).get(0) %>
                </td>
                <td><%= sortedDate.get(i).get(1)  %>
                </td>
            </tr>
            <% } %>
            </tbody>
        </table>
    </div>


    <h4>3. Top 10 Device</h4>
    <p class="info">Show all devices if these are less than 10 records</p>
<%--    Bar chart for device analytics    --%>
    <div class="container" id="barchart_device" >
        <script type="text/javascript">
            google.charts.load("current", {packages: ["corechart"]});
            google.charts.setOnLoadCallback(drawChart);

            function drawChart() {
                var list = [["Element", "Density", {role: "style"}]]
                <% for (int i = 0; i < Math.min(10, sortedDevice.size()); i++) { %>
                list.push(["<%= sortedDevice.get(i).get(0) %>", <%= Integer.parseInt(sortedDevice.get(i).get(1)) %>, "blue"])
                <% } %>
                var data = google.visualization.arrayToDataTable(list);

                var view = new google.visualization.DataView(data);
                view.setColumns([0, 1,
                    {
                        calc: "stringify",
                        sourceColumn: 1,
                        type: "string",
                        role: "annotation"
                    },
                    2]);

                var options = {
                    title: "Top 10 devices",
                    width: 600,
                    height: 400,
                    bar: {groupWidth: "95%"},
                    legend: {position: "none"},
                };
                var chart = new google.visualization.BarChart(document.getElementById("barchart_device"));
                chart.draw(view, options);
            }
        </script>
    </div>
<%--    Table for device analytics    --%>
    <div class="container-fluid">
        <table class="table table-striped">
            <thead>
            <tr>
                <th scope="col">Device</th>
                <th scope="col">Frequency</th>
            </tr>
            </thead>
            <tbody>
            <% for (int i = 0; i < Math.min(10, sortedDevice.size()); i++) { %>
            <tr>
                <td><%= sortedDevice.get(i).get(0) %>
                </td>
                <td><%= sortedDevice.get(i).get(1)  %>
                </td>
            </tr>
            <% } %>
            </tbody>
        </table>
    </div>
    <h4>4. Average latency is <%= mongoConn.averageLatency()%> ms</h4>
</div>


</body>
</html>
