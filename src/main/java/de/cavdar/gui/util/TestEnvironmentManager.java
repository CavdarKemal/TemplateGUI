package de.cavdar.gui.util;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Manages test environment directories and logging configuration.
 * Creates environment-specific directories under TEST-ENVS and configures
 * logging to use the appropriate log file.
 *
 * @author TemplateGUI
 * @version 1.0
 */
public class TestEnvironmentManager {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(TestEnvironmentManager.class);

    private static final String TEST_ENVS_DIR = "TEST-ENVS";
    private static final String LOGS_DIR = "logs";
    private static final String TEST_OUTPUTS_DIR = "TEST-OUTPUTS";

    private static String currentEnvironment = null;
    private static File currentEnvDir = null;
    private static File currentLogsDir = null;
    private static File currentTestOutputsDir = null;
    private static File baseDirectory = null;

    private TestEnvironmentManager() {
        // Utility class
    }

    /**
     * Sets the base directory for TEST-ENVS.
     * If not set, uses user.dir.
     *
     * @param baseDir the base directory
     */
    public static void setBaseDirectory(File baseDir) {
        baseDirectory = baseDir;
        LOG.debug("Base directory set to: {}", baseDir);
    }

    /**
     * Gets the current base directory.
     *
     * @return the base directory (user.dir if not explicitly set)
     */
    public static File getBaseDirectory() {
        if (baseDirectory != null) {
            return baseDirectory;
        }
        return new File(System.getProperty("user.dir"));
    }

    /**
     * Resets the manager state. Useful for testing.
     * Also closes any open FileAppender to release file handles.
     */
    public static void reset() {
        closeLogging();
        currentEnvironment = null;
        currentEnvDir = null;
        currentLogsDir = null;
        currentTestOutputsDir = null;
        baseDirectory = null;
    }

    /**
     * Closes the FileAppender to release file handles.
     * Useful for testing to allow temp directory cleanup.
     */
    public static void closeLogging() {
        try {
            Logger rootLogger = Logger.getRootLogger();
            Enumeration<?> appenders = rootLogger.getAllAppenders();

            while (appenders.hasMoreElements()) {
                Object appender = appenders.nextElement();
                if (appender instanceof FileAppender) {
                    FileAppender fileAppender = (FileAppender) appender;
                    fileAppender.close();
                    rootLogger.removeAppender(fileAppender);
                }
            }
        } catch (Exception e) {
            // Ignore errors during cleanup
        }
    }

    /**
     * Extracts the environment name from a config file name.
     * Takes the first 3 characters and converts to uppercase.
     *
     * @param configFileName the config file name (e.g., "ene-config.properties")
     * @return the environment name (e.g., "ENE")
     */
    public static String extractEnvironmentName(String configFileName) {
        if (configFileName == null || configFileName.length() < 3) {
            return "DEFAULT";
        }
        return configFileName.substring(0, 3).toUpperCase();
    }

    /**
     * Switches to a new environment based on the config file name.
     * Creates the necessary directories and reconfigures logging.
     *
     * @param configFileName the config file name (e.g., "ene-config.properties")
     * @return true if switch was successful
     */
    public static boolean switchEnvironment(String configFileName) {
        String envName = extractEnvironmentName(configFileName);

        if (envName.equals(currentEnvironment)) {
            LOG.debug("Already in environment: {}", envName);
            return true;
        }

        LOG.info("Switching to environment: {}", envName);

        // Create directory structure
        File baseDir = getBaseDirectory();
        File testEnvsDir = new File(baseDir, TEST_ENVS_DIR);
        File envDir = new File(testEnvsDir, envName);
        File logsDir = new File(envDir, LOGS_DIR);
        File testOutputsDir = new File(envDir, TEST_OUTPUTS_DIR);

        // Create directories
        if (!createDirectories(logsDir, testOutputsDir)) {
            return false;
        }

        // Configure logging
        File logFile = new File(logsDir, envName + ".log");
        if (!configureLogging(logFile)) {
            LOG.warn("Could not configure logging for environment: {}", envName);
        }

        // Update state
        currentEnvironment = envName;
        currentEnvDir = envDir;
        currentLogsDir = logsDir;
        currentTestOutputsDir = testOutputsDir;

        LOG.info("Environment switched to: {} ({})", envName, envDir.getAbsolutePath());
        return true;
    }

