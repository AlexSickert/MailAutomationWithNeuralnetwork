package brainextensionmailpull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BrainExtensionMailPull {

    public static String downloadPath = ".";
    public static String folderFilter = ".";

    public static String dbConnString = "";
    public static String dbName = "";
    public static String dbUser = "";
    public static String dbPass = "";

    public static void main(String[] args) {
        // TODO code application logic here

        try {

            XmlConfigManager config = new XmlConfigManager("BrainExtensionMailPull.xml");
            //ImapPullSingleStrato pullStrato = new ImapPullSingleStrato();
            downloadPath = config.getValue("downloadPath");
            folderFilter = config.getValue("folderFilter");

            dbConnString = config.getValue("dbConnString");
            dbName = config.getValue("dbName");
            dbUser = config.getValue("dbUser");
            dbPass = config.getValue("dbPass");

            //pullStrato.loopThroughFolders(config.getValue("email"), config.getValue("pass"));
            ImapStratoJunk pullStrato = new ImapStratoJunk();

            while (true) {
                pullStrato.loopThroughFolders(config.getValue("email"), config.getValue("pass"));
                sleep();
            }

        } catch (Exception e) {

            e.printStackTrace();

            System.out.println(e.toString());

        }
    }

    private static void sleep() {

        int i = 10000 * 60 * 60;

        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            System.out.println(dateFormat.format(date));
            System.out.println("we go to sleep now for 1 hour.");
            Thread.sleep(i);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
