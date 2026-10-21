package utils.logger;

import java.util.logging.Logger;
import java.util.logging.Level;


/**
 * Класс для упрощения работы с логгером
 * CustomLogger
 */
public class CustomLogger
{
    private final Logger logger;

    public CustomLogger(String name)
    {
        logger = Logger.getLogger(name);
    }

    /**
     * Логгировать инфомрационное сообщение
     * @param message текст сообщения
     */
    public void info(String message)
    {
        log(Level.INFO, message);
    }

    /**
     * Логгирование информационного сообщения
     * @param message текст сообщения
     * @param args аргументы сообщения
     */
    public void info(String message, String... args)
    {
        log(Level.INFO, message, args);
    }

    /**
     * Логгировать предупреждение
     * @param message сообщение
     */
    public void warn(String message)
    {
        log(Level.WARNING, message);
    }

    /**
     * Логгировать предупреждение
     * @param message сообщение
     * @param args аргументы сообщения
     */
    public void warn(String message, String... args)
    {
        log(Level.WARNING, message, args);
    }

    /**
     * Логгировать ошибку
     * @param message сообщение
     */
    public void error(String message)
    {
        log(Level.SEVERE, message);
    }

    /**
     * Логгировать ошибку
     * @param message сообщение
     * @param args аргументы сообщения
     */
    public void error(String message, String... args)
    {
        log(Level.SEVERE, message, args);
    }

    // приватные методы 

    /**
     * Логгировать сообщение
     * @param level уровень логгирования
     * @param message сообщение
     */
    private void log(Level level, String message)
    {
        logger.log(level, message);
    }

    /**
     * Логгировать сообщение с форматированием
     * @param level уровень логгирования
     * @param message сообщение
     * @param args аргументы сообщения
     */
    private void log(Level level, String message, String... args)
    {
        logger.log(level, message, args);
    }
}
