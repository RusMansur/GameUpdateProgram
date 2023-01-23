import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DBLinks {
    static String path;
    static List<Site> siteList = SiteListFile.readingSiteListFile();

    // Парсинг сайта
    public boolean parsingSite(Document document, String link) throws IOException {
        String name = getGameName(document);
        String date = getDateFromSite(document);
        String version = getVersion(document);
        Site newSite = new Site(name, link, version, date);
        if (!siteList.contains(newSite)) {
            siteList.add(newSite);
            downloadTorrentFile(document);
            return true;
        }
        return false;
    }

    // Проверка обновлений
    public void checkUpdate(SiteListFile siteListFile) {
        String dateFromSite = null;
        String versionFromSite = null;
        boolean siteListChanged = false;
        for (Site siteInFile : siteList) {
            Document document = null;
            try {
                if (!(siteInFile.getHref() == null)) {
                    document = Jsoup.connect(siteInFile.getHref()).get();
                    dateFromSite = getDateFromSite(document);
                    versionFromSite = getVersion(document);
                    if (siteInFile.getDate() == null) {
                        siteInFile.setDate("01.01.1972");
                    }
                } else {
                    System.out.println("Нет ссылки для " + siteInFile.getName());
                }
                if (!compareDates(siteInFile, dateFromSite)) {
                    siteInFile.setDate(dateFromSite);
                    siteInFile.setVersion(versionFromSite);
                    downloadTorrentFile(document);
                    siteListChanged = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (siteListChanged) {siteListFile.saveSiteListFile();}
    }

    // Скачивание торрент-файла
    void downloadTorrentFile(Document document) throws IOException {
        String link = getTorrentLink(document);
        String[] strings = link.split("/");
        FileUtils.copyURLToFile(
                new URL(link),
                new File(path + strings[strings.length - 1])
        );
        System.out.println("Скачиваю торрент файл в каталог: " + path);
    }

    // Сравнение дат
    private boolean compareDates(Site siteInFile, String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate dateInFile = LocalDate.parse(siteInFile.getDate(), formatter);
        LocalDate dateInSite = LocalDate.parse(date, formatter);

        if (dateInFile.isBefore(dateInSite)) {
            System.out.println("Вышло обновление " + siteInFile.getName() + " " + date + "!");
            return false;
        } else {
            System.out.println("Обновлений " + siteInFile.getName() + " нет.");
            return true;
        }
    }

    // Получить название игры
    public String getGameName(Document document) {
        Elements element = document.getElementsByAttributeValue("name", "description");
        String stringContent = element.attr("content");
        String[] strings = null;
        if (stringContent.contains("–")) {
            strings = stringContent.split("–");
        } else if (stringContent.contains("-")) {
            strings = stringContent.split("-");
        }
        assert strings != null;
        return strings[0];
    }

    // Получить ссылку на торрент-файл
    public String getTorrentLink(Document document) {
        String href = null;
        Elements elements = document.getElementsByTag("a");
        for (Element element : elements) {
            if (element.hasClass("btn_green")) {
                href = element.attr("href");
            }
        }
        return href;
    }

    // Получить дату
    public String getDateFromSite(Document document) {
        String date = null;
        Elements elements = document.getElementsByTag("time");
        for (Element element : elements) {
            if (element.hasClass("updated")
                    && !element.hasClass("published updated")) {
                date = element.text();
            }
        }
        return date;
    }

    // Получить версию
    public String getVersion(Document document) {
        String version = null;
        Elements elements = document.getElementsByTag("div");
        for (Element element : elements) {
            if (element.hasAttr("style")) {
                String[] strings = element.text().split(" ");
                for (int i = 0; i < strings.length; i++) {
                    if (strings[i].equals("Версия:")) {
                        version = strings[i + 1];
                        break;
                    }
                }
            }
        }
        return version;
    }
}
