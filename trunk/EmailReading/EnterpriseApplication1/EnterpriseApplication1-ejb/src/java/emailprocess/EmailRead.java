package emailprocess;



public class EmailRead {

    /** Creates a new instance of Main */
    public static void read() {
         try {

            GmailUtilities gmail = new GmailUtilities();
            gmail.setUserPass("robot@mycheapfriend.com", "K86924");
            gmail.connect();
            gmail.openFolder("INBOX");
            //gmail.getEmail();
            int totalMessages = gmail.getMessageCount();
            int newMessages = gmail.getNewMessageCount();

            System.out.println("Total messages = " + totalMessages);
            System.out.println("New messages = " + newMessages);
            System.out.println("-------------------------------");

//Uncomment the below line to print the body of the message. Remember it will eat-up your bandwidth if you have 100's of messages.
            
            gmail.printAllMessages();
            //set all information...
            gmail.setEmailInfo();
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
