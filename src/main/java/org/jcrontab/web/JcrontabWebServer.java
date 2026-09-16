/**
 *  This file is part of the jcrontab package
 *  Copyright (C) 2001-2026 Israel Olalla
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free
 *  Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *  MA 02111-1307, USA
 *
 *  For questions, suggestions:
 *
 *  iolalla@gmail.com
 *
 */
package org.jcrontab.web;

import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.jcrontab.Crontab;
import org.jcrontab.CrontabBean;
import org.jcrontab.data.CrontabEntryBean;
import org.jcrontab.data.CrontabEntryDAO;
import org.jcrontab.data.CrontabParser;
import org.jcrontab.data.DataNotFoundException;
import org.jcrontab.log.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Modern built-in embedded HTTP Web Console and REST API for Jcrontab.
 * Powered by Java 21 Virtual Threads and standard jdk.httpserver (zero external dependencies).
 */
public class JcrontabWebServer {

    private final int configuredPort;
    private final String authUser;
    private final String authPassword;
    private final boolean authEnabled;
    private HttpServer server;
    private int boundPort;
    private final long startTime = System.currentTimeMillis();

    public JcrontabWebServer(int port) {
        this(port, null, null);
    }

    public JcrontabWebServer(int port, String authUser, String authPassword) {
        this.configuredPort = port;
        this.authUser = (authUser != null && !authUser.trim().isEmpty()) ? authUser.trim() : "admin";
        this.authPassword = authPassword;
        this.authEnabled = (authPassword != null && !authPassword.isEmpty());
    }

    public synchronized void start() throws IOException {
        if (server != null) {
            return;
        }
        server = HttpServer.create(new InetSocketAddress(configuredPort), 0);
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());

        HttpContext rootCtx = server.createContext("/", new RootHandler());
        HttpContext tasksCtx = server.createContext("/api/tasks", new TasksApiHandler());
        HttpContext runCtx = server.createContext("/api/tasks/run", new TaskRunApiHandler());
        HttpContext statusCtx = server.createContext("/api/status", new StatusApiHandler());

        if (authEnabled) {
            BasicAuthenticator authenticator = new BasicAuthenticator("Jcrontab Console") {
                @Override
                public boolean checkCredentials(String username, String password) {
                    return authUser.equals(username) && authPassword.equals(password);
                }
            };
            rootCtx.setAuthenticator(authenticator);
            tasksCtx.setAuthenticator(authenticator);
            runCtx.setAuthenticator(authenticator);
            statusCtx.setAuthenticator(authenticator);
            Log.info("Web Console security enabled (HTTP Basic Auth challenge for user '" + authUser + "').");
        }

