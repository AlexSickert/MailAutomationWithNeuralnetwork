package brainextensionmailpull;

import static brainextensionmailpull.BrainExtensionMailPull.folderFilter;
import com.sun.mail.imap.IMAPFolder;
import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;


public class ImapPullSingleStrato {

    ProcessOneMail proc = new ProcessOneMail();

    public void loopThroughFolders(String email, String pass) {

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

            System.out.println("done ... ");
            System.out.println("opening folder ... ");

            IMAPFolder[] f = (IMAPFolder[]) store.getDefaultFolder().list();

            IMAPFolder foTarget = (IMAPFolder) store.getFolder("INBOX.ROBOJUNK");
            foTarget.open(Folder.READ_WRITE);
            Message foTargetCount[] = foTarget.getMessages();
            System.out.println(foTargetCount.length);
            

            IMAPFolder foSource = (IMAPFolder) store.getFolder("INBOX");
            foSource.open(Folder.READ_WRITE);
            Message foSourceCount[] = foSource.getMessages();
            System.out.println(foSourceCount.length);
            
            MailSorter sort = new MailSorter();
            sort.testSort(foSource, foTarget);
            
            foTarget.close(true);
            foSource.close(true);

            System.exit(0);

            lastNumInt = 0;

            if (f.length > 0) {
                for (IMAPFolder sfd : f) {
                    System.out.println("processing messages of " + sfd.getName());
                    try {
                        sfd.open(Folder.READ_ONLY);
                        Message messages[] = sfd.getMessages();
                        System.out.println("number of  messages in " + sfd.getName() + " : " + messages.length);

                        if (folderFilter.trim().toLowerCase().equals(sfd.getName().trim().toLowerCase())) {
                            for (Message m : messages) {
                                proc.processOneMessage(m, email);
                            }
                        } else {
                            System.out.println("ignoring folder because wrong folder name: " + sfd.getName());
                        }

                        System.out.println("processing subfolders of " + sfd.getName());
                        processFolderArray(sfd, email, lastNumInt);
                        sfd.close(false);
                    } catch (Exception x) {
                        System.out.println("could not open folder " + sfd.getName());
                        System.out.println("error  " + x.getMessage());
                    }

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

    private void processFolderArray(IMAPFolder folder, String email, int lastSuccess) {
        System.out.println("in processFolderArray for " + folder.getName());

        try {
            System.out.println("opening the folder");
            if (!folder.isOpen()) {
                folder.open(Folder.READ_ONLY);
            }

            System.out.println("castinng folder;  " + folder.getFullName());

            System.out.println("castinng folder list length  " + folder.list().length);

            if (folder.list().length > 0) {

                IMAPFolder[] f = (IMAPFolder[]) folder.list();
                System.out.println("casting OK ");

                if (f.length > 0) {
                    for (IMAPFolder sfd : f) {

                        if (!sfd.isOpen()) {
                            sfd.open(Folder.READ_ONLY);
                        }

                        Message messages[] = sfd.getMessages();
                        System.out.println("number of  messages in " + sfd.getName() + " : " + messages.length);
                        System.out.println("processing messages of " + sfd.getName());

                        if (folderFilter.trim().toLowerCase().equals(sfd.getName().trim().toLowerCase())) {
                            for (Message m : messages) {

                                proc.processOneMessage(m, email);
                            }
                        } else {
                            System.out.println("ignoring folder because wrong folder name: " + sfd.getName());
                        }

                        System.out.println("processing subfolders of " + sfd.getName());
                        processFolderArray(sfd, email, lastSuccess);

                        if (sfd.isOpen()) {
                            sfd.close(false);
                        }

                    }
                }
            }

            if (folder.isOpen()) {
                folder.close(false);
            }

        } catch (Exception x) {
            System.out.println("error: " + x.getMessage());
            x.printStackTrace();

        }

    }

}
