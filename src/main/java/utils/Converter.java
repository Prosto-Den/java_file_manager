package utils;

import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Converter 
{
    public static String convertFileSize(long size)
    {
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        double res = size;
        int unitIndex = 0;

        while (res >= 1024 && unitIndex < units.length - 1)
        {
            res /= 1024;
            unitIndex++;
        }

        return String.format("%.2f %s", res, units[unitIndex]);
    } 
    
    // TODO возможно стоит сделать более универсальным (считать черех миллисекунды), как было раньше в FileSystemUtils
    public static String convertDateTime(FileTime fileDate)
    {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(fileDate.toInstant(), ZoneId.systemDefault());
        return localDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}
