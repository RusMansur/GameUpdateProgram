import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

public class PropertiesFile implements GetJarPath {
    Properties properties = new Properties();
    String torrentPath = "SAVE_TORRENTFILE_PATH";

    File programPath = GetJarPath.getPath().getParentFile();
    File propertiesFile = new File(programPath + "\\config.ini");

    //    Создать файл свойств
    public void createPropertiesFile() {
        if (!propertiesFile.exists()) {
            try {
                propertiesFile.createNewFile();
                properties.setProperty(torrentPath, programPath.getPath());
                properties.store(Files.newOutputStream(propertiesFile.toPath()), "");
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    // Загрузить файл свойств
    public void loadProperties() {
        if (propertiesFile.exists()) {
            try {
                properties.load(Files.newInputStream(propertiesFile.toPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            DBLinks.path = properties.getProperty(torrentPath);
        } else {
            createPropertiesFile();
        }
    }

    // Записать свойство в файл
    public void saveProperty(String path) {
        DBLinks.path = path;
        try {
            properties.setProperty(torrentPath, path);
            properties.store(Files.newOutputStream(propertiesFile.toPath()), "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
