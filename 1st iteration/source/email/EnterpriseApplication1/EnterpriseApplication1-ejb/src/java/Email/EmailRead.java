package Email;
/**
 *
 * @author David
 */


public class EmailRead {

    /** Creates a new instance of Main */
    public static void read() {
         try {

            GmailUtilities gmail = new GmailUtilities();
            gmail.setUserPass("robot@mycheapfriend.com", "K86924");
            gmail.connect();
            gmail.openFolder("INBOX");
            int totalMessages = gmail.getMessageCount();
            int newMessages = gmail.getNewMessageCount();
            //gmail.printAllMessages();
            //set all information...
            gmail.setEmailInfo();
            System.out.println(gmail.getInfo().size());
            gmail.closeFolder();

        } catch(Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }


    }

    /* test program for reading emails. */
    public static void main(String[] args) {
        EmailRead.read();



    }

}
