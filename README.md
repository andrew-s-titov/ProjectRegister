**GUIDE**

- Make sure you have JDK 11 or higher installed and JAVA_HOME variable configured
- Download and install MySQL 8: https://dev.mysql.com/downloads/mysql/
- Open this project with IntelliJ IDEA or any other Java IDE, select 
  *Build -> Build Artifact -> war* 
  (ProjectRegister-1.war will be created in *$MODULE_DIR$:target* directory).
  You can also use Apache Maven desktop or any other tool to build a *.war* project.  
- Download Apache Tomcat 10: https://tomcat.apache.org/download-10.cgi
- Extract Apache Tomcat from the archive to any directory
- **Deploy:** copy *ProjectRegister-1.war* into the *$CATALINA_HOME\webapps* directory of a Tomcat instance
- **Start:** run startup.bat (Windows) or startup.sh (Linux) in *$CATALINA_HOME\bin* directory, open browser and go to: http://localhost:8080/ProjectRegister-1/
