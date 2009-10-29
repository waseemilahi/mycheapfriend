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

    private final static String phoneNumPattern = "\\d{10}|(?:\\D?\\d{3}\\D ?(?:\\d{7}|(?:\\d{3}\\D\\d{4})))";
    private final static String nicknamePattern = "[a-zA-Z]{2,2}[a-zA-Z0-9_-]{1,8}";
    private final static String amountPattern = "\\$?\\d{1,4}(\\.\\d{2})?";

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
        String body = content.trim();
        String[] atoms = body.split("\\s+");

        boolean multiple_friends = false;
        boolean nickname = false;
        
        this.billFriends = new ArrayList<Object>();
        this.billAmounts = new ArrayList<Integer>();

        for(String atom : atoms)
        {
            atom = atom.trim();

            if(atom.matches(nicknamePattern))
                billFriends.add(atom.toLowerCase());
            else if (atom.matches(phoneNumPattern))
                billFriends.add(new Long(atom.replaceAll("\\D", "")));
            else if (atom.matches(amountPattern))
            {
                atom = atom.replaceAll("[^\\d.]", "");
                int ival = 0;
                if(atom.indexOf('.') >= 0)
                {
                    atom = atom.replaceAll("\\.", "");
                    ival = Integer.parseInt(atom);
                }
                else
                {
                    ival = Integer.parseInt(atom) * 100;
                }
                billAmounts.add(new Integer(ival));
            }
            else if(billAmounts.size() <= 1 && atom.equalsIgnoreCase("me"))
            {
                billFriends.add(atom);
                multiple_friends = true;
            }
            else
            {
                this.type = TextMessage.ERROR;
                this.errorType = TextMessage.LEXICAL_ERROR;
                break;
            }
        }

        if(billFriends.size() == 0)
        {
                this.type = TextMessage.ERROR;
                this.errorType = TextMessage.SYNTAX_ERROR;
        }
        else if(billAmounts.size() == 0)
        {
            boolean fail = true;
            if(billFriends.size() == 2)
            {
                Long num  = null;
                String nick = null;
                for(Object o : billFriends)
                    if(o instanceof String)
                        nick = (String) o;
                    else if(o instanceof Long)
                        num = (Long) o;
                if(num != null && nick != null)
                {
                    this.friendNick = nick;
                    this.friendPhone = num.longValue();
                    this.type = TextMessage.NEW_FRIEND;
                }
            }
            if(fail)
            {
                this.type = TextMessage.ERROR;
                this.errorType = TextMessage.SYNTAX_ERROR;
            }
        }
        else if(billAmounts.size() == billFriends.size() )
        {
            if(multiple_friends)
            {
                //"me" not allowed in billFriendssize
                this.type = TextMessage.ERROR;
                this.errorType = TextMessage.SYNTAX_ERROR;
            }
        }
        else if (billAmounts.size() == 1 && billFriends.size() >= 2)
        {
            Integer bill = billAmounts.remove(0);
            int bill_amount = bill.intValue() / billFriends.size();
            int extra = bill.intValue() - (bill_amount * billFriends.size());

            for(int i = 0; i < billFriends.size(); i++)
            {
                int this_bill_amount = bill_amount;
                if(extra > 0)
                {
                    this_bill_amount++;
                    extra--;
                }
                billAmounts.add(new Integer(this_bill_amount));
            }
        }
        else
        {
                this.type = TextMessage.ERROR;
                this.errorType = TextMessage.SYNTAX_ERROR;
        }

        if(this.type != TextMessage.ERROR && this.type != TextMessage.NEW_FRIEND);
            this.type = TextMessage.NEW_BILL;
        
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
