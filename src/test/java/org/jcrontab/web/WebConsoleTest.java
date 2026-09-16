package org.jcrontab.web;

import org.jcrontab.Crontab;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class WebConsoleTest {

    private static JcrontabWebServer webServer;
    private static int port;
    private static HttpClient client;
    private static Path tempProperties;
    private static Path tempCrontab;

    @BeforeAll
    public static void setUp() throws Exception {
        tempCrontab = Files.createTempFile("web-crontab", ".txt");
        Files.writeString(tempCrontab, "* * * * * org.jcrontab.tests.TaskTest1\n");

        tempProperties = Files.createTempFile("web-jcrontab", ".properties");
        Files.writeString(tempProperties,
                "org.jcrontab.data.datasource = org.jcrontab.data.FileSource\n" +
                "org.jcrontab.data.file = " + tempCrontab.toAbsolutePath() + "\n");

        Crontab.getInstance().init(tempProperties.toAbsolutePath().toString());

        webServer = new JcrontabWebServer(0);
        webServer.start();
        port = webServer.getPort();
        assertTrue(port > 0, "Server should bind to ephemeral port");
        assertTrue(webServer.isRunning(), "Server should report running");

        client = HttpClient.newHttpClient();
    }

    @AfterAll
    public static void tearDown() throws Exception {
        if (webServer != null) {
            webServer.stop();
            assertFalse(webServer.isRunning(), "Server should report stopped");
        }
        Crontab.getInstance().uninit(100);
        try { Files.deleteIfExists(tempProperties); } catch (Exception ignored) {}
        try { Files.deleteIfExists(tempCrontab); } catch (Exception ignored) {}
    }

    @Test
    public void testGetDashboardHtml() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/"))
                .GET()
                .build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, res.statusCode());
        assertTrue(res.headers().firstValue("Content-Type").orElse("").contains("text/html"));
        assertTrue(res.body().contains("Jcrontab Management Console"));
        assertTrue(res.body().contains("tasks-table"));
    }

    @Test
    public void testGetStatusJson() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/status"))
                .GET()
                .build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, res.statusCode());
        assertTrue(res.headers().firstValue("Content-Type").orElse("").contains("application/json"));
        assertTrue(res.body().contains("\"status\":\"UP\""));
        assertTrue(res.body().contains("\"version\":\"2.0.0\""));
    }

    @Test
    public void testGetTasksJson() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/tasks"))
                .GET()
                .build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, res.statusCode());
        assertTrue(res.headers().firstValue("Content-Type").orElse("").contains("application/json"));
        assertTrue(res.body().startsWith("[") && res.body().endsWith("]"));
    }

    @Test
    public void testTaskLifecycleViaApi() throws Exception {
        // 1. Create a task via POST JSON
        String newTaskJson = """
            {
                "className": "org.jcrontab.tests.TaskTest2",
                "methodName": "main",
                "minutes": "*/5",
                "hours": "*",
                "daysOfMonth": "*",
                "months": "*",
                "daysOfWeek": "*",
                "extraInfo": "paramA paramB",
                "businessDays": false
            }
        """;

        HttpRequest postReq = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(newTaskJson))
                .build();
        HttpResponse<String> postRes = client.send(postReq, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, postRes.statusCode());
        assertTrue(postRes.body().contains("\"status\":\"ok\""));

        // 2. Fetch tasks and verify the new task is present
        HttpRequest listReq = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/tasks"))
                .GET()
                .build();
        HttpResponse<String> listRes = client.send(listReq, HttpResponse.BodyHandlers.ofString());

        assertTrue(listRes.body().contains("org.jcrontab.tests.TaskTest2"));
        assertTrue(listRes.body().contains("*/5"));

        // Extract ID
        int idIdx = listRes.body().indexOf("\"id\":");
        assertTrue(idIdx >= 0);
        int commaIdx = listRes.body().indexOf(",", idIdx);
        String idStr = listRes.body().substring(idIdx + 5, commaIdx).trim();

        // 3. Trigger manual execution via POST /api/tasks/run
        HttpRequest runReq = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/tasks/run?id=" + idStr))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> runRes = client.send(runReq, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, runRes.statusCode());
        assertTrue(runRes.body().contains("\"status\":\"ok\""));

        // 4. Delete the task via DELETE /api/tasks?id=X
        HttpRequest deleteReq = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/tasks?id=" + idStr))
                .DELETE()
                .build();
        HttpResponse<String> delRes = client.send(deleteReq, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, delRes.statusCode());
        assertTrue(delRes.body().contains("\"status\":\"ok\""));
    }

    @Test
    public void testAuthenticatedWebServerChallengeAndAccess() throws Exception {
        JcrontabWebServer authServer = new JcrontabWebServer(0, "adminuser", "secretPass");
        authServer.start();
        int authPort = authServer.getPort();
        assertTrue(authServer.isAuthEnabled());
        assertEquals("adminuser", authServer.getAuthUser());

        try {
            // 1. Unauthenticated request to / -> expect 401 Unauthorized
            HttpRequest unauthReq = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + authPort + "/"))
                    .GET()
                    .build();
            HttpResponse<String> unauthRes = client.send(unauthReq, HttpResponse.BodyHandlers.ofString());
            assertEquals(401, unauthRes.statusCode());
            assertTrue(unauthRes.headers().firstValue("WWW-Authenticate").orElse("").contains("Basic"));

            // 2. Request with invalid credentials -> expect 401 Unauthorized
            String badAuthHeader = "Basic " + java.util.Base64.getEncoder().encodeToString("adminuser:wrong".getBytes(java.nio.charset.StandardCharsets.UTF_8));
            HttpRequest badReq = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + authPort + "/"))
                    .header("Authorization", badAuthHeader)
                    .GET()
                    .build();
            HttpResponse<String> badRes = client.send(badReq, HttpResponse.BodyHandlers.ofString());
            assertEquals(401, badRes.statusCode());

            // 3. Request with valid credentials -> expect 200 OK and auth badge
            String validAuthHeader = "Basic " + java.util.Base64.getEncoder().encodeToString("adminuser:secretPass".getBytes(java.nio.charset.StandardCharsets.UTF_8));
            HttpRequest okReq = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + authPort + "/"))
                    .header("Authorization", validAuthHeader)
                    .GET()
                    .build();
            HttpResponse<String> okRes = client.send(okReq, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, okRes.statusCode());
            assertTrue(okRes.body().contains("Jcrontab Management Console"));
            assertTrue(okRes.body().contains("adminuser"));

            // 4. API endpoint with valid credentials -> expect 200 OK
            HttpRequest okApiReq = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + authPort + "/api/status"))
                    .header("Authorization", validAuthHeader)
                    .GET()
                    .build();
            HttpResponse<String> okApiRes = client.send(okApiReq, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, okApiRes.statusCode());
            assertTrue(okApiRes.body().contains("\"status\":\"UP\""));

            // 5. API endpoint unauthenticated -> expect 401 Unauthorized
            HttpRequest unauthApiReq = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + authPort + "/api/tasks"))
                    .GET()
                    .build();
            HttpResponse<String> unauthApiRes = client.send(unauthApiReq, HttpResponse.BodyHandlers.ofString());
            assertEquals(401, unauthApiRes.statusCode());
        } finally {
            authServer.stop();
        }
    }

    @Test
    public void testSchedulerBuilderWebAuth() throws Exception {
        org.jcrontab.JCrontabScheduler scheduler = org.jcrontab.JCrontabScheduler.builder()
                .enableWeb(0)
                .webAuth("builderUser", "builderPass")
                .build();
        try {
            scheduler.start();
            JcrontabWebServer ws = scheduler.getWebServer();
            assertNotNull(ws);
            assertTrue(ws.isAuthEnabled());
            assertEquals("builderUser", ws.getAuthUser());

            // Unauthenticated request should get 401
            HttpRequest unauthReq = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + ws.getPort() + "/api/status"))
                    .GET()
                    .build();
            HttpResponse<String> unauthRes = client.send(unauthReq, HttpResponse.BodyHandlers.ofString());
            assertEquals(401, unauthRes.statusCode());

            // Authenticated request should get 200
            String authHeader = "Basic " + java.util.Base64.getEncoder().encodeToString("builderUser:builderPass".getBytes(java.nio.charset.StandardCharsets.UTF_8));
            HttpRequest okReq = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + ws.getPort() + "/api/status"))
                    .header("Authorization", authHeader)
                    .GET()
                    .build();
            HttpResponse<String> okRes = client.send(okReq, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, okRes.statusCode());
        } finally {
            scheduler.stop();
        }
    }

    @Test
    public void testSchedulerWebIntegration() throws Exception {
        java.util.concurrent.atomic.AtomicBoolean taskRan = new java.util.concurrent.atomic.AtomicBoolean(false);
        org.jcrontab.JCrontabScheduler scheduler = org.jcrontab.JCrontabScheduler.builder()
                .enableWeb(0)
                .build();
        try {
            var handle = scheduler.schedule("0 0 * * *", () -> taskRan.set(true));
            scheduler.start();
            int wsPort = scheduler.getWebServer().getPort();

            // 1. Check /api/status reports taskCount = 1
            HttpRequest statusReq = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + wsPort + "/api/status"))
                    .GET()
                    .build();
            HttpResponse<String> statusRes = client.send(statusReq, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, statusRes.statusCode());
            assertTrue(statusRes.body().contains("\"taskCount\":1"));

            // 2. Trigger run via POST /api/tasks/run
            HttpRequest runReq = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + wsPort + "/api/tasks/run?id=" + handle.getId()))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> runRes = client.send(runReq, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, runRes.statusCode());

            // Give virtual thread a brief moment to complete
            Thread.sleep(150);
            assertTrue(taskRan.get(), "Task scheduled on JCrontabScheduler should run when triggered from Web Console");

            // 3. Delete task via DELETE /api/tasks
            HttpRequest delReq = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + wsPort + "/api/tasks?id=" + handle.getId()))
                    .DELETE()
                    .build();
            HttpResponse<String> delRes = client.send(delReq, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, delRes.statusCode());
            assertEquals(0, scheduler.getTasks().size(), "Task should be removed from JCrontabScheduler");
        } finally {
            scheduler.close();
        }
    }
}
