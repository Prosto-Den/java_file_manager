package models;

import org.jetbrains.annotations.NotNull;
import java.util.Locale;


/**
 * Класс для хранения данных локали
 * Language
 * @param code код локали
 * @param displayName отображаемое имя
 * @param flagPath путь к изображению флага (для отображения в UI)
 */
public record Language(String code, String displayName, String flagPath)
{   
    /**
     * Преобразовать данные в локаль
     * @return локаль, относящаяся к данному языку
     */
    public Locale toLocale()
    {
        String[] parts = code.split("_");
        return parts.length == 2 ? Locale.of(parts[0], parts[1]) : Locale.of(code);
    }

    @Override
    public @NotNull String toString() { return displayName; }
}
