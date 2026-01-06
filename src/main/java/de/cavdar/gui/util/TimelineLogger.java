package de.cavdar.gui.util;

import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Specialized logger for timeline/performance tracking.
 * Logs actions with start time, end time, and duration to a separate file.
 * Integrates with TestEnvironmentManager for environment-specific log files.
 *
 * Usage:
 * <pre>
 *     String actionId = TimelineLogger.start("loadData", "Loading customer data");
 *     // ... do work ...
 *     TimelineLogger.end(actionId);
 *
 *     // Or with try-with-resources:
 *     try (TimelineLogger.Action action = TimelineLogger.action("processFile")) {
 *         // ... do work ...
 *     }
 * </pre>
 *
 * @author TemplateGUI
 * @version 1.0
 */
public class TimelineLogger {

    private static final String LOGGER_NAME = "TIMELINE";
    private static final String APPENDER_NAME = "TimelineAppender";
    private static final String LOG_FILE_NAME = "timeline.log";
    private static final String PATTERN = "%d{dd.MM.yyyy HH:mm:ss.SSS} | %m%n";

    private static final Logger TIMELINE = Logger.getLogger(LOGGER_NAME);
    private static final Map<String, ActionInfo> activeActions = new ConcurrentHashMap<>();

    private static RollingFileAppender fileAppender;
    private static long actionCounter = 0;

    static {
        // Prevent log propagation to root logger
        TIMELINE.setAdditivity(false);
    }

    private TimelineLogger() {
        // Utility class
    }

    /**
     * Configures the timeline logger to write to the specified directory.
     * Called by TestEnvironmentManager when switching environments.
     *
     * @param logsDir the directory for log files
     * @return true if configuration was successful
     */
    public static boolean configure(File logsDir) {
        try {
            File logFile = new File(logsDir, LOG_FILE_NAME);

            if (fileAppender != null) {
                // Update existing appender
                fileAppender.setFile(logFile.getAbsolutePath());
                fileAppender.activateOptions();
                TIMELINE.info("Timeline logger reconfigured: " + logFile.getAbsolutePath());
                return true;
            }

            // Create new appender
            fileAppender = new RollingFileAppender();
            fileAppender.setName(APPENDER_NAME);
            fileAppender.setFile(logFile.getAbsolutePath());
            fileAppender.setMaxFileSize("10MB");
            fileAppender.setMaxBackupIndex(5);
            fileAppender.setLayout(new PatternLayout(PATTERN));
            fileAppender.setAppend(true);
            fileAppender.activateOptions();

            TIMELINE.addAppender(fileAppender);
            TIMELINE.info("Timeline logger initialized: " + logFile.getAbsolutePath());
            return true;
        } catch (Exception e) {
            System.err.println("[TimelineLogger] Error configuring: " + e.getMessage());
            return false;
        }
    }

    /**
     * Closes the timeline logger and releases file handles.
     */
    public static void close() {
        if (fileAppender != null) {
            fileAppender.close();
            TIMELINE.removeAppender(fileAppender);
            fileAppender = null;
        }
        activeActions.clear();
    }

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
            TIMELINE.warn("END   | action=UNKNOWN | id=" + actionId + " | error=No matching start");
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
