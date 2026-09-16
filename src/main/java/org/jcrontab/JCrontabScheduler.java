/**
 * This file is part of the jcrontab package
 * Copyright (C) 2001-2026 Israel Olalla
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 */
package org.jcrontab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.jcrontab.data.CrontabEntryBean;
import org.jcrontab.data.CrontabParser;
import org.jcrontab.log.Log;

/**
 * Modern, high-performance task scheduler supporting standard POSIX crontab syntax,
 * analytical next-execution calculations, timezone awareness, and Java 21 Virtual Threads.
 *
 * Implements AutoCloseable and supports multiple independent instances per JVM.
 *
 * @author Israel Olalla
 */
public class JCrontabScheduler implements AutoCloseable {

    private final AtomicInteger idGenerator = new AtomicInteger(1);
    private final AtomicBoolean running = new AtomicBoolean(false);

    private final ScheduledExecutorService timerService;
    private final ExecutorService taskExecutor;
    private final boolean ownsExecutors;

    private final Map<Integer, ScheduledTask> tasks = new ConcurrentHashMap<>();
    private final List<TaskListener> listeners = new CopyOnWriteArrayList<>();
    private final CrontabParser parser = new CrontabParser();

    public interface TaskHandle {
        int getId();
        CrontabEntry getEntry();
        void cancel();
        boolean isCancelled();
    }

    private class ScheduledTask implements TaskHandle {
        final CrontabEntry entry;
        final AtomicBoolean cancelled = new AtomicBoolean(false);
        volatile ScheduledFuture<?> future;
        volatile ZonedDateTime lastScheduledTime;

        ScheduledTask(CrontabEntry entry) {
            this.entry = entry;
        }

        @Override
        public int getId() {
            return entry.id();
        }

        @Override
        public CrontabEntry getEntry() {
            return entry;
        }

        @Override
        public void cancel() {
            if (cancelled.compareAndSet(false, true)) {
                if (future != null) {
                    future.cancel(false);
                }
                tasks.remove(entry.id());
            }
        }

        @Override
        public boolean isCancelled() {
            return cancelled.get();
        }
    }

    public static class Builder {
        private ExecutorService taskExecutor;
        private ScheduledExecutorService timerService;
        private boolean useVirtualThreads = true;
        private final List<TaskListener> listeners = new ArrayList<>();

        public Builder withVirtualThreads(boolean useVirtualThreads) {
            this.useVirtualThreads = useVirtualThreads;
            return this;
        }

        public Builder withTaskExecutor(ExecutorService executor) {
            this.taskExecutor = executor;
            return this;
        }

        public Builder withTimerService(ScheduledExecutorService timerService) {
            this.timerService = timerService;
            return this;
        }

        private boolean enableWeb = false;
        private int webPort = 8080;
        private String authUser = null;
        private String authPassword = null;
        private java.util.function.Predicate<LocalDate> holidayPredicate = null;

        public Builder withHolidayPredicate(java.util.function.Predicate<LocalDate> holidayPredicate) {
            this.holidayPredicate = holidayPredicate;
            return this;
        }

        public Builder addListener(TaskListener listener) {
            if (listener != null) {
                this.listeners.add(listener);
            }
            return this;
        }

        public Builder enableWeb() {
            return enableWeb(8080);
        }

        public Builder enableWeb(int port) {
            this.enableWeb = true;
            this.webPort = port;
            return this;
        }

        public Builder webAuth(String password) {
            return webAuth("admin", password);
        }

        public Builder webAuth(String user, String password) {
            this.authUser = user;
            this.authPassword = password;
            return this;
        }

