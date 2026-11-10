package utils.platform;

import java.nio.file.Path;


public interface IIntegrationService 
{
    /**
     * Открыть файл соответствующей для него программой
     * @param osType тип операционной системы
     * @param path путь к файлу
     * @return код ошибки открытия файла
     * */
    void openFile(Path path);

    /**
     * Открыть директорию в терминале. Терминал запуститься в отдельнои процессе и не заблокирует работу файлового менеджера
     * @param path путь к директории
     */
    void openInTerminal(Path path);

     /**
     * Переместить файл (директорию) в корзину
     * @param filePath путь к файлу(директории)
     * @return true если перемещенеи в корзину прошло успешно, иначе false
     */
    boolean moveToTrash(Path filePath);

    /**
     * Запустить выполнение команды отдельным процессом без ожидания
     * @param command команда
     * @param args аргументы команды
     * @return процесс, выполняющий команду
     */
    Process runCommand(String command, String... args);

    /**
     * Запустить команду в отдельном процессе и дождаться её завершения
     * @param command команда
     * @param args аргуменыт к команде
     * @return true, если процесс был успешно завершён, иначе false
     */
    boolean runCommandAndWait(String command, String... args);
}
