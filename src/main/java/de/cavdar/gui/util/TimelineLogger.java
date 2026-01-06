package de.cavdar.gui.util;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central logging utility that manages both standard logging and timeline/performance tracking.
 * All classes should use this instead of declaring their own loggers.
 *
 * <h3>Configuration:</h3>
 * <pre>
 *     // Configure logging with custom directory and filenames
 *     File logsDir = new File("path/to/logs");
 *     TimelineLogger.configure(logsDir, "myapp.log", "myapp-actions.log");
 *
 *     // Close when done
 *     TimelineLogger.close();
 * </pre>
 *
 * <h3>Standard Logging:</h3>
 * <pre>
 *     // Instead of: private static final Logger LOG = LoggerFactory.getLogger(MyClass.class);
 *     // Use:
 *     TimelineLogger.info(MyClass.class, "Processing {} items", count);
 *     TimelineLogger.debug(MyClass.class, "Details: {}", details);
 *     TimelineLogger.warn(MyClass.class, "Warning message");
 *     TimelineLogger.error(MyClass.class, "Error occurred", exception);
 * </pre>
 *
 * <h3>Timeline/Performance Tracking:</h3>
 * <pre>
 *     // Variante 1: Start/End mit ID
 *     String actionId = TimelineLogger.start("loadData", "Loading customer data");
 *     // ... do work ...
 *     TimelineLogger.end(actionId);  // oder: TimelineLogger.end(actionId, "OK");
 *
 *     // Variante 2: Try-with-resources (empfohlen)
 *     try (TimelineLogger.Action action = TimelineLogger.action("processFile")) {
 *         // ... do work ...
 *         action.result("5 records");  // optional
 *     }
 *
 *     // Variante 3: Einzelnes Event
 *     TimelineLogger.event("userLogin", "user=admin");
 * </pre>
 *
 * @author TemplateGUI
 * @version 2.1
 */
public class TimelineLogger {

    // ===== Logger Configuration =====
    private static final String TIMELINE_LOGGER_NAME = "TIMELINE";
    private static final String TIMELINE_APPENDER_NAME = "TimelineAppender";
    private static final String APP_APPENDER_NAME = "AppFileAppender";
    private static final String TIMELINE_PATTERN = "%d{dd.MM.yyyy HH:mm:ss.SSS} | %m%n";
    private static final String APP_PATTERN = "%d{dd.MM.yyyy HH:mm:ss.SSS} [%-5p] %c - %m%n";

    // SLF4J Logger for timeline logging calls
    private static final Logger TIMELINE = LoggerFactory.getLogger(TIMELINE_LOGGER_NAME);
    // Log4j Logger for timeline appender configuration
    private static final org.apache.log4j.Logger LOG4J_TIMELINE = org.apache.log4j.Logger.getLogger(TIMELINE_LOGGER_NAME);

    // ===== Standard Logger Cache =====
    private static final Map<Class<?>, Logger> loggerCache = new ConcurrentHashMap<>();

    // ===== Timeline Action Tracking =====
    private static final Map<String, ActionInfo> activeActions = new ConcurrentHashMap<>();
    private static RollingFileAppender timelineAppender;
    private static RollingFileAppender appAppender;
    private static long actionCounter = 0;

    static {
        // Prevent timeline log propagation to root logger
        LOG4J_TIMELINE.setAdditivity(false);
    }

    private TimelineLogger() {
        // Utility class
    }

    // ==========================================================================
    // STANDARD LOGGING METHODS
    // ==========================================================================

    /**
     * Gets or creates a logger for the specified class.
     * Use this if you need the Logger instance directly.
     *
     * @param clazz the class to get logger for
     * @return the SLF4J logger
     */
    public static Logger getLogger(Class<?> clazz) {
        return loggerCache.computeIfAbsent(clazz, LoggerFactory::getLogger);
    }

    /**
     * Logs a TRACE message.
     */
    public static void trace(Class<?> clazz, String message, Object... args) {
        getLogger(clazz).trace(message, args);
    }

    /**
     * Logs a DEBUG message.
     */
    public static void debug(Class<?> clazz, String message, Object... args) {
        getLogger(clazz).debug(message, args);
    }

    /**
     * Logs an INFO message.
     */
    public static void info(Class<?> clazz, String message, Object... args) {
        getLogger(clazz).info(message, args);
    }

    /**
     * Logs a WARN message.
     */
    public static void warn(Class<?> clazz, String message, Object... args) {
        getLogger(clazz).warn(message, args);
    }

    /**
     * Logs a WARN message with exception.
     */
    public static void warn(Class<?> clazz, String message, Throwable t) {
        getLogger(clazz).warn(message, t);
    }

