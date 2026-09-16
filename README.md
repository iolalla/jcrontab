# Jcrontab

A modern, high-performance, lightweight cron scheduler library and runtime for Java 21+.

Originally created as a full-featured Java replacement for UNIX/POSIX `cron`, Jcrontab 2.0 has been modernized for **Java 21**, featuring **Virtual Threads (Project Loom)**, precision analytical scheduling with `java.time`, and zero external runtime dependencies beyond SLF4J.

---

## Key Features

- **Java 21 Virtual Threads**: Highly concurrent task execution using `Executors.newVirtualThreadPerTaskExecutor()`—no platform thread starvation.
- **Modern `java.time` Engine**: Precision next-execution calculations with timezone (`ZoneId`) and DST awareness; replaces legacy iterative calendar polling loops.
- **Rich Cron Syntax Support**:
  - Standard 5-field POSIX (`* * * * *`)
  - 6-field with second-level precision (`*/15 * * * * *`)
  - 7-field with year specifications (`0 0 12 1 1 ? 2030`)
  - Named macro shortcuts (`@hourly`, `@daily`, `@midnight`, `@weekly`, `@monthly`, `@yearly`, `@annually`)
  - Range syntax (`9-17`), steps (`*/5`), lists (`1,15,30`), and English month/day names (`JAN-DEC`, `MON-SUN`).
- **Flexible Scheduling API**:
  - Programmatic fluent API with lambda `Runnable` actions.
  - Classic reflection-based invocation (`com.example.MyTask#run`).
  - Native process execution via modern `ProcessBuilder`.
  - Crontab table file parser with comment and whitespace handling.
- **Lifecycle & Observability**: Structured `TaskListener` callbacks for scheduling, execution start, success, and error reporting.
- **Holidays & Business Days**: Calendar filtering to skip task execution on holidays or non-business days.

---

## Build & Test Commands

You can build and test the project using standard Maven. If working in an environment with the bundled `.m2/repository` cache, supply `-Dmaven.repo.local=.m2/repository`.

### 1. Clean & Build (Full Package with Tests)
```bash
mvn -Dmaven.repo.local=.m2/repository clean package
```

To run completely offline without querying remote repositories:
```bash
mvn -Dmaven.repo.local=.m2/repository -o clean package
```

### 2. Fast Build (Skip Tests)
To quickly package the JAR without executing tests:
```bash
mvn -Dmaven.repo.local=.m2/repository -o clean package -DskipTests
```

### 3. Run Unit Tests Only
Executes the JUnit 5 Jupiter test suite (40 tests):
```bash
mvn -Dmaven.repo.local=.m2/repository -o test
```

### 4. Build Output Artifact
The compiled JAR library will be placed in the `target/` directory:
```bash
target/jcrontab-2.0.0-SNAPSHOT.jar
```

---

## Execution

### Generating the Classpath
To automatically generate the exact runtime classpath required to execute Jcrontab with Maven:

```bash
# Generate the runtime classpath to target/runtime-classpath.txt
mvn -Dmaven.repo.local=.m2/repository -o dependency:build-classpath -DincludeScope=runtime -Dmdep.outputFile=target/runtime-classpath.txt

# Or print the classpath directly to your console:
mvn -Dmaven.repo.local=.m2/repository -o dependency:build-classpath -DincludeScope=runtime -q -Dmdep.outputFile=/dev/stdout
```

### CLI / Standalone Execution
When running the standalone scheduler daemon via `org.jcrontab.Jcrontab`, include the Jcrontab JAR, the SLF4J API and logging provider (e.g., `slf4j-simple`), plus any project classes/resources containing your scheduled tasks:

```bash
# 1. Define the classpath:
export CLASSPATH="target/jcrontab-2.0.0-SNAPSHOT.jar:.m2/repository/org/slf4j/slf4j-api/2.0.12/slf4j-api-2.0.12.jar:.m2/repository/org/slf4j/slf4j-simple/2.0.12/slf4j-simple-2.0.12.jar"

# 2. Run with default user ~/.jcrontab configuration:
java -cp "$CLASSPATH" org.jcrontab.Jcrontab

# 3. Run with a specific properties file (including custom task classes and config in src/test/resources):
java -cp "$CLASSPATH:src/test/resources:target/test-classes" org.jcrontab.Jcrontab src/test/resources/jcrontab.properties
```

### Programmatic Usage (Java 21)