        public JCrontabScheduler build() {
            boolean owns = (taskExecutor == null && timerService == null);
            ExecutorService exec = taskExecutor;
            if (exec == null) {
                if (useVirtualThreads) {
                    exec = Executors.newVirtualThreadPerTaskExecutor();
                } else {
                    exec = Executors.newCachedThreadPool();
                }
            }

            ScheduledExecutorService timer = timerService;
            if (timer == null) {
                timer = Executors.newSingleThreadScheduledExecutor(r -> {
                    Thread t = new Thread(r, "jcrontab-timer");
                    t.setDaemon(true);
                    return t;
                });
            }

            JCrontabScheduler scheduler = new JCrontabScheduler(timer, exec, owns, enableWeb, webPort, authUser, authPassword);
            scheduler.setHolidayPredicate(holidayPredicate);
            listeners.forEach(scheduler::addListener);
            return scheduler;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static JCrontabScheduler create() {
        return builder().build();
    }

    private final boolean enableWeb;
    private final int webPort;
    private final String webAuthUser;
    private final String webAuthPassword;
    private org.jcrontab.web.JcrontabWebServer webServer;
    private volatile java.util.function.Predicate<LocalDate> holidayPredicate;

    public void setHolidayPredicate(java.util.function.Predicate<LocalDate> holidayPredicate) {
        this.holidayPredicate = holidayPredicate;
    }

    public JCrontabScheduler(ScheduledExecutorService timerService, ExecutorService taskExecutor, boolean ownsExecutors, boolean enableWeb, int webPort, String webAuthUser, String webAuthPassword) {
        this.timerService = Objects.requireNonNull(timerService, "timerService cannot be null");
        this.taskExecutor = Objects.requireNonNull(taskExecutor, "taskExecutor cannot be null");
        this.ownsExecutors = ownsExecutors;
        this.enableWeb = enableWeb;
        this.webPort = webPort;
        this.webAuthUser = webAuthUser;
        this.webAuthPassword = webAuthPassword;
    }

    public JCrontabScheduler(ScheduledExecutorService timerService, ExecutorService taskExecutor, boolean ownsExecutors, boolean enableWeb, int webPort) {
        this(timerService, taskExecutor, ownsExecutors, enableWeb, webPort, null, null);
    }

    public JCrontabScheduler(ScheduledExecutorService timerService, ExecutorService taskExecutor, boolean ownsExecutors) {
        this(timerService, taskExecutor, ownsExecutors, false, 8080);
    }

    /**
     * Adds an execution event listener.
     */
    public void addListener(TaskListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    /**
     * Removes an execution event listener.
     */
    public void removeListener(TaskListener listener) {
        listeners.remove(listener);
    }

    /**
     * Schedules a lambda Runnable using standard cron syntax.
     */
    public TaskHandle schedule(String cronExpression, Runnable action) {
        return schedule(cronExpression, ZoneId.systemDefault(), action);
    }

    /**
     * Schedules a lambda Runnable with a specific timezone.
     */
    public TaskHandle schedule(String cronExpression, ZoneId zoneId, Runnable action) {
        CronSchedule schedule = parser.parseSchedule(cronExpression);
        int id = idGenerator.getAndIncrement();
        CrontabEntry entry = CrontabEntry.of(id, schedule, zoneId, action);
        return schedule(entry);
    }

    /**
     * Schedules an existing CrontabEntry.
     */
    public TaskHandle schedule(CrontabEntry entry) {
        Objects.requireNonNull(entry, "Entry cannot be null");
        ScheduledTask task = new ScheduledTask(entry);
        tasks.put(entry.id(), task);

        if (running.get()) {
            scheduleNext(task);
        }
        return task;
    }

    /**
     * Schedules an entry described by legacy CrontabEntryBean.
     */
    public TaskHandle schedule(CrontabEntryBean bean) {
        Objects.requireNonNull(bean, "CrontabEntryBean cannot be null");
        if (bean.getId() <= 0) {
            bean.setId(idGenerator.getAndIncrement());
        }
        return schedule(bean.toCrontabEntry());
    }

    /**
     * Loads and schedules crontab lines from a file path.
     */
    public List<TaskHandle> loadCrontab(Path path) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return loadCrontab(reader);
        }
    }

    /**
     * Loads and schedules crontab lines from an InputStream.
     */
    public List<TaskHandle> loadCrontab(InputStream in) throws IOException {
        return loadCrontab(new InputStreamReader(in, StandardCharsets.UTF_8));
    }

