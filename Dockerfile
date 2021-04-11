FROM tomcat:9.0
ADD target/cake-manager.war /usr/local/tomcat/webapps
CMD ["catalina.sh", "run"]