#### 1. Modern Fluent Scheduler (`JCrontabScheduler`)
```java
import org.jcrontab.JCrontabScheduler;
import java.time.Duration;

// JCrontabScheduler implements AutoCloseable
try (JCrontabScheduler scheduler = new JCrontabScheduler()) {

    // Schedule a task using a lambda and cron expression
    scheduler.schedule("*/5 * * * *", () -> {
        System.out.println("Running on a Java 21 Virtual Thread!");
    });

    // Schedule second-precision tasks
    scheduler.schedule("*/10 * * * * *", () -> {
        System.out.println("Runs every 10 seconds");
    });

    // Add lifecycle observability listener
    scheduler.addListener(new TaskListener() {
        @Override
        public void onSuccess(CrontabEntry entry, Duration duration) {
            System.out.println("Task finished in " + duration.toMillis() + "ms");
        }
    });

    scheduler.start();

    // Keep running or wait as needed
    Thread.sleep(60_000);
}
```

#### 2. Load Tasks from Crontab File
```java
try (JCrontabScheduler scheduler = new JCrontabScheduler()) {
    scheduler.loadCrontabFile(Path.of("config/crontab"));
    scheduler.start();
}
```

---

## Logging Configuration

### Default Console Logging
Out of the box, Jcrontab outputs log messages directly to the console (`System.out` / `System.err`) if no external logging provider (or a NOP logger) is detected. No extra dependencies or configuration files are required.

To force console output regardless of configured backends:
```bash
java -Djcrontab.log.console=true -cp ... org.jcrontab.Jcrontab
```

### Log4j 2 / Log4j Configuration

To use Log4j for logging, specify the Log4j logger in your `jcrontab.properties`:

```properties
org.jcrontab.log.Logger=org.jcrontab.log.Log4JLogger
org.jcrontab.log.log4J.Properties=log4j2.properties
```

#### Example `log4j2.properties` (Log4j 2.x):
```properties
status = WARN
name = JcrontabLog4j2Config

# Console Appender
appender.console.type = Console
appender.console.name = ConsoleAppender
appender.console.target = SYSTEM_OUT
appender.console.layout.type = PatternLayout
appender.console.layout.pattern = %d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n

# Root Logger
rootLogger.level = INFO
rootLogger.appenderRefs = stdout
rootLogger.appenderRef.stdout.ref = ConsoleAppender

# Jcrontab Loggers
logger.jcrontab.name = jcrontab
logger.jcrontab.level = INFO

logger.cron4web.name = Cron4Web
logger.cron4web.level = INFO
```

#### Example `log4j.properties` (Classic Log4j 1.x):
```properties
log4j.rootLogger=INFO, stdout

log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.Target=System.out
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss} [%t] %-5p %c - %m%n

log4j.logger.jcrontab=INFO
log4j.logger.Cron4Web=INFO
```

---

## Crontab File Format

```text
# Tasks planification configuration file
# Minute   Hour   DayOfMonth   Month   DayOfWeek   Class[#method]   [Arguments...]

# Run every minute
* * * * * com.example.MyService

# Run at 08:30 on weekdays (Mon-Fri)
30 8 * * 1-5 com.example.ReportsService#generate daily morning

# Run every 15 minutes during business hours
*/15 9-17 * * MON-FRI com.example.SyncTask

# Run native command
0 0 * * * org.jcrontab.NativeExec /usr/local/bin/backup.sh
```

---

## Web Management & CRUD Interfaces

Jcrontab provides two options for managing scheduled tasks via web interfaces:
1. **Built-in Web Console & REST API** (Recommended): Zero external dependencies, embedded in Java 21, lightweight, and modern.
2. **Tomcat / Java EE WAR Deployment**: Traditional `jcrontab.war` for deployment on Tomcat, Jetty, or any Servlet 2.4+ container.

---

### Option 1: Built-in Web Console & REST API

The built-in console is powered by standard Java SE `com.sun.net.httpserver.HttpServer` and Java 21 Virtual Threads (`Executors.newVirtualThreadPerTaskExecutor()`). It requires **zero extra dependencies** or application servers.

#### Features
- **Responsive Single-Page Dashboard**: Live task monitor, statistics cards, and instant filtering.
- **Full CRUD Support**: Add new tasks with interactive cron preset selectors, edit existing tasks, and delete obsolete entries.
- **Manual Task Execution ("Run Now")**: Trigger any scheduled task immediately on demand on an isolated virtual thread.
- **RESTful JSON API**: Programmatically inspect, create, trigger, and delete tasks from CI/CD, curl, or external microservices.

#### Enabling the Web Console

##### 1. Via CLI Flag
Pass `--web [port]` (defaults to `8080` if port is omitted):
```bash
# Start standalone daemon with Web Console on port 8080:
java -cp "$CLASSPATH" org.jcrontab.Jcrontab --web 8080

# Start with a specific properties file and Web Console on port 9090:
java -cp "$CLASSPATH:src/test/resources:target/test-classes" org.jcrontab.Jcrontab src/test/resources/jcrontab.properties --web 9090
```

##### 2. Via `jcrontab.properties` Configuration
Add the following properties to your configuration file:
```properties
org.jcrontab.web.enable = true
org.jcrontab.web.port = 8080
```

