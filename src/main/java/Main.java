import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DBLinks dbLinks = new DBLinks();
        PropertiesFile propertiesFile = new PropertiesFile();
        propertiesFile.loadProperties();
        SiteListFile siteListFile = new SiteListFile();
        DBLinks.siteList = SiteListFile.readingSiteListFile();
        boolean runProgram = true;
        System.out.println("\nПрограмма для отслеживания обновлений игр на сайте 'thelastgame.ru'");
        printList();
        try (Scanner input = new Scanner(System.in)) {
            while (runProgram) {
                try {
                    System.out.print(
                            "\nМеню:\n" +
                                    "1. Проверить обновления.\n" +
                                    "2. Добавить ссылку для отслеживания.\n" +
                                    "3. Удалить игру из отслеживаемых.\n" +
                                    "4. Изменить путь сохранения.\n" +
                                    "5. Завершить программу.\n" +
                                    "> ");

                    int numOp = Integer.parseInt(input.nextLine());
                    if (numOp < 1 || numOp > 5) System.err.println("Выбери пункт меню!");
                    switch (numOp) {
                        case 1: // Проверить обновления
                            dbLinks.checkUpdate(siteListFile);
                            break;

                        case 2: // Добавить ссылку для отслеживания
                            System.out.print("Введи название ссылки:> ");
                            String link = input.nextLine();
                            if (link.contains("http")) {
                                try {
                                    Document document = Jsoup.connect(link).get();
                                    if (dbLinks.parsingSite(document, link)) siteListFile.saveSiteListFile();
                                    System.out.println("Игра добавлена в список отслеживаемых.");
                                } catch (IOException exception) {
                                    System.err.println("Введи ссылку на страницу с игрой (например: https://thelastgame.ru/crossout/): ");
                                }
                                printList();
                            } else {
                                System.err.println("Введи правильную ссылку!");
                            }
                            break;

                        case 3: // Удалить игру из отслеживаемых
                            printList();
                            System.out.println("Введи номер игры, которую нужно удалить:");
                            int n = Integer.parseInt(input.nextLine());
                            DBLinks.siteList.remove(n - 1);
                            siteListFile.saveSiteListFile();
                            printList();
                            break;

                        case 4:
                            System.out.println("Путь сохранения торрент-файлов: " + DBLinks.path);
                            System.out.print("Введи путь или 'esc':> ");
                            String string = input.nextLine();
                            if (!string.equals("esc")) {
                                String path = new String(string.getBytes("windows-1251"));
                                System.out.println(path);
                                propertiesFile.saveProperty(path);
                                break;
                            } else {
                                continue;
                            }

                        case 5: // Завершить программу
                            runProgram = false;
                    }
                } catch (Exception e) {
                    System.err.println("Неверный ввод!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printList() {
        System.out.println("Игр в базе отслеживания " + DBLinks.siteList.size() + ":");
        for (int i = 0; i < DBLinks.siteList.size(); i++) {
            Site site = DBLinks.siteList.get(i);
            System.out.println(i + 1 + ". " + site.getName() + " " + site.getDate());
        }
    }
}

