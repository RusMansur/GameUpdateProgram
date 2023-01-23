import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SiteListFile implements GetJarPath {
    // Путь сохранения файла со списком сайтов в каталоге с Jar-файлом (программой)
    static File siteListFile = new File(GetJarPath.getPath() + "/sitelist.json");

    // Прочитать файл sitelist.json
    public static List<Site> readingSiteListFile() {
        List<Site> list = new ArrayList<>();
        if (!siteListFile.exists() && siteListFile.length() != 0) {
            createSiteListFile();
        } else if (siteListFile.length() > 0) {
            JSONParser jsonParser = new JSONParser();
            try {
                Object object = jsonParser.parse(new FileReader(siteListFile));
                JSONArray jsonArray = (JSONArray) object;
                for (Object node : jsonArray) {
                    JSONObject jObject = (JSONObject) node;
                    Site site = new Site(
                            jObject.get("name").toString(),
                            jObject.get("href").toString(),
                            jObject.get("version").toString(),
                            jObject.get("date").toString()
                    );
                    list.add(site);
                }
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    // Создать файл sitelist.json
    private static void createSiteListFile() {
        if (!siteListFile.exists()) {
            try {
                siteListFile.createNewFile();
                readingSiteListFile();
            } catch (IOException e) {
                System.err.println("Не удалось создать файл списка сайтов!");
                e.printStackTrace();
            }
        }
    }

    // Сохранение списка сайтов в файл sitelist.json
    public void saveSiteListFile() {
        if (!readingSiteListFile().equals(DBLinks.siteList)) {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder
                    .setPrettyPrinting()
                    .create();
            Type listType = new TypeToken<List<Site>>() {
            }.getType();
            String json = gson.toJson(DBLinks.siteList, listType);
            try (FileWriter fileWriter = new FileWriter(siteListFile)) {
                fileWriter.write(json);
                fileWriter.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
                System.err.println("Не удалось записать файл");
            }
        }
    }
}