##### 3. Via Programmatic Builder (`JCrontabScheduler`)
```java
JCrontabScheduler scheduler = JCrontabScheduler.builder()
        .enableWeb(8080)
        .webAuth("admin", "secret123") // Enable password challenge
        .build();

scheduler.start();
// Web dashboard accessible at http://localhost:8080/
```

#### Security & Password Challenge

To protect the management console and REST endpoints from unauthorized access, Jcrontab includes an embedded **HTTP Basic Auth challenge** layer. When enabled:
- Browsers natively display a credential challenge prompt before the UI loads.
- The UI displays an authenticated badge (e.g. `🔒 admin`).
- All REST API endpoints require valid credentials (`HTTP 401 Unauthorized` returned otherwise).

##### Enabling Authentication via CLI
```bash
# Challenge with username 'admin' and custom password:
java -cp "$CLASSPATH" org.jcrontab.Jcrontab --web 8080 --password mysecret

# Challenge with custom username and password:
java -cp "$CLASSPATH" org.jcrontab.Jcrontab --web 8080 --auth admin:mysecret
```

##### Enabling Authentication via `jcrontab.properties`
```properties
org.jcrontab.web.enable = true
org.jcrontab.web.port = 8080
org.jcrontab.web.auth.user = admin
org.jcrontab.web.auth.password = mysecret
```

#### Accessing the Web Dashboard
Open your browser to:
```text
http://localhost:8080/
```
Enter your configured credentials when prompted by the browser.

#### REST API Endpoints & `curl` Examples
When security is enabled, supply `-u username:password` (or `--user`) with your `curl` requests:

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/status` | System health, uptime, and active task count |
| `GET` | `/api/tasks` | List all scheduled tasks in JSON format |
| `POST` | `/api/tasks` | Create or update a scheduled task |
| `DELETE` | `/api/tasks?id={id}` | Delete a task by its ID |
| `POST` | `/api/tasks/run?id={id}`| Trigger immediate asynchronous task execution |

##### 1. Check Server Status
```bash
curl -s http://localhost:8080/api/status
```
Response:
```json
{"version":"2.0.0","status":"UP","uptimeSeconds":42,"taskCount":3,"port":8080}
```

##### 2. List Tasks
```bash
curl -s http://localhost:8080/api/tasks
```

##### 3. Create a Scheduled Task
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "minutes": "*/5",
    "hours": "*",
    "daysOfMonth": "*",
    "months": "*",
    "daysOfWeek": "*",
    "className": "com.example.BackupTask",
    "methodName": "main",
    "extraInfo": "daily backup",
    "businessDays": false
  }'
```

##### 4. Trigger Task Immediately ("Run Now")
```bash
curl -X POST "http://localhost:8080/api/tasks/run?id=1"
```
Response:
```json
{"status":"ok","message":"Task triggered asynchronously","taskId":101}
```

##### 5. Delete a Task
```bash
curl -X DELETE "http://localhost:8080/api/tasks?id=1"
```

---

### Option 2: Tomcat / Java EE WAR Deployment (`jcrontab.war`)

For enterprise environments deploying Jcrontab into servlet containers like Apache Tomcat or Jetty, Jcrontab includes servlet controllers, XML/XSLT management views, and a WAR packaging script.

#### Building `jcrontab.war`
Run the provided automated build script:
```bash
./build-war.sh
```
This compiles the classes and generates `target/jcrontab.war`.

#### Deploying to Apache Tomcat
1. Copy `target/jcrontab.war` into Tomcat's `webapps/` directory:
   ```bash
   cp target/jcrontab.war $CATALINA_HOME/webapps/
   ```
2. Start Tomcat:
   ```bash
   $CATALINA_HOME/bin/startup.sh
   ```
3. Once deployed, the following endpoints are available:
   - **Interactive Web Manager**: `http://localhost:8080/jcrontab/CrontabViewServlet`
   - **XML Task Management Endpoint**: `http://localhost:8080/jcrontab/CrontabServletXML`
   - **Background Scheduler Lifecycle**: Managed automatically by `loadCrontabServlet` defined in `WEB-INF/web.xml`.

---

## Service Resilience & Fault-Tolerance

Jcrontab 2.0 is designed as an always-on system service:
- **Resilient Parsing**: Syntax errors, unparseable lines, or unrecognized characters in crontab files are logged as errors with file name and line number, but **never crash** or terminate the application. All valid tasks in the crontab continue executing.
- **Isolated Task Failures**: Runtime exceptions or missing task classes during task execution are caught and reported via SLF4J / `TaskListener`, leaving the scheduler daemon and other jobs unaffected.
- **Robust Daemon Lifecyle**: When started via `Jcrontab.main`, the process remains persistent until an explicit `SIGINT` / `SIGTERM` or `Jcrontab.stop()` call, triggering clean shutdown hooks.

---

## License

Jcrontab is distributed under the GNU Lesser General Public License (LGPL). See `LICENSE` for details.
