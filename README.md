# Bizis-Zgz
WebApp to get information about Bike Stations and the weather in the city of Zaragoza(Spain).
The information about Bike Stations is got interacting with a free API of the City Hall of Zaragoza and the weather is got interacting with the website http://www.aemet.es/, Spanish website with information about the weather. Both web services are implemented in Java.

### Execution environment
1. Java JDK 8
2. Tomcat 4.1.40
3. Axis webapp running in Tomcat
4. XAMpp Apache

### Building and deploying
1. Copy and paste the .war files in the folder tomcat/webapps/  
2. Copy and paste .class files (folder axis/compilados) into the folder webapps/axis/WEB-INF/classes 
3. Copy the deploy.wsdd (folder axis/) into the folder tomcat/webapps/axis
4. Start tomcat
5. Register SOAP Axis services, executing in webapps/axis: java -cp WEB-INF/lib/* org.apache.axis.client.AdminClient deploy.wsdd
6. Copy the files in the folder php/ into xampp/htdocs
7. Start Apache with Xampp