    /**
     * Logs an ERROR message.
     */
    public static void error(Class<?> clazz, String message, Object... args) {
        getLogger(clazz).error(message, args);
    }

    /**
     * Logs an ERROR message with exception.
     */
    public static void error(Class<?> clazz, String message, Throwable t) {
        getLogger(clazz).error(message, t);
    }

    /**
     * Checks if DEBUG level is enabled for the class.
     */
    public static boolean isDebugEnabled(Class<?> clazz) {
        return getLogger(clazz).isDebugEnabled();
    }

    /**
     * Checks if TRACE level is enabled for the class.
     */
    public static boolean isTraceEnabled(Class<?> clazz) {
        return getLogger(clazz).isTraceEnabled();
    }

    // ==========================================================================
    // LOGGER CONFIGURATION
    // ==========================================================================

    /**
     * Configures both the application logger and the timeline/action logger.
     * This is the central configuration point for all file-based logging.
     *
     * @param logOutputDir    the directory for log files
     * @param appLogFileName  the filename for application logs (e.g., "app.log")
     * @param actionLogFileName the filename for timeline/action logs (e.g., "actions.log")
     * @return true if configuration was successful
     */
    public static boolean configure(File logOutputDir, String appLogFileName, String actionLogFileName) {
        try {
            // Ensure directory exists
            if (!logOutputDir.exists() && !logOutputDir.mkdirs()) {
                System.err.println("[TimelineLogger] Could not create log directory: " + logOutputDir.getAbsolutePath());
                return false;
            }

            // Configure application logger
            boolean appConfigured = configureAppLogger(new File(logOutputDir, appLogFileName));

            // Configure timeline logger
            boolean timelineConfigured = configureTimelineLogger(new File(logOutputDir, actionLogFileName));

            return appConfigured && timelineConfigured;
        } catch (Exception e) {
            System.err.println("[TimelineLogger] Error configuring: " + e.getMessage());
            return false;
        }
    }

    /**
     * Configures the application file appender.
     */
    private static boolean configureAppLogger(File logFile) {
        try {
            org.apache.log4j.Logger rootLogger = org.apache.log4j.Logger.getRootLogger();

            if (appAppender != null) {
                // Update existing appender
                appAppender.setFile(logFile.getAbsolutePath());
                appAppender.activateOptions();
                System.out.println("[TimelineLogger] App logger reconfigured: " + logFile.getAbsolutePath());
                return true;
            }

            // Create new appender
            appAppender = new RollingFileAppender();
            appAppender.setName(APP_APPENDER_NAME);
            appAppender.setFile(logFile.getAbsolutePath());
            appAppender.setMaxFileSize("10MB");
            appAppender.setMaxBackupIndex(10);
            appAppender.setLayout(new PatternLayout(APP_PATTERN));
            appAppender.setAppend(true);
            appAppender.activateOptions();

            rootLogger.addAppender(appAppender);
            System.out.println("[TimelineLogger] App logger initialized: " + logFile.getAbsolutePath());
            return true;
        } catch (Exception e) {
            System.err.println("[TimelineLogger] Error configuring app logger: " + e.getMessage());
            return false;
        }
    }

    /**
     * Configures the timeline file appender.
     */
    private static boolean configureTimelineLogger(File logFile) {
        try {
            if (timelineAppender != null) {
                // Update existing appender
                timelineAppender.setFile(logFile.getAbsolutePath());
                timelineAppender.activateOptions();
                System.out.println("[TimelineLogger] Timeline logger reconfigured: " + logFile.getAbsolutePath());
                return true;
            }

            // Create new appender
            timelineAppender = new RollingFileAppender();
            timelineAppender.setName(TIMELINE_APPENDER_NAME);
            timelineAppender.setFile(logFile.getAbsolutePath());
            timelineAppender.setMaxFileSize("10MB");
            timelineAppender.setMaxBackupIndex(5);
            timelineAppender.setLayout(new PatternLayout(TIMELINE_PATTERN));
            timelineAppender.setAppend(true);
            timelineAppender.activateOptions();

            LOG4J_TIMELINE.addAppender(timelineAppender);
            System.out.println("[TimelineLogger] Timeline logger initialized: " + logFile.getAbsolutePath());
            return true;
        } catch (Exception e) {
            System.err.println("[TimelineLogger] Error configuring timeline logger: " + e.getMessage());
            return false;
        }
    }

    /**
     * Closes all loggers and releases file handles.
     */
    public static void close() {
        // Close timeline appender
        if (timelineAppender != null) {
            timelineAppender.close();
            LOG4J_TIMELINE.removeAppender(timelineAppender);
            timelineAppender = null;
        }
        // Close app appender
        if (appAppender != null) {
            appAppender.close();
            org.apache.log4j.Logger.getRootLogger().removeAppender(appAppender);
            appAppender = null;
        }
        activeActions.clear();
    }

