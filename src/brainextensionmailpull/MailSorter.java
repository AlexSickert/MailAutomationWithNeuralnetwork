/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package brainextensionmailpull;

import com.sun.mail.imap.IMAPFolder;
import java.util.ArrayList;
import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Message;

/**
 *
 * @author admin
 */
public class MailSorter {

    public void testSort(IMAPFolder sourceFolder, IMAPFolder targetFolder) {

        String from;
        String to;
        String subject;
        int coutner = 0;
        //Message[] msgArr;
        ArrayList<Message> arl = new ArrayList<Message>();

        JunkStrings js = new JunkStrings();
        ArrayList<String> tstsArl = js.getStrings();

        try {
            Message foSourceCount[] = sourceFolder.getMessages();

            for (Message m : foSourceCount) {

                coutner += 1;

                from = getFromString(m);
                to = getToString(m);
                subject = m.getSubject();

                //System.out.println(subject);
                //String tst = "Fryday Afterwork at Tequila House Terrace";
                if (subject != null) {
                    for (String tst : tstsArl) {
                        //if(to.toLowerCase().contains("brain@") || to.toLowerCase().contains("bex@")){

                        if (subject.toLowerCase().contains(tst.toLowerCase())) {

                            System.out.println("Junk mail found in sourceFolder. subject: " + subject);

                            arl.add(m);

                            //msgArr = (Message) arl.toArray();
                            Message[] msgArr = new Message[arl.size()];
                            arl.toArray(msgArr);

                            sourceFolder.copyMessages(msgArr, targetFolder);

                            m.setFlag(Flags.Flag.DELETED, true);

                            break;
                        } else {
                            //System.out.println("mail ok ... " + coutner + " ... " + subject);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // check mails in target folder
        try {
            Message foTargetCount[] = targetFolder.getMessages();

            for (Message m : foTargetCount) {

                subject = m.getSubject();
                System.out.println("mail found in targetFolder. subject: " + subject);

            }
        } catch (Exception e) {
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

}