        server.start();
        boundPort = server.getAddress().getPort();
        Log.info("Jcrontab Web Console running at http://localhost:" + boundPort + "/");
    }

    public synchronized void stop() {
        if (server != null) {
            server.stop(0);
            server = null;
            Log.info("Jcrontab Web Console stopped.");
        }
    }

    public int getPort() {
        return boundPort > 0 ? boundPort : configuredPort;
    }

    public boolean isRunning() {
        return server != null;
    }

    public boolean isAuthEnabled() {
        return authEnabled;
    }

    public String getAuthUser() {
        return authUser;
    }

    private class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (!"/".equals(path) && !"/index.html".equals(path)) {
                sendResponse(exchange, 404, "text/plain", "Not Found");
                return;
            }
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "text/plain", "Method Not Allowed");
                return;
            }
            String badge = authEnabled
                    ? "<span class=\"badge\" style=\"background:rgba(16,185,129,0.15); border:1px solid #10b981; color:#34d399;\">&#128274; " + escapeHtml(authUser) + "</span>"
                    : "";
            String rendered = DASHBOARD_HTML.replace("<!--AUTH_BADGE-->", badge);
            byte[] htmlBytes = rendered.getBytes(StandardCharsets.UTF_8);
            sendResponse(exchange, 200, "text/html; charset=UTF-8", htmlBytes);
        }
    }

    private class StatusApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "text/plain", "Method Not Allowed");
                return;
            }
            int taskCount = 0;
            try {
                CrontabEntryBean[] list = CrontabEntryDAO.getInstance().findAll();
                if (list != null) taskCount = list.length;
            } catch (Exception ignored) {}

            long uptimeSec = (System.currentTimeMillis() - startTime) / 1000;
            String json = String.format(Locale.ROOT,
                    "{\"version\":\"2.0.0\",\"status\":\"UP\",\"uptimeSeconds\":%d,\"taskCount\":%d,\"port\":%d}",
                    uptimeSec, taskCount, getPort());
            sendResponse(exchange, 200, "application/json", json);
        }
    }

    private class TasksApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod().toUpperCase(Locale.ROOT);
            try {
                switch (method) {
                    case "GET":
                        handleGet(exchange);
                        break;
                    case "POST":
                        handlePost(exchange);
                        break;
                    case "DELETE":
                        handleDelete(exchange);
                        break;
                    default:
                        sendResponse(exchange, 405, "application/json", "{\"error\":\"Method Not Allowed\"}");
                }
            } catch (Exception e) {
                Log.error("Error handling /api/tasks request: " + e.getMessage(), e);
                sendResponse(exchange, 500, "application/json",
                        "{\"error\":\"" + escapeJson(e.getMessage() != null ? e.getMessage() : e.toString()) + "\"}");
            }
        }

        private void handleGet(HttpExchange exchange) throws Exception {
            CrontabEntryBean[] list;
            try {
                list = CrontabEntryDAO.getInstance().findAll();
            } catch (DataNotFoundException dnfe) {
                list = new CrontabEntryBean[0];
            }
            if (list == null) list = new CrontabEntryBean[0];

            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.length; i++) {
                if (i > 0) sb.append(",");
                sb.append(beanToJson(list[i]));
            }
            sb.append("]");
            sendResponse(exchange, 200, "application/json", sb.toString());
        }

        private void handlePost(HttpExchange exchange) throws Exception {
            String body = readBody(exchange);
            Map<String, String> params = parsePayload(body, exchange.getRequestHeaders().getFirst("Content-Type"));

            String className = params.get("className");
            if (className == null || className.trim().isEmpty()) {
                sendResponse(exchange, 400, "application/json", "{\"error\":\"className is required\"}");
                return;
            }

            String minutes = params.getOrDefault("minutes", params.getOrDefault("minute", "*")).trim();
            String hours = params.getOrDefault("hours", params.getOrDefault("hour", "*")).trim();
            String dom = params.getOrDefault("daysOfMonth", params.getOrDefault("dom", "*")).trim();
            String month = params.getOrDefault("months", params.getOrDefault("month", "*")).trim();
            String dow = params.getOrDefault("daysOfWeek", params.getOrDefault("dow", "*")).trim();
            String year = params.getOrDefault("years", params.getOrDefault("year", "*")).trim();
            String seconds = params.getOrDefault("seconds", params.getOrDefault("second", "0")).trim();
            String methodName = params.getOrDefault("methodName", "").trim();
            String extraInfoStr = params.getOrDefault("extraInfo", "").trim();
            boolean businessDays = "true".equalsIgnoreCase(params.get("businessDays"));

            String idStr = params.get("id");
            Integer existingId = (idStr != null && !idStr.isEmpty()) ? Integer.parseInt(idStr) : null;

            // Build line for CrontabParser
            String target = methodName.isEmpty() ? className : className + "#" + methodName;
            String crontabLine = minutes + " " + hours + " " + dom + " " + month + " " + dow + " " + target;
            if (!extraInfoStr.isEmpty()) {
                crontabLine += " " + extraInfoStr;
            }

            CrontabParser cp = new CrontabParser();
            CrontabEntryBean bean = cp.marshall(crontabLine);

            boolean[] bYears = new boolean[100];
            cp.parseToken(year, bYears, false);
            bean.setBYears(bYears);
            bean.setYears(year);

            boolean[] bSeconds = new boolean[60];
            cp.parseToken(seconds, bSeconds, false);
            bean.setBSeconds(bSeconds);
            bean.setSeconds(seconds);
            bean.setBusinessDays(businessDays);

            CrontabEntryDAO dao = CrontabEntryDAO.getInstance();

            if (existingId != null) {
                // If editing existing, remove old first if ID exists
                try {
                    CrontabEntryBean old = dao.getById(existingId);
                    if (old != null) {
                        dao.remove(new CrontabEntryBean[]{old});
                    }
                } catch (Exception ignored) {}
                bean.setId(existingId);
            }

            dao.store(bean);
            sendResponse(exchange, 200, "application/json",
                    "{\"status\":\"ok\",\"message\":\"Task saved successfully\",\"id\":" + bean.getId() + "}");
        }

        private void handleDelete(HttpExchange exchange) throws Exception {
            String query = exchange.getRequestURI().getQuery();
            String body = readBody(exchange);
            Map<String, String> params = new HashMap<>();
            if (query != null) params.putAll(parseQuery(query));
            if (!body.isEmpty()) params.putAll(parsePayload(body, exchange.getRequestHeaders().getFirst("Content-Type")));

            String idStr = params.get("id");
            if (idStr == null || idStr.isEmpty()) {
                sendResponse(exchange, 400, "application/json", "{\"error\":\"Task 'id' is required for deletion\"}");
                return;
            }

            int id = Integer.parseInt(idStr);
            CrontabEntryDAO dao = CrontabEntryDAO.getInstance();
            CrontabEntryBean bean = dao.getById(id);
            if (bean == null) {
                sendResponse(exchange, 404, "application/json", "{\"error\":\"Task ID not found: " + id + "\"}");
                return;
            }

            dao.remove(new CrontabEntryBean[]{bean});
            sendResponse(exchange, 200, "application/json", "{\"status\":\"ok\",\"message\":\"Task deleted successfully\"}");
        }
    }

    private class TaskRunApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "application/json", "{\"error\":\"Method Not Allowed\"}");
                return;
            }
            try {
                String query = exchange.getRequestURI().getQuery();
                String body = readBody(exchange);
                Map<String, String> params = new HashMap<>();
                if (query != null) params.putAll(parseQuery(query));
                if (!body.isEmpty()) params.putAll(parsePayload(body, exchange.getRequestHeaders().getFirst("Content-Type")));

                String idStr = params.get("id");
                if (idStr == null || idStr.isEmpty()) {
                    sendResponse(exchange, 400, "application/json", "{\"error\":\"Task 'id' is required to run\"}");
                    return;
                }

                int id = Integer.parseInt(idStr);
                CrontabEntryDAO dao = CrontabEntryDAO.getInstance();
                CrontabEntryBean bean = dao.getById(id);
                if (bean == null) {
                    sendResponse(exchange, 404, "application/json", "{\"error\":\"Task ID not found: " + id + "\"}");
                    return;
                }

                CrontabBean cb = new CrontabBean();
                cb.setClassName(bean.getClassName());
                cb.setMethodName(bean.getMethodName() != null ? bean.getMethodName() : "");
                cb.setExtraInfo(bean.getExtraInfo());

                int taskId = Crontab.getInstance().newTask(cb);
                sendResponse(exchange, 200, "application/json",
                        "{\"status\":\"ok\",\"message\":\"Task triggered asynchronously\",\"taskId\":" + taskId + "}");
            } catch (Exception e) {
                Log.error("Error executing task manually: " + e.getMessage(), e);
                sendResponse(exchange, 500, "application/json",
                        "{\"error\":\"" + escapeJson(e.getMessage() != null ? e.getMessage() : e.toString()) + "\"}");
            }
        }
    }

    private static String beanToJson(CrontabEntryBean b) {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"id\":").append(b.getId()).append(",");
        sb.append("\"minutes\":\"").append(escapeJson(b.getMinutes())).append("\",");
        sb.append("\"hours\":\"").append(escapeJson(b.getHours())).append("\",");
        sb.append("\"daysOfMonth\":\"").append(escapeJson(b.getDaysOfMonth())).append("\",");
        sb.append("\"months\":\"").append(escapeJson(b.getMonths())).append("\",");
        sb.append("\"daysOfWeek\":\"").append(escapeJson(b.getDaysOfWeek())).append("\",");
        sb.append("\"years\":\"").append(escapeJson(b.getYears())).append("\",");
        sb.append("\"seconds\":\"").append(escapeJson(b.getSeconds())).append("\",");
        sb.append("\"businessDays\":").append(b.getBusinessDays()).append(",");
        sb.append("\"className\":\"").append(escapeJson(b.getClassName())).append("\",");
        sb.append("\"methodName\":\"").append(escapeJson(b.getMethodName())).append("\",");

        String expression = String.format("%s %s %s %s %s",
                b.getMinutes(), b.getHours(), b.getDaysOfMonth(), b.getMonths(), b.getDaysOfWeek());
        sb.append("\"expression\":\"").append(escapeJson(expression)).append("\",");

        sb.append("\"extraInfo\":[");
        String[] extra = b.getExtraInfo();
        if (extra != null) {
            for (int j = 0; j < extra.length; j++) {
                if (j > 0) sb.append(",");
                sb.append("\"").append(escapeJson(extra[j])).append("\"");
            }
        }
        sb.append("]");
        sb.append("}");
        return sb.toString();
    }

    private static String readBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int r;
        while ((r = is.read(buf)) != -1) {
            baos.write(buf, 0, r);
        }
        return baos.toString(StandardCharsets.UTF_8);
    }

    private static Map<String, String> parsePayload(String body, String contentType) {
        Map<String, String> map = new HashMap<>();
        if (body == null || body.trim().isEmpty()) {
            return map;
        }
        if (contentType != null && contentType.contains("application/json")) {
            // Lightweight JSON key-value extraction
            Pattern p = Pattern.compile("\"([^\"]+)\"\\s*:\\s*(\"[^\"]*\"|true|false|null|[0-9.]+|\\[[^\\]]*\\])");
            Matcher m = p.matcher(body);
            while (m.find()) {
                String key = m.group(1);
                String val = m.group(2).trim();
                if (val.startsWith("\"") && val.endsWith("\"")) {
                    val = val.substring(1, val.length() - 1);
                }
                map.put(key, val);
            }
        } else {
            // Form urlencoded
            map.putAll(parseQuery(body));
        }
        return map;
    }

    private static Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isEmpty()) return map;
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0) {
                String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                String val = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
                map.put(key, val);
            } else if (!pair.isEmpty()) {
                map.put(URLDecoder.decode(pair, StandardCharsets.UTF_8), "");
            }
        }
        return map;
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String contentType, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        sendResponse(exchange, statusCode, contentType, bytes);
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String contentType, byte[] bytes) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.getResponseHeaders().set("Cache-Control", "no-cache, no-store, must-revalidate");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (ch < ' ') {
                        sb.append(String.format(Locale.ROOT, "\\u%04x", (int) ch));
                    } else {
                        sb.append(ch);
                    }
            }
        }
        return sb.toString();
    }

    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    private static final String DASHBOARD_HTML = """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Jcrontab Management Console</title>
    <style>
        :root {
            --primary: #2563eb;
            --primary-hover: #1d4ed8;
            --bg: #0f172a;
            --card-bg: #1e293b;
            --border: #334155;
            --text: #f8fafc;
            --text-muted: #94a3b8;
            --success: #10b981;
            --danger: #ef4444;
            --warning: #f59e0b;
        }
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; }
        body { background-color: var(--bg); color: var(--text); padding: 24px; min-height: 100vh; }
        .container { max-width: 1200px; margin: 0 auto; }
        header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; border-bottom: 1px solid var(--border); padding-bottom: 16px; flex-wrap: wrap; gap: 12px; }
        .title-group { display: flex; align-items: center; gap: 12px; }
        h1 { font-size: 24px; font-weight: 700; color: #fff; }
        .badge { background: #3b82f6; color: #fff; font-size: 12px; font-weight: 600; padding: 4px 10px; border-radius: 9999px; }
        .status-pill { display: flex; align-items: center; gap: 6px; font-size: 13px; color: var(--success); font-weight: 600; background: rgba(16, 185, 129, 0.1); padding: 6px 12px; border-radius: 9999px; border: 1px solid rgba(16, 185, 129, 0.2); }
        .pulse { width: 8px; height: 8px; background: var(--success); border-radius: 50%; box-shadow: 0 0 8px var(--success); }
        .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 16px; margin-bottom: 24px; }
        .stat-card { background: var(--card-bg); border: 1px solid var(--border); border-radius: 8px; padding: 16px; }
        .stat-label { font-size: 12px; color: var(--text-muted); text-transform: uppercase; font-weight: 600; margin-bottom: 6px; }
        .stat-value { font-size: 24px; font-weight: 700; color: #fff; }
        .actions-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; gap: 12px; flex-wrap: wrap; }
        .search-box { flex: 1; max-width: 400px; padding: 10px 14px; background: var(--card-bg); border: 1px solid var(--border); border-radius: 6px; color: #fff; outline: none; }
        .search-box:focus { border-color: var(--primary); }
        .btn { padding: 9px 16px; border-radius: 6px; font-weight: 600; font-size: 13px; cursor: pointer; border: none; display: inline-flex; align-items: center; gap: 6px; transition: all 0.15s ease; text-decoration: none; }
        .btn-primary { background: var(--primary); color: #fff; }
        .btn-primary:hover { background: var(--primary-hover); }
        .btn-success { background: var(--success); color: #fff; }
        .btn-danger { background: var(--danger); color: #fff; }
        .btn-secondary { background: var(--card-bg); color: var(--text); border: 1px solid var(--border); }
        .btn-secondary:hover { background: var(--border); }
        .btn-sm { padding: 5px 10px; font-size: 12px; }
        table { width: 100%; border-collapse: collapse; background: var(--card-bg); border: 1px solid var(--border); border-radius: 8px; overflow: hidden; }
        th { text-align: left; padding: 12px 16px; background: rgba(0,0,0,0.2); color: var(--text-muted); font-size: 12px; text-transform: uppercase; font-weight: 600; border-bottom: 1px solid var(--border); }
        td { padding: 14px 16px; border-bottom: 1px solid var(--border); font-size: 13px; vertical-align: middle; }
        tr:last-child td { border-bottom: none; }
        tr:hover td { background: rgba(255,255,255,0.02); }
        .cron-expr { font-family: monospace; background: rgba(0,0,0,0.3); padding: 4px 8px; border-radius: 4px; color: #38bdf8; font-weight: bold; }
        .tag { font-size: 11px; padding: 2px 8px; border-radius: 4px; font-weight: 600; }
        .tag-true { background: rgba(16, 185, 129, 0.2); color: var(--success); }
        .tag-false { background: rgba(148, 163, 184, 0.2); color: var(--text-muted); }
        .actions-cell { display: flex; gap: 8px; }
        .modal-backdrop { display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.7); z-index: 1000; align-items: center; justify-content: center; padding: 16px; }
        .modal { background: var(--card-bg); border: 1px solid var(--border); border-radius: 12px; width: 100%; max-width: 580px; overflow: hidden; box-shadow: 0 20px 25px -5px rgba(0,0,0,0.5); }
        .modal-header { padding: 16px 20px; border-bottom: 1px solid var(--border); display: flex; justify-content: space-between; align-items: center; }
        .modal-body { padding: 20px; display: flex; flex-direction: column; gap: 14px; max-height: 80vh; overflow-y: auto; }
        .modal-footer { padding: 16px 20px; border-top: 1px solid var(--border); display: flex; justify-content: flex-end; gap: 10px; background: rgba(0,0,0,0.1); }
        .form-group { display: flex; flex-direction: column; gap: 6px; }
        .form-row { display: grid; grid-template-columns: repeat(auto-fit, minmax(80px, 1fr)); gap: 10px; }
        label { font-size: 12px; font-weight: 600; color: var(--text-muted); }
        input[type="text"], select { background: rgba(0,0,0,0.3); border: 1px solid var(--border); padding: 8px 12px; border-radius: 6px; color: #fff; font-size: 13px; outline: none; }
        input[type="text"]:focus, select:focus { border-color: var(--primary); }
        .checkbox-label { display: flex; align-items: center; gap: 8px; font-size: 13px; cursor: pointer; color: var(--text); }
        .toast { position: fixed; bottom: 24px; right: 24px; background: var(--card-bg); border: 1px solid var(--border); padding: 12px 20px; border-radius: 8px; box-shadow: 0 10px 15px -3px rgba(0,0,0,0.5); font-size: 13px; font-weight: 600; z-index: 2000; display: none; }
    </style>
</head>
<body>
    <div class="container">
        <header>
            <div class="title-group">
                <h1>Jcrontab Management Console</h1>
                <span class="badge">v2.0.0</span>
                <!--AUTH_BADGE-->
            </div>
            <div class="status-pill">
                <span class="pulse"></span>
                <span>Active Service</span>
            </div>
        </header>

        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-label">Total Scheduled Tasks</div>
                <div class="stat-value" id="stat-total">0</div>
            </div>
            <div class="stat-card">
                <div class="stat-label">Runtime Engine</div>
                <div class="stat-value" style="font-size: 18px; color: #38bdf8;">Java 21 Virtual Threads</div>
            </div>
            <div class="stat-card">
                <div class="stat-label">Web Console Port</div>
                <div class="stat-value" id="stat-port">...</div>
            </div>
        </div>

        <div class="actions-bar">
            <input type="text" class="search-box" id="search-input" placeholder="Search tasks by class, method, or parameters..." oninput="filterTasks()">
            <div style="display: flex; gap: 10px;">
                <button class="btn btn-secondary" onclick="loadTasks()">↻ Refresh</button>
                <button class="btn btn-primary" onclick="openModal()">+ Add New Task</button>
            </div>
        </div>

        <table id="tasks-table">
            <thead>
                <tr>
                    <th style="width: 50px;">ID</th>
                    <th>Schedule</th>
                    <th>Business Days</th>
                    <th>Target Class & Method</th>
                    <th>Parameters (Extra Info)</th>
                    <th style="width: 220px;">Actions</th>
                </tr>
            </thead>
            <tbody id="tasks-tbody">
                <tr><td colspan="6" style="text-align: center; color: var(--text-muted);">Loading tasks...</td></tr>
            </tbody>
        </table>
    </div>

    <!-- Modal -->
    <div class="modal-backdrop" id="task-modal">
        <div class="modal">
            <div class="modal-header">
                <h3 id="modal-title">New Cron Task</h3>
                <button onclick="closeModal()" style="background:none; border:none; color:var(--text-muted); cursor:pointer; font-size:18px;">&times;</button>
            </div>
            <div class="modal-body">
                <input type="hidden" id="task-id">
                <div class="form-group">
                    <label>Schedule Preset</label>
                    <select id="schedule-preset" onchange="applyPreset()">
                        <option value="">-- Custom --</option>
                        <option value="* * * * *">Every Minute (* * * * *)</option>
                        <option value="*/5 * * * *">Every 5 Minutes (*/5 * * * *)</option>
                        <option value="*/15 * * * *">Every 15 Minutes (*/15 * * * *)</option>
                        <option value="0 * * * *">Hourly (0 * * * *)</option>
                        <option value="0 0 * * *">Daily at Midnight (0 0 * * *)</option>
                        <option value="0 9 * * 1-5">Mon-Fri at 9:00 AM (0 9 * * 1-5)</option>
                    </select>
                </div>
                <div class="form-row">
                    <div class="form-group">
                        <label>Minute</label>
                        <input type="text" id="task-minute" value="*">
                    </div>
                    <div class="form-group">
                        <label>Hour</label>
                        <input type="text" id="task-hour" value="*">
                    </div>
                    <div class="form-group">
                        <label>Day Month</label>
                        <input type="text" id="task-dom" value="*">
                    </div>
                    <div class="form-group">
                        <label>Month</label>
                        <input type="text" id="task-month" value="*">
                    </div>
                    <div class="form-group">
                        <label>Day Week</label>
                        <input type="text" id="task-dow" value="*">
                    </div>
                </div>
                <div class="form-group">
                    <label>Class Name</label>
                    <input type="text" id="task-class" placeholder="org.jcrontab.tests.TaskTest1">
                </div>
                <div class="form-group">
                    <label>Method Name (optional, defaults to main)</label>
                    <input type="text" id="task-method" placeholder="main">
                </div>
                <div class="form-group">
                    <label>Extra Info / Arguments (space separated)</label>
                    <input type="text" id="task-extra" placeholder="arg1 arg2">
                </div>
                <div class="form-group">
                    <label class="checkbox-label">
                        <input type="checkbox" id="task-business">
                        <span>Run on Business Days Only</span>
                    </label>
                </div>
            </div>
            <div class="modal-footer">
                <button class="btn btn-secondary" onclick="closeModal()">Cancel</button>
                <button class="btn btn-primary" onclick="saveTask()">Save Task</button>
            </div>
        </div>
    </div>

    <div class="toast" id="toast"></div>

    <script>
        let allTasks = [];

        async function loadStatus() {
            try {
                const res = await fetch('/api/status');
                const data = await res.json();
                document.getElementById('stat-port').innerText = data.port || '8080';
            } catch (e) {
                console.error(e);
            }
        }

        async function loadTasks() {
            try {
                const res = await fetch('/api/tasks');
                allTasks = await res.json();
                document.getElementById('stat-total').innerText = allTasks.length;
                renderTasks(allTasks);
            } catch (e) {
                showToast('Failed to load tasks: ' + e.message, true);
            }
        }

        function renderTasks(tasks) {
            const tbody = document.getElementById('tasks-tbody');
            if (!tasks || tasks.length === 0) {
                tbody.innerHTML = '<tr><td colspan="6" style="text-align: center; color: var(--text-muted);">No scheduled tasks configured.</td></tr>';
                return;
            }
            tbody.innerHTML = tasks.map(t => `
                <tr>
                    <td><strong>#${t.id}</strong></td>
                    <td><span class="cron-expr">${escapeHtml(t.expression)}</span></td>
                    <td><span class="tag ${t.businessDays ? 'tag-true' : 'tag-false'}">${t.businessDays ? 'Yes' : 'No'}</span></td>
                    <td><strong>${escapeHtml(t.className)}</strong>${t.methodName ? '#' + escapeHtml(t.methodName) : ''}</td>
                    <td style="color: var(--text-muted);">${(t.extraInfo || []).map(escapeHtml).join(' ') || '<em>none</em>'}</td>
                    <td>
                        <div class="actions-cell">
                            <button class="btn btn-success btn-sm" onclick="runTask(${t.id})">▶ Run Now</button>
                            <button class="btn btn-secondary btn-sm" onclick="editTask(${t.id})">✎ Edit</button>
                            <button class="btn btn-danger btn-sm" onclick="deleteTask(${t.id})">🗑 Delete</button>
                        </div>
                    </td>
                </tr>
            `).join('');
        }

        function filterTasks() {
            const query = document.getElementById('search-input').value.toLowerCase();
            const filtered = allTasks.filter(t => 
                (t.className || '').toLowerCase().includes(query) ||
                (t.methodName || '').toLowerCase().includes(query) ||
                (t.expression || '').toLowerCase().includes(query) ||
                (t.extraInfo || []).join(' ').toLowerCase().includes(query)
            );
            renderTasks(filtered);
        }

        function openModal(task = null) {
            document.getElementById('modal-title').innerText = task ? 'Edit Cron Task #' + task.id : 'New Cron Task';
            document.getElementById('task-id').value = task ? task.id : '';
            document.getElementById('schedule-preset').value = '';
            document.getElementById('task-minute').value = task ? task.minutes : '*';
            document.getElementById('task-hour').value = task ? task.hours : '*';
            document.getElementById('task-dom').value = task ? task.daysOfMonth : '*';
            document.getElementById('task-month').value = task ? task.months : '*';
            document.getElementById('task-dow').value = task ? task.daysOfWeek : '*';
            document.getElementById('task-class').value = task ? task.className : '';
            document.getElementById('task-method').value = task ? task.methodName : '';
            document.getElementById('task-extra').value = task && task.extraInfo ? task.extraInfo.join(' ') : '';
            document.getElementById('task-business').checked = task ? !!task.businessDays : false;
            document.getElementById('task-modal').style.display = 'flex';
        }

        function closeModal() {
            document.getElementById('task-modal').style.display = 'none';
        }

        function applyPreset() {
            const val = document.getElementById('schedule-preset').value;
            if (!val) return;
            const parts = val.split(' ');
            if (parts.length === 5) {
                document.getElementById('task-minute').value = parts[0];
                document.getElementById('task-hour').value = parts[1];
                document.getElementById('task-dom').value = parts[2];
                document.getElementById('task-month').value = parts[3];
                document.getElementById('task-dow').value = parts[4];
            }
        }

        function editTask(id) {
            const task = allTasks.find(t => t.id === id);
            if (task) openModal(task);
        }

        async function saveTask() {
            const id = document.getElementById('task-id').value;
            const payload = {
                id: id || undefined,
                minutes: document.getElementById('task-minute').value.trim() || '*',
                hours: document.getElementById('task-hour').value.trim() || '*',
                daysOfMonth: document.getElementById('task-dom').value.trim() || '*',
                months: document.getElementById('task-month').value.trim() || '*',
                daysOfWeek: document.getElementById('task-dow').value.trim() || '*',
                className: document.getElementById('task-class').value.trim(),
                methodName: document.getElementById('task-method').value.trim(),
                extraInfo: document.getElementById('task-extra').value.trim(),
                businessDays: document.getElementById('task-business').checked ? 'true' : 'false'
            };

            if (!payload.className) {
                alert('Please provide a class name.');
                return;
            }

            try {
                const res = await fetch('/api/tasks', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });
                const data = await res.json();
                if (res.ok) {
                    closeModal();
                    showToast(data.message || 'Task saved successfully!');
                    loadTasks();
                } else {
                    alert('Error: ' + (data.error || 'Failed to save task'));
                }
            } catch (e) {
                alert('Error: ' + e.message);
            }
        }

        async function runTask(id) {
            try {
                const res = await fetch('/api/tasks/run?id=' + id, { method: 'POST' });
                const data = await res.json();
                if (res.ok) {
                    showToast('Task #' + id + ' triggered! (TaskID: ' + data.taskId + ')');
                } else {
                    showToast('Failed to trigger task: ' + (data.error || 'Unknown error'), true);
                }
            } catch (e) {
                showToast('Failed: ' + e.message, true);
            }
        }

        async function deleteTask(id) {
            if (!confirm('Are you sure you want to delete task #' + id + '?')) return;
            try {
                const res = await fetch('/api/tasks?id=' + id, { method: 'DELETE' });
                const data = await res.json();
                if (res.ok) {
                    showToast('Task #' + id + ' deleted!');
                    loadTasks();
                } else {
                    showToast('Failed to delete: ' + (data.error || 'Unknown error'), true);
                }
            } catch (e) {
                showToast('Failed: ' + e.message, true);
            }
        }

        function showToast(msg, isError = false) {
            const toast = document.getElementById('toast');
            toast.innerText = msg;
            toast.style.borderColor = isError ? 'var(--danger)' : 'var(--success)';
            toast.style.color = isError ? 'var(--danger)' : 'var(--success)';
            toast.style.display = 'block';
            setTimeout(() => { toast.style.display = 'none'; }, 3500);
        }

        function escapeHtml(text) {
            if (!text) return '';
            return String(text).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
        }

        loadStatus();
        loadTasks();
    </script>
</body>
</html>
""";
}
