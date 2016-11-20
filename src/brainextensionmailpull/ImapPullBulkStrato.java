/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package brainextensionmailpull;

import com.sun.mail.imap.IMAPFolder;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Properties;
import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;

/**
 *
 * @author xandi
 */
public class ImapPullBulkStrato {

    String filenameBasePath = ".";
    static String attachmentsString = "";

    public void doIt(String email, String pass) {

        System.out.println("email = " + email);
        System.out.println("pass = " + pass);        

        IMAPFolder folder = null;
        Folder folder2 = null;

        Folder[] folders = null;
        Folder[] subFolders = null;

        Store store = null;

        long timeNow;
        long timeLast;
        long messageCounter;
        long timeDiff;
        long timeTotal;

//        path = config.getValue("path");
        // testing with last num always 0
        int lastNumInt = 0;

        try {
            Properties props = System.getProperties();
            //props.setProperty("mail.store.protocol", "imaps");
            props.setProperty("mail.store.protocol", "imap");

            Session session = Session.getDefaultInstance(props, null);
            // session.setDebug(true);
            //store = session.getStore("imaps");
            store = session.getStore("imap");
            System.out.println("connecting ... ");

            System.out.println("email: " + email);
            System.out.println("pass: " + pass);

            store.connect("alexandersickert.com", 143, email, pass);

            System.out.println("connection established ... ");
            System.out.println("opening folder ... ");
            
            IMAPFolder[] f = (IMAPFolder[]) store.getDefaultFolder().list();

            lastNumInt = 0;

            if (f.length > 0) {
                for (IMAPFolder sfd : f) {
                    System.out.println("processing messages of " + sfd.getName());
                    try {
                        sfd.open(Folder.READ_ONLY);
                        Message messages[] = sfd.getMessages();
                        System.out.println("number of  messages in " + sfd.getName() + " : " + messages.length);
                        efficientGetContents(sfd, messages, email, lastNumInt);
                        sfd.close(false);
                    } catch (Exception x) {
                        System.out.println("could not open folder " + sfd.getName());
                        System.out.println("error  " + x.getMessage());
                    }
                    System.out.println("processing subfolders of " + sfd.getName());
                    processFolderArray(sfd, email, lastNumInt);
                }
            } else {
                System.out.println("IMAP default folder has length zero");
            }
            System.out.println("shutting down");
            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * this method loops through a folder
     *
     * @param folder
     * @param email
     * @param lastSuccess
     */
    private void processFolderArray(IMAPFolder folder, String email, int lastSuccess) {
        System.out.println("in processFolderArray for " + folder.getName());
        try {

//            javax.mail.Folder.
//                    com.sun.mail.imap.IMAPFolder
            System.out.println("opening the folder");

            folder.open(Folder.READ_ONLY);

//            IMAPFolder[] f = (IMAPFolder[]) store.getDefaultFolder().list();
            System.out.println("castinng folder;  " + folder.getFullName());

            System.out.println("castinng folder list length  " + folder.list().length);

            if (folder.list().length > 0) {

                IMAPFolder[] f = (IMAPFolder[]) folder.list();
                System.out.println("casting OK ");

                if (f.length > 0) {
                    for (IMAPFolder sfd : f) {
                        sfd.open(Folder.READ_ONLY);
                        Message messages[] = sfd.getMessages();
                        System.out.println("number of  messages in " + sfd.getName() + " : " + messages.length);
                        System.out.println("processing messages of " + sfd.getName());
                        efficientGetContents(sfd, messages, email, lastSuccess);

                        System.out.println("processing subfolders of " + sfd.getName());
                        processFolderArray(sfd, email, lastSuccess);
                    }
                }
            }

        } catch (Exception x) {
            System.out.println("error: " + x.getMessage());
            x.printStackTrace();

        }

    }

    /**
     * this method gets all the messages of one folder upt to a limit of
     * messages so that the total size does not exceed a certain level
     *
     * @param inbox
     * @param messages
     * @param email
     * @param lastSuccess
     * @return
     * @throws MessagingException
     */
    public int efficientGetContents(IMAPFolder inbox, Message[] messages, String email, int lastSuccess)
            throws MessagingException {
        FetchProfile fp = new FetchProfile();
        fp.add(FetchProfile.Item.FLAGS);
        fp.add(FetchProfile.Item.ENVELOPE);
        System.out.println("feching messages...");
        inbox.fetch(messages, fp);
        int index = 0;
        int nbMessages = messages.length;
        final int maxDoc = 200;
        final long maxSize = 100000000; // 100Mo
//        final long maxSize = 20000000; // 2MB

        // Message numbers limit to fetch
        int start;
        int end;
        int totalSize;

        index = lastSuccess;
        System.out.println("lastSuccess: " + lastSuccess);

        while (index < nbMessages) {

            start = messages[index].getMessageNumber();
            int docs = 0;
            totalSize = 0;
            boolean noskip = true; // There are no jumps in the message numbers
            // list
            boolean notend = true;
            // Until we reach one of the limits
            while (docs < maxDoc && totalSize < maxSize && noskip && notend) {
                docs++;
                totalSize += messages[index].getSize();

                // in future we will use this data to create a unique id for each email
                System.out.println(messages[index].getReceivedDate());
                System.out.println(messages[index].getFrom()[0]);
                System.out.println(messages[index].getSubject());

                System.out.println("total size of all message: " + totalSize);
                index++;
                if (notend = (index < nbMessages)) {
                    noskip = (messages[index - 1].getMessageNumber() + 1 == messages[index]
                            .getMessageNumber());
                }
            }

            end = messages[index - 1].getMessageNumber();

            System.out.println("now new protokoll command");
            System.out.println("Fetching contents for " + start + ":" + end);
            System.out.println("Size fetched = " + (totalSize / 1000000)
                    + " Mb");

            inbox.doCommand(new CustomProtocolCommand(start, end, email));

            //set last success value
      

        }

        return nbMessages;
    }

}
