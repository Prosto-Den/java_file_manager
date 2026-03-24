package models;

import org.jetbrains.annotations.NotNull;
import java.util.Locale;

public record Language(String code, String displayName, String flagPath)
{
    public Locale toLocale()
    {
        String[] parts = code.split("_");
        return parts.length == 2 ? Locale.of(parts[0], parts[1]) : Locale.of(code);
    }

    @Override
    public @NotNull String toString() { return displayName; }
}