    /**
     * Loads and schedules crontab lines from a Reader.
     */
    public List<TaskHandle> loadCrontab(Reader in) throws IOException {
        BufferedReader reader = (in instanceof BufferedReader br) ? br : new BufferedReader(in);
        List<TaskHandle> loaded = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }
            try {
                int id = idGenerator.getAndIncrement();
                CrontabEntry entry = parser.parseEntry(trimmed, id);
                loaded.add(schedule(entry));
            } catch (Exception e) {
                Log.error("Error parsing crontab line: " + line, e);
            }
        }
        return loaded;
    }

    /**
     * Starts scheduler execution.
     */
    public synchronized void start() {
        if (running.compareAndSet(false, true)) {
            Log.info("Starting JCrontabScheduler with " + tasks.size() + " tasks");
            for (ScheduledTask task : tasks.values()) {
                scheduleNext(task);
            }
            if (enableWeb) {
                try {
                    webServer = new org.jcrontab.web.JcrontabWebServer(webPort, webAuthUser, webAuthPassword, this);
                    webServer.start();
                } catch (java.io.IOException e) {
                    Log.error("Failed to start Jcrontab Web Console: " + e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Stops the scheduler, cancelling all scheduled future triggers.
     */
    public synchronized void stop() {
        if (running.compareAndSet(true, false)) {
            Log.info("Stopping JCrontabScheduler");
            if (webServer != null) {
                webServer.stop();
                webServer = null;
            }
            for (ScheduledTask task : tasks.values()) {
                if (task.future != null) {
                    task.future.cancel(false);
                }
            }
        }
    }

    /**
     * Returns the embedded Web Console if enabled.
     */
    public org.jcrontab.web.JcrontabWebServer getWebServer() {
        return webServer;
    }

    /**
     * Checks if the scheduler is running.
     */
    public boolean isRunning() {
        return running.get();
    }

    /**
     * Returns an unmodifiable list of registered tasks.
     */
    public List<CrontabEntry> getTasks() {
        List<CrontabEntry> list = new ArrayList<>();
        for (ScheduledTask st : tasks.values()) {
            list.add(st.getEntry());
        }
        return Collections.unmodifiableList(list);
    }

    /**
     * Cancels and removes a scheduled task by ID.
     *
     * @param id task ID
     * @return true if removed, false if not found
     */
    public boolean cancelTask(int id) {
        ScheduledTask task = tasks.get(id);
        if (task != null) {
            task.cancel();
            return true;
        }
        return false;
    }

    /**
     * Triggers immediate asynchronous execution of the task with the given ID.
     *
     * @param id task ID
     * @return true if the task was found and triggered, false otherwise
     */
    public boolean triggerNow(int id) {
        ScheduledTask task = tasks.get(id);
        if (task == null || task.isCancelled()) {
            return false;
        }
        executeTask(task, ZonedDateTime.now(task.getEntry().zoneId()), true);
        return true;
    }

    private void scheduleNext(ScheduledTask task) {
        if (!running.get() || task.isCancelled()) {
            return;
        }

        CrontabEntry entry = task.getEntry();
        ZoneId zone = entry.zoneId();
        ZonedDateTime now = ZonedDateTime.now(zone);
        ZonedDateTime reference = now;
        ZonedDateTime prev = task.lastScheduledTime;
        if (prev != null && prev.isAfter(now)) {
            reference = prev;
        }

        Optional<ZonedDateTime> nextTimeOpt = entry.schedule().nextExecution(reference);

        if (nextTimeOpt.isEmpty()) {
            Log.info("Task " + entry.id() + " has no future execution time within horizon.");
            return;
        }

        ZonedDateTime nextTime = nextTimeOpt.get();
        task.lastScheduledTime = nextTime;
        Duration delay = Duration.between(Instant.now(), nextTime.toInstant());
        long delayMillis = delay.isNegative() ? 0 : (delay.toMillis() + (delay.getNano() % 1_000_000 > 0 ? 1 : 0));

        listeners.forEach(l -> {
            try { l.onScheduled(entry, nextTime); } catch (Throwable ignored) {}
        });

        task.future = timerService.schedule(() -> {
            try {
                if (!task.isCancelled() && running.get()) {
                    executeTask(task, nextTime, false);
                }
            } finally {
                if (!task.isCancelled() && running.get()) {
                    scheduleNext(task);
                }
            }
        }, delayMillis, TimeUnit.MILLISECONDS);
    }

    private void executeTask(ScheduledTask task, ZonedDateTime executionTime, boolean force) {
        CrontabEntry entry = task.getEntry();

        if (!force && entry.businessDaysOnly() && isWeekendOrHoliday(executionTime.toLocalDate())) {
            Log.debug("Skipping task " + entry.id() + " (not a business day: " + executionTime.toLocalDate() + ")");
            return;
        }

        taskExecutor.submit(() -> {
            listeners.forEach(l -> {
                try { l.onStarted(entry, executionTime); } catch (Throwable ignored) {}
            });

            Instant start = Instant.now();
            try {
                if (entry.taskAction() != null) {
                    entry.taskAction().run();
                } else if (entry.className() != null && !entry.className().isEmpty()) {
                    executeReflection(entry);
                }
                Duration duration = Duration.between(start, Instant.now());
                listeners.forEach(l -> {
                    try { l.onSuccess(entry, duration); } catch (Throwable ignored) {}
                });
            } catch (Throwable t) {
                Duration duration = Duration.between(start, Instant.now());
                Log.error("Task execution failed [id=" + entry.id() + ", class=" + entry.className() + "]: " + t.getMessage(), t);
                listeners.forEach(l -> {
                    try { l.onFailure(entry, t, duration); } catch (Throwable ignored) {}
                });
            }
        });
    }

    private void executeReflection(CrontabEntry entry) throws Exception {
        Class<?> clazz = Class.forName(entry.className());
        String methodName = entry.methodName();
        String[] extraInfo = entry.extraInfo();

        if (methodName != null && !methodName.isEmpty()) {
            try {
                Method m = clazz.getMethod(methodName, String[].class);
                Object target = Modifier.isStatic(m.getModifiers()) ? null : clazz.getDeclaredConstructor().newInstance();
                m.invoke(target, (Object) extraInfo);
                return;
            } catch (NoSuchMethodException e) {
                Method m = clazz.getMethod(methodName);
                Object target = Modifier.isStatic(m.getModifiers()) ? null : clazz.getDeclaredConstructor().newInstance();
                m.invoke(target);
                return;
            }
        }

        // Default to main(String[]) or Runnable.run()
        try {
            Method main = clazz.getMethod("main", String[].class);
            if (Modifier.isStatic(main.getModifiers())) {
                main.invoke(null, (Object) extraInfo);
                return;
            }
        } catch (NoSuchMethodException ignored) {}

        Object instance = clazz.getDeclaredConstructor().newInstance();
        if (instance instanceof Runnable runnable) {
            runnable.run();
        } else {
            throw new NoSuchMethodException("No suitable method found to execute on " + entry.className());
        }
    }

    private boolean isWeekendOrHoliday(LocalDate date) {
        DayOfWeek dow = date.getDayOfWeek();
        if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
            return true;
        }
        java.util.function.Predicate<LocalDate> pred = this.holidayPredicate;
        if (pred != null && pred.test(date)) {
            return true;
        }
        try {
            String hs = Crontab.getInstance().getProperty("org.jcrontab.data.holidaysource");
            if (hs != null && !hs.trim().isEmpty()) {
                org.jcrontab.data.HoliDay[] holidays = org.jcrontab.data.HoliDayFactory.getInstance().findAll();
                if (holidays != null) {
                    for (org.jcrontab.data.HoliDay h : holidays) {
                        if (h != null && h.getDate() != null) {
                            LocalDate hDate = h.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            if (hDate.equals(date)) {
                                return true;
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
        return false;
    }

    @Override
    public void close() {
        stop();
        if (ownsExecutors) {
            timerService.shutdown();
            taskExecutor.shutdown();
            try {
                if (!timerService.awaitTermination(2, TimeUnit.SECONDS)) {
                    timerService.shutdownNow();
                }
                if (!taskExecutor.awaitTermination(2, TimeUnit.SECONDS)) {
                    taskExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                timerService.shutdownNow();
                taskExecutor.shutdownNow();
            }
        }
    }
}