    /**
     * Creates the required directories.
     */
    private static boolean createDirectories(File logsDir, File testOutputsDir) {
        try {
            if (!logsDir.exists() && !logsDir.mkdirs()) {
                LOG.error("Could not create logs directory: {}", logsDir.getAbsolutePath());
                return false;
            }
            if (!testOutputsDir.exists() && !testOutputsDir.mkdirs()) {
                LOG.error("Could not create test outputs directory: {}", testOutputsDir.getAbsolutePath());
                return false;
            }
            LOG.debug("Directories created/verified: {}, {}", logsDir.getAbsolutePath(), testOutputsDir.getAbsolutePath());
            return true;
        } catch (SecurityException e) {
            LOG.error("Security exception creating directories", e);
            return false;
        }
    }

    /**
     * Configures log4j FileAppender to use the specified log file.
     * If no FileAppender exists, creates a new RollingFileAppender.
     */
    private static boolean configureLogging(File logFile) {
        try {
            Logger rootLogger = Logger.getRootLogger();
            Enumeration<?> appenders = rootLogger.getAllAppenders();
            FileAppender existingFileAppender = null;

            // Find existing FileAppender
            while (appenders.hasMoreElements()) {
                Object appender = appenders.nextElement();
                if (appender instanceof FileAppender) {
                    existingFileAppender = (FileAppender) appender;
                    break;
                }
            }

            if (existingFileAppender != null) {
                // Update existing FileAppender
                existingFileAppender.setFile(logFile.getAbsolutePath());
                existingFileAppender.activateOptions();
                System.out.println("[TestEnvironmentManager] Log file set to: " + logFile.getAbsolutePath());
                return true;
            }

            // No FileAppender found, create a new RollingFileAppender
            System.out.println("[TestEnvironmentManager] Creating new FileAppender for: " + logFile.getAbsolutePath());
            RollingFileAppender newAppender = new RollingFileAppender();
            newAppender.setName("FileAppender");
            newAppender.setFile(logFile.getAbsolutePath());
            newAppender.setMaxFileSize("10MB");
            newAppender.setMaxBackupIndex(10);
            newAppender.setLayout(new PatternLayout("%d{dd.MM.yyyy HH:mm:ss.SSS} [%-5p] %c - %m%n"));
            newAppender.setAppend(true);
            newAppender.activateOptions();

            rootLogger.addAppender(newAppender);
            System.out.println("[TestEnvironmentManager] FileAppender created and added to root logger");
            return true;
        } catch (Exception e) {
            System.err.println("[TestEnvironmentManager] Error configuring logging: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ===== Getters =====

    /**
     * Returns the current environment name.
     */
    public static String getCurrentEnvironment() {
        return currentEnvironment;
    }

    /**
     * Returns the current environment directory.
     */
    public static File getCurrentEnvDir() {
        return currentEnvDir;
    }

    /**
     * Returns the current logs directory.
     */
    public static File getCurrentLogsDir() {
        return currentLogsDir;
    }

    /**
     * Returns the current test outputs directory.
     */
    public static File getCurrentTestOutputsDir() {
        return currentTestOutputsDir;
    }

    /**
     * Returns the current log file path.
     */
    public static String getCurrentLogFilePath() {
        if (currentLogsDir == null || currentEnvironment == null) {
            return null;
        }
        return new File(currentLogsDir, currentEnvironment + ".log").getAbsolutePath();
    }
}
