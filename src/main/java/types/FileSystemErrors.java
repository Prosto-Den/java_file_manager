package types;

public enum FileSystemErrors
{
    DESKTOP_NOT_SUPPORTED, // Desktop API не поддерживается на данном устройстве
    FILE_NOT_FOUND, // Файл не найден
    OPEN_FILE_ERROR, // Ошибка открытия файла
    OK, // Ошибок нет :)
    NOT_A_FILE, // Не является файлом
    PROCESS_IO_ERROR, // Ошибка работы ввода/вывода процесса
    PROCESS_CLOSE_ERROR, // Процесс закрылся раньше времени
    UNKNOWN_ERROR // Неизвестная ошибка
}
