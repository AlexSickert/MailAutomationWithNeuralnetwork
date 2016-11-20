/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package brainextensionmailpull;

import com.sun.mail.imap.IMAPFolder;
import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;

/**
 *
 * @author admin
 */
public class ImapStratoJunk {

    public void loopThroughFolders(String email, String pass) {

        System.out.println("email = " + email);
        System.out.println("pass = " + pass);

//        IMAPFolder folder = null;
//        Folder folder2 = null;

        Folder[] folders = null;
        Folder[] subFolders = null;

        Store store = null;

//        long timeNow;
//        long timeLast;
//        long messageCounter;
//        long timeDiff;
//        long timeTotal;
//
//        int lastNumInt = 0;

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

//            IMAPFolder[] f = (IMAPFolder[]) store.getDefaultFolder().list();

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
            
            System.out.println("folder processed, now we close the folder and clean them up");

            foTarget.close(true);
            foSource.close(true);
            
            System.out.println("all done in loopThroughFolders"); 

        } catch (Exception e) {
            System.out.println("error in ImapStratoJunk  loopThroughFolders(String email, String pass)");
            e.printStackTrace();
        }
    }
}
