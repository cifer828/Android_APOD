<%--
  Created by IntelliJ IDEA.
  User: qiuchenzhang
  Date: 4/3/20
  Time: 1:02 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css" integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
    <title>NASA APOF API Server</title>

  </head>
  <body>
  <h2 class="text-center">Task2 has both API and dashboard</h2>
  <div class="container">
    <div class="jumbotron">
      <h3>Call APOF API</h3>
      <p><em>http://host/api?type=[device type]&date=[YYYY-MM-DD]</em>></p>
      <p>Return APOF JSON including title, copyright, description and image url</p>
    </div>
  </div>

  <div class="container">
    <div class="jumbotron">
      <h3>Dashboard</h3>
      <p>Show log information and analytics</p>
      <p><a href="/dashboard">dashboard link</a></p>
    </div>
  </div>
  </body>
</html>
