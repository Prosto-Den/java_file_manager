package utils.logger;

import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.ConsoleHandler;
import java.util.logging.LogManager;
import java.util.logging.Handler;
import java.io.IOException;

import models.AppSettings.LoggerSettings;
import app.AppContext;


public class LoggerHelper 
{
    /**
     * Проинициализировать глобальный логгер
     * @param loggerSettings настройки логгера
     */
    public static void initLogger(LoggerSettings loggerSettings) 
    {
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        for (Handler handler : rootLogger.getHandlers())
            rootLogger.removeHandler(handler);

        Level level = convertLevel(loggerSettings.level);
        if (level == Level.OFF)
            return;

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(level);
        consoleHandler.setFormatter(new SimpleFormatter());
        rootLogger.addHandler(consoleHandler);

        if (loggerSettings.path != null)
        {
            if (loggerSettings.path.isEmpty())
                loggerSettings.path = AppContext.getLogPath().toString();
            try
            {
                FileHandler fileHandler = new FileHandler(loggerSettings.path, true);
                fileHandler.setLevel(level);
                fileHandler.setFormatter(new SimpleFormatter());
                rootLogger.addHandler(fileHandler);
            }
            catch (IOException ex)
            {
                System.err.println("Не удалось создать файл для логгирования: " + ex.getMessage());
            }
        }
    }

    /**
     * Конвертировать строковое значение из настроек в Level
     * @param level тип логгирования в виде строки
     * @return тип логгирования в виде Level
     */
    private static Level convertLevel(String level)
    {
        if (level == null || level.isBlank())
            return Level.INFO;

        switch (level.toUpperCase())
        {
            case "OFF": return Level.OFF;
            case "SEVERE":
            case "ERROR":
                return Level.SEVERE;
            case "WARNING":
            case "WARN":
                return Level.WARNING;
            case "CONFIG": return Level.CONFIG;
            case "FINE": return Level.FINE;
            case "FINEST": return Level.FINEST;
            case "ALL": return Level.ALL;
            default: return Level.INFO;
        }
    }
}
