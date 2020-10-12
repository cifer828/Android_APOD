## Astronomy Picture of the Day ##

![modal_img](https://qiuchen.netlify.app/assets/img/img_project_3_0.png) ![modal_img](https://qiuchen.netlify.app/assets/img/img_project_3_1.png)

This Android App asks user to choose a specific day and then displays NASA's Astronomy Picture of that day as well as the background story. Front-end and back-end are seperated in this application and they are communicated with custimized RESTful APIs. Back-end is responsible for fetching required data and storing logs in MongDB while front-end utilizes async task to load images for better user experiences. Back-end is developed based on TomEE and deployed on Heroku using Docker.

[Dashboard Link](https://floating-mesa-72146.herokuapp.com/) (Need some time for heroku to activate resources)

Check [NASA API](https://api.nasa.gov/) to learn about the API used in this project.