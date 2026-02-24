package resourceHandler;

import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.*;
import java.net.URL;


/**
 * Класс дял управления строковыми ресурсами
 * */
public class StringResourceBundleControl extends ResourceBundle.Control
{
    private static final String allowedFormat = "xml";

    @Override
    public List<String> getFormats(String baseName)
    {
        return Collections.singletonList(allowedFormat);
    }

    @Override
    public ResourceBundle newBundle(String baseName,
                                    Locale locale,
                                    String format,
                                    ClassLoader loader,
                                    boolean reload) throws IllegalAccessException, InstantiationException, IOException
    {
        if (!format.equals(allowedFormat))
            return null;

        // строим имя ресурса со строками
        String bundleName = toBundleName(baseName, locale);
        String resourceName = toResourceName(bundleName, "xml");

        // получаем url для ресурса
        URL url = loader.getResource(resourceName);
        if (url == null)
            return null;

        // считываем данные с ресурса в поток ввода
        URLConnection connection = url.openConnection();
        if (connection == null)
            return null;

        try (InputStream stream = connection.getInputStream())
        {
            return new StringResourceBundle(stream);
        }
    }

    /**
     * Класс для хранения строковых ресурсов
     * */
    private static class StringResourceBundle extends ResourceBundle
    {
        // хранилище строковых ресурсов
        private final Map<String, String> resources = new HashMap<>();

        /**
         * Создать хранилище строковых ресурсов их потока ввода
         * @param stream поток ввода, содержащий строковые ресурсы в xml формате
         * */
        public StringResourceBundle(InputStream stream)
        {
            // тут происходит парсинг xml
            try
            {
                // считываем весь xml файл в properties. На этом этапе происходит парсинг по формату:
                // <properties>
                //      <entry key="ключ">Строковое значение</entry>
                // </properties>
                Properties props = new Properties();
                props.loadFromXML(stream);

                // перекладываем строковые ресурсы из properties в словарь
                for (String key : props.stringPropertyNames())
                    resources.put(key, props.getProperty(key));
            }
            catch (IOException e)
            {
                throw new RuntimeException("Не удалось загрузить XML ресурс", e);
            }
        }

        @Override
        protected Object handleGetObject(@NotNull String key) {
            return resources.get(key);
        }

        @Override
        public @NotNull Enumeration<String> getKeys() {
            return Collections.enumeration(resources.keySet());
        }
    }
}