    // ==========================================================================
    // TIMELINE ACTION TRACKING
    // ==========================================================================

    /**
     * Starts tracking an action.
     *
     * @param actionName the name of the action
     * @return the action ID (use this to call end())
     */
    public static String start(String actionName) {
        return start(actionName, null);
    }

    /**
     * Starts tracking an action with a description.
     *
     * @param actionName  the name of the action
     * @param description optional description
     * @return the action ID (use this to call end())
     */
    public static String start(String actionName, String description) {
        String actionId = generateActionId(actionName);
        Instant startTime = Instant.now();

        ActionInfo info = new ActionInfo(actionName, description, startTime);
        activeActions.put(actionId, info);

        StringBuilder sb = new StringBuilder();
        sb.append("START | action=").append(actionName);
        if (description != null && !description.isEmpty()) {
            sb.append(" | desc=").append(description);
        }
        sb.append(" | id=").append(actionId);

        TIMELINE.info(sb.toString());
        return actionId;
    }

    /**
     * Ends tracking an action and logs the duration.
     *
     * @param actionId the action ID returned by start()
     */
    public static void end(String actionId) {
        end(actionId, null);
    }

    /**
     * Ends tracking an action with a result message.
     *
     * @param actionId the action ID returned by start()
     * @param result   optional result message (e.g., "OK", "FAILED", "5 records")
     */
    public static void end(String actionId, String result) {
        ActionInfo info = activeActions.remove(actionId);
        if (info == null) {
            TIMELINE.warn("END   | action=UNKNOWN | id={} | error=No matching start", actionId);
            return;
        }

        Instant endTime = Instant.now();
        Duration duration = Duration.between(info.startTime, endTime);

        StringBuilder sb = new StringBuilder();
        sb.append("END   | action=").append(info.actionName);
        sb.append(" | duration=").append(formatDuration(duration));
        if (result != null && !result.isEmpty()) {
            sb.append(" | result=").append(result);
        }
        sb.append(" | id=").append(actionId);

        TIMELINE.info(sb.toString());
    }

    /**
     * Logs a single event (no start/end tracking).
     *
     * @param eventName the name of the event
     */
    public static void event(String eventName) {
        event(eventName, null);
    }

    /**
     * Logs a single event with details.
     *
     * @param eventName the name of the event
     * @param details   optional details
     */
    public static void event(String eventName, String details) {
        StringBuilder sb = new StringBuilder();
        sb.append("EVENT | name=").append(eventName);
        if (details != null && !details.isEmpty()) {
            sb.append(" | details=").append(details);
        }
        TIMELINE.info(sb.toString());
    }

    /**
     * Creates an auto-closeable action for use with try-with-resources.
     *
     * @param actionName the name of the action
     * @return an Action that will log end() when closed
     */
    public static Action action(String actionName) {
        return new Action(actionName, null);
    }

    /**
     * Creates an auto-closeable action with description.
     *
     * @param actionName  the name of the action
     * @param description optional description
     * @return an Action that will log end() when closed
     */
    public static Action action(String actionName, String description) {
        return new Action(actionName, description);
    }

    // ==========================================================================
    // HELPER METHODS AND INNER CLASSES
    // ==========================================================================

    private static synchronized String generateActionId(String actionName) {
        return actionName + "-" + (++actionCounter);
    }

    private static String formatDuration(Duration duration) {
        long millis = duration.toMillis();
        if (millis < 1000) {
            return millis + "ms";
        } else if (millis < 60000) {
            return String.format("%.2fs", millis / 1000.0);
        } else {
            long minutes = duration.toMinutes();
            long secs = duration.toSecondsPart();
            return String.format("%dm %ds", minutes, secs);
        }
    }

    /**
     * Holds information about an active action.
     */
    private static class ActionInfo {
        final String actionName;
        final String description;
        final Instant startTime;

        ActionInfo(String actionName, String description, Instant startTime) {
            this.actionName = actionName;
            this.description = description;
            this.startTime = startTime;
        }
    }

    /**
     * Auto-closeable action for try-with-resources pattern.
     */
    public static class Action implements AutoCloseable {
        private final String actionId;
        private String result;

        Action(String actionName, String description) {
            this.actionId = TimelineLogger.start(actionName, description);
        }

        /**
         * Sets the result to be logged when the action ends.
         *
         * @param result the result message
         * @return this action for chaining
         */
        public Action result(String result) {
            this.result = result;
            return this;
        }

        @Override
        public void close() {
            TimelineLogger.end(actionId, result);
        }
    }
}
