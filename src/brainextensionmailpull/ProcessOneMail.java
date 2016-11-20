/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package brainextensionmailpull;

import static brainextensionmailpull.BrainExtensionMailPull.downloadPath;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;

/**
 *
 * @author xandi
 */
public class ProcessOneMail {

    private static String attachmentsString;

    public void processOneMessage(Message msg, String email) {

        attachmentsString = "";

        System.out.println("processOneMessage()");

        try {

            System.out.println("-------------------------------");

            System.out.println("Message number : " + msg.getMessageNumber());

            Enumeration headers = msg.getAllHeaders();
            String header = "";

            header += "\n\n";

            Address[] x = msg.getAllRecipients();
            Address oneAdd;
            String add = "";

            if (x == null) {
                add = "no recipient";
//                    System.out.println("Message has no recipient");
            } else {

                for (int r = 0; r < x.length; r++) {
                    oneAdd = x[r];
                    add += oneAdd.toString();
                }

            }

            // uniques message ID
            String id = msg.getHeader("Message-ID")[0];

            if (id == null) {

                id = "unknown";
            }

            long whatever = id.hashCode();

            if (whatever < 0) {
                whatever = whatever * -1;
            }
            Date sentDate = null;
            sentDate = msg.getSentDate();

            Calendar c = Calendar.getInstance();
            c.setTime(sentDate);
            int day = c.get(Calendar.DAY_OF_MONTH);
            int month = c.get(Calendar.MONTH);
            month = month + 1;
            int year = c.get(Calendar.YEAR);

            String from = "unknown";
            if (msg.getReplyTo().length >= 1) {
                from = msg.getReplyTo()[0].toString();
            } else if (msg.getFrom().length >= 1) {
                from = msg.getFrom()[0].toString();
            }
            String subject = msg.getSubject();

            // skip if existing
            String filename = getPath(email, year, month, day) + java.nio.file.FileSystems.getDefault().getSeparator() + whatever;

            if (fileExists(filename)) {
                System.out.println("file exists ... ");
            } else {

                System.out.println("file is new ... ");

                BufferedReader reader = new BufferedReader(new InputStreamReader(msg.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
                String everything = sb.toString();

                everything = header + everything;

//                System.out.println(everything);
                saveOriginal(everything, filename);

                System.out.println("filename: " + filename);

                if (msg.getContent() == null) {
                    System.out.println("msg.getContent() == null: ");
                }

                saveParts(msg.getContent(), filename);
                //msg.setFlag(Flags.Flag.SEEN, true);
                // to delete the message
                // msg.setFlag(Flags.Flag.DELETED, true);

                String dbEntry = "";

                dbEntry += year + "|";
                dbEntry += month + "|";
                dbEntry += day + "|";
                dbEntry += whatever + "|";
                if (msg.getSentDate() != null) {
                    dbEntry += cleanString(msg.getSentDate().toString()) + "|";
                } else {
                    dbEntry += "null" + "|";
                }

                if (msg.getReceivedDate() != null) {
                    dbEntry += cleanString(msg.getReceivedDate().toString()) + "|";
                } else {
                    dbEntry += "null" + "|";
                }

                dbEntry += cleanString(getFromString(msg)) + "|";
                dbEntry += cleanString(getToString(msg)) + "|";
                dbEntry += cleanString(attachmentsString) + "|";

                String subj;
                if (msg.getSubject() != null) {
                    dbEntry += cleanString(msg.getSubject()) + "|";
                    subj = cleanString(msg.getSubject());
                } else {
                    dbEntry += "null" + "|";
                    subj = "";
                }

                dbEntry += id + "|";

                appendToDB("." + java.nio.file.FileSystems.getDefault().getSeparator() + "db.txt", dbEntry);
                attachmentsString = "";

                insertDataInDatabase(subj, cleanString(attachmentsString));

            }

        } catch (Exception x) {
            x.printStackTrace();
            System.out.println("something went wrong: " + x.toString());
            System.out.println("I will move over to next message");

        }

    }

    private void insertDataInDatabase(String subject, String path) {

        // subjec
        // content
//        We get the contant via reading the html and txt fiel from path
                
        // filepath to attachemnts
        // we need an id
        // we need to put it in the right folder
        // insert a random string into the db, then get the id of this insert
        // get the id of the folder where it should be a child of
        // add the relationship in database
    }

    private String cleanString(String s) {
        String ret = "";
        try {
            ret = s.replaceAll("\\|", " ");
        } catch (Exception x) {

        }
        return ret;
    }

    private String getPath(String email, int year, int month, int day) {

        String retPath = downloadPath;

        String x = java.nio.file.FileSystems.getDefault().getSeparator();

        retPath += x + email;
        makeDir(retPath);
        retPath += x + year;
        makeDir(retPath);
        retPath += x + month;
        makeDir(retPath);
        retPath += x + day;
        makeDir(retPath);

        return retPath;

    }

    private static void makeDir(String path) {
//        System.out.println("trying to create directory: " + path);
        File theDir = new File(path);

// if the directory does not exist, create it
        if (!theDir.exists()) {

            boolean result = false;

            try {
                theDir.mkdir();
                result = true;
            } catch (SecurityException se) {
                //handle it
            }
            if (result) {
//                System.out.println("DIR created");
            }
        } else {
            if (theDir.isFile()) {
//                System.out.println("creating directory because only file wiht this name exists: " + path);
                boolean result = false;

                try {

                    theDir.mkdir();
                    result = true;
                } catch (Exception se) {
                    //handle it
                    System.out.println("error: ");
                    se.printStackTrace();
                }
                if (result) {
//                    System.out.println("DIR created");
                }
            }
        }

    }

    boolean fileExists(String path) {
        File theDir = new File(path);
        return theDir.exists();
    }

    private void appendToDB(String fileName, String text) {

        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)))) {
            out.println(text);
            out.close();
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
            e.printStackTrace();
        }
    }

    private String getFromString(Message msg) {
        String ret = "";

        try {

            if (msg.getReplyTo() != null) {
                if (msg.getReplyTo().length >= 1) {

                    for (int i = 0; i < msg.getReplyTo().length; i++) {
                        ret += msg.getReplyTo()[i].toString() + ";";
                    }

                }
            }

            if (msg.getFrom() != null) {
                if (msg.getFrom().length >= 1) {

                    for (int i = 0; i < msg.getFrom().length; i++) {
                        ret += msg.getFrom()[i].toString() + ";";
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;

    }

    private String getToString(Message msg) {
        String ret = "";
        Address adr;

        try {
            if (msg.getRecipients(Message.RecipientType.TO) != null) {
                if (msg.getRecipients(Message.RecipientType.TO).length >= 1) {

                    for (int i = 0; i < msg.getRecipients(Message.RecipientType.TO).length; i++) {
                        adr = msg.getRecipients(Message.RecipientType.TO)[i];
                        ret += adr.toString() + ";";
                    }

                }
            }

            if (msg.getRecipients(Message.RecipientType.CC) != null) {
                if (msg.getRecipients(Message.RecipientType.CC).length >= 1) {

                    for (int i = 0; i < msg.getRecipients(Message.RecipientType.CC).length; i++) {
                        adr = msg.getRecipients(Message.RecipientType.CC)[i];
                        ret += adr.toString() + ";";
                    }

                }
            }

            if (msg.getRecipients(Message.RecipientType.BCC) != null) {
                if (msg.getRecipients(Message.RecipientType.BCC).length >= 1) {

                    for (int i = 0; i < msg.getRecipients(Message.RecipientType.BCC).length; i++) {
                        adr = msg.getRecipients(Message.RecipientType.BCC)[i];
                        ret += adr.toString() + ";";
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
        return ret;

    }

    public static void saveParts(Object content, String filename)
            throws IOException, MessagingException {

        System.out.println("saveParts called with attachmentsString: " + attachmentsString);
        OutputStream out = null;
        InputStream in = null;
        String type = "";
        String extension = "";
        String filenameWithExtension = "";

        try {
            if (content instanceof Multipart) {

                // make a subfolder
                Multipart multi = ((Multipart) content);
                int parts = multi.getCount();
                for (int j = 0; j < parts; ++j) {
                    MimeBodyPart part = (MimeBodyPart) multi.getBodyPart(j);
                    if (part.getContent() instanceof Multipart) {

                        saveParts(part.getContent(), filename);

                    } else {

                        makeDir(filename);

                        type = part.getContentType();
                        String typeUp = type.toLowerCase();
                        // part.isMimeType("text/html")
                        // part.isMimeType("text/plain")
                        System.out.println("type: " + typeUp);

                        if (typeUp.contains("text/html")) {
//                            extension = "mail.html";
                            System.out.println("in html type section");

                            extension = part.getDataHandler().getName();
                            System.out.println("extension = '" + extension + "'");

                            if (extension == null || extension == "null") {
                                extension = "mail.html";
                            }

                            filenameWithExtension = filename + java.nio.file.FileSystems.getDefault().getSeparator() + extension;

                            attachmentsString += extension + ";";
                            part.saveFile(filenameWithExtension);

                        } else {
                            if (typeUp.contains("text/plain")) {
                                System.out.println("in text  type section");
//                                extension = "mail.txt";
                                extension = part.getDataHandler().getName();
                                if (extension == null || extension == "null") {
                                    extension = "mail.txt";
                                }

                            } else {
                                //  Try to get the name of the attachment
                                extension = part.getDataHandler().getName();

                            }

                            if (extension == null) {
                                extension = "UNKNOWN-FILE-TYPE-AND-NAME.dat";
                            }
                            if (extension.contains("=?")) {
                                extension = "UNKNOWN-FILE-TYPE-AND-NAME.dat";
                            }

                            filenameWithExtension = filename + java.nio.file.FileSystems.getDefault().getSeparator() + extension;
                            attachmentsString += extension + ";";
                            part.saveFile(filenameWithExtension);
                        }
                    }
                }
            }
        } finally {
        }
    }

    public static void saveOriginal(String content, String filename)
            throws IOException, MessagingException {

        PrintWriter out = null;

        try {

            filename = filename + ".dat";

            out = new PrintWriter(filename);
            out.println(content);

        } finally {

            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

}
