package mycheapfriend;

import java.util.ArrayList;


/**
 *
 * @author David
 */

class EmailInfo implements TextMessage{

    private final static String NEW_ACCOUNT_ADDR = "new_account";
    private final static String RERSET_PASS_ADDR = "reset_pass";
    private final static String UNSUBSCRIBE_ADDR = "unsubscribe";
    private final static String RESUBSCRIBE_ADDR = "resubscribe";
    private final static String VALID_PASS_REGEX = "[0-9a-z]{6,6}";

    private String from,to,subject,date,content;

    private int type, errorType;

    private long phone, friendPhone;

    private String domain, password, friendNick;

    private ArrayList<Object> billFriends;

   private ArrayList<Integer> billAmounts;

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public String getFrom() {
        return from;
    }

    public String getSubject() {
        return subject;
    }

    public String getTo() {
        return to;
    }

    public void setContent(String content) {
        this.content = content;
        
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setFrom(String from) {
        this.from = from;
        int atLocation = from.indexOf('@');
        String parsed_phone = from.substring(0, atLocation).replace("\\D", "");
        try{
            this.phone = Long.parseLong(parsed_phone);
            this.domain = from.substring(atLocation+1).toLowerCase();
        } catch( Exception e)
        {
            this.type = TextMessage.ERROR;
            this.errorType = TextMessage.INVALID_SENDER;
        }
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setTo(String to) {
        this.to = to;
        String parsed_to = to.substring(0, to.indexOf('@')).toLowerCase();

        if(parsed_to.equals(EmailInfo.NEW_ACCOUNT_ADDR))
            this.type = TextMessage.NEW_ACCOUNT;
        else if(parsed_to.equals(EmailInfo.RERSET_PASS_ADDR))
            this.type = TextMessage.RESET_PASS;
        else if(parsed_to.equals(EmailInfo.UNSUBSCRIBE_ADDR))
            this.type = TextMessage.UNSUBSCRIBE;
        else if(parsed_to.equals(EmailInfo.RESUBSCRIBE_ADDR))
            this.type = TextMessage.RESUBSCRIBE;
        else if(!parsed_to.matches(EmailInfo.VALID_PASS_REGEX))
        {
            this.type = TextMessage.ERROR;
            this.errorType = TextMessage.UNKNOWN_TYPE;
        }
    }
    
    public int getType() {
        return this.type;
    }

    public int getErrorType() {
        return this.errorType;
    }

    public long getPhone() {
        return this.phone;
    }

    public String getDomain() {
        return this.domain;
    }

    public String getPassword() {
        return this.password;
    }

    public long getFriendPhone() {
        return this.friendPhone;
    }

    public String getFriendNick() {
        return this.friendNick;
    }

    public int getNumBills() {
        if(billAmounts != null)
            return this.billAmounts.size();
        else
            return 0;
    }

    public Object getBillFriend(int index) {
        if(billFriends != null && index < getNumBills())
            return billFriends.get(index);
        else
            return null;
    }

    public long getBillMoney(int index) {
        if(billAmounts != null && index < getNumBills())
            return billAmounts.get(index).longValue();
        else
            return 0;
    }
}
