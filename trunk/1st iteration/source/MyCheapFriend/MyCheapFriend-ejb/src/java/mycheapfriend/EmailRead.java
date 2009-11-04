package mycheapfriend;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David
 */


public class EmailRead {
    /** Creates a new instance of Main */
    public static ArrayList<EmailInfo> read() throws Exception {
            GmailUtilities gmail= new GmailUtilities();
            gmail.setUserPass("robot@mycheapfriend.com", "K86924");
            gmail.connect();
            gmail.openFolder("INBOX");
            //set all information...
            gmail.setEmailInfo();
            System.out.println("# of new emails is " + gmail.info.size());
            System.out.println(gmail.info.get(0).getTo());
            /* for(emailinfo a: gmail.getInfo())
                System.out.println(a.getContent()); */
            gmail.closeFolder();
            gmail.disconnect();
            return gmail.info;
    }

    /* test program for reading emails. */
    public static void main(String[] args) {
        try {
            EmailRead.read();
        } catch (Exception ex) {
            Logger.getLogger(EmailRead.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
