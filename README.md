
			Jcrontab README File

Jcrontab is a scheduler written in JAVA. The project objective 
is to provide a fully functional system to substitute Crontab in JAVA projects. 

Jcrontab :

Provides a system to run Classes/Threads/CronTask/Tasks/Beans/EJB/main/native/... 
at a given time.
Provides a  parser of Crontab-like files fully compatible with 
UNIX-POSIX crontab files.
It provides an example class to launch native programs.
Loads the crontab info from a file, XML or a Database
It refreshes the Crontab info at a given frequency. 
Consumes a few kb of memory and doesn't waste CPU. 
Can define Holidays, to avoid activity in those days.


Is an LGPL Project you can use and distribute it Freely under the LGPL 
License http://www.gnu.org/copyleft/lesser.html
Is a Project born in http://www.sourceforge.net, and you can find all the
information you need about Jcrontab in http://Jcrontab.sourceforge.net

General Notes:

If you are interested in this project and want to help, you are welcome.
The ways you can help are writting code, designing a better crontab,
designing a webPage, testing the project and of course using Jcrontab.


COMPILATION:
    
You can compile it like any other maven project:


$mvn clean package

EXECUTION
    
    You can use Jcrontab with your projects just adding jar/Jcrontab.jar
in your classpath and importing the right classes (See org.jcrontab.jcrontab 
for a valid example) or calling the main()  method in org.jcronta.jcrontab
        
    Or you can just:
    java -jar Jcrontab.jar
    Or:
    java -jar Jcrontab.jar -f /home/jondoe/src/Jcrontab.properties 3

     If you want to launch your classes at a given time don't forget to change
crontab to addapt to your needs. More info at org.jcrontab.data.crontab

     If you want to launch Jcrontab from you application server you can do it 
initializing Jcrontab from servlets init method or in a Load-on-startup servlet. 
Take a look at org.jcrontab.web or war/Jcrontab.war (See 
org.jcrontab.web.loadCrontabServlet.java). This have been tested in: Tomcat
, resin, Jboss and Jetty. If you test it somewhere else please send and email, 
with the server and the changes you had to do to get this working.
