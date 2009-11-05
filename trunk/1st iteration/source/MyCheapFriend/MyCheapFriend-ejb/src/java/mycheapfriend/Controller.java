
package mycheapfriend;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;


/**
 * Controller is used to handle incoming messages sent by a cell phone.
 * @author Shaoqing Niu
 */
public class Controller{

    private final String[] POSSIBLE_DOMAINS = {"txt.att.net", "cingularme.com","messaging.nextel.com", "messaging.sprintpcs.com", "tmomail.net", "vtext.com"};
    //["airtelkk.com", "alertas.personal.com.ar", "bplmobile.com", "cingularme.com", "clarotorpedo.com.br", "comcel.com.co", "cwemail.com", "email.uscc.net", "emtelworld.net", "fido.ca", "ideasclaro-ca.com", "ivctext.com", "iwspcs.net", "mas.aw", "message.alltel.com", "messaging.nextel.com", "messaging.sprintpcs.com", "mmst5.tracfone.com", "mobile.celloneusa.com", "mobipcs.net", "movistar.com.co", "msg.acsalaska.com", "msg.gci.net", "msg.globalstarusa.com", "msg.iridium.com", "msg.koodomobile.com", "msg.telus.com", "myboostmobile.com", "mymetropcs.com", "nextel.net.ar", "orange.pl", "page.att.net", "pcs.rogers.com", "qwestmp.com", "rek2.com.mx", "slinteractive.com.au", "sms.airtelmontana.com", "sms.co.za", "sms.ctimovil.com.ar", "sms.lmt.lv", "sms.mobitel.lk", "sms.movistar.net.ar", "sms.mymeteor.ie", "sms.sasktel.com", "sms.spicenepal.com", "sms.t-mobile.at", "sms.thumbcellular.com", "sms.tigo.com.co", "sms.vodafone.it", "sms.ycc.ru", "t-mobile.uk.net", "tachyonsms.co.uk", "text.aql.com", "text.mtsmobility.com", "text.plusgsm.pl", "tmomail.net", "tms.suncom.com", "txt.att.net", "txt.bell.ca", "utext.com", "vmobile.ca", "vmobl.com", "voda.co.za", "vtext.com"]
    EmailSend emailSend;
    InitialContext context;
    UserObjFacadeRemote userObjFacade;

    /**
     * handle(TextMessage) is used to handle a parsed message.
     * @param tm
     */
    public void handle(TextMessage tm) {
        log("new message from " + tm.getFrom());
        log("message: " + ((EmailInfo)tm).getContent());
        try {
            emailSend = new EmailSend();
            context = new InitialContext();
            userObjFacade = (UserObjFacadeRemote) context.lookup("ejb.UserObjFacade");
        } catch (NamingException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            emailSend.setAll("", "Internal error, try back later (SORRY!!)", tm.getFrom());
            return;
        }

        //handle error
        if(tm.getType() == TextMessage.ERROR)
        {
            log("error: " + getErrorMessage(tm.getErrorType()));
            emailSend.setAll("", getErrorMessage(tm.getErrorType()), tm.getFrom());
            emailSend.send();
            return;
        }

        //initializing common vars
        UserObj user = userObjFacade.find(tm.getPhone());
        boolean newUser = user == null;
        boolean userAuthenticated = !newUser && user.getActive() && tm.getPassword() != null && user.getPassword().equalsIgnoreCase(tm.getPassword());
        log("sender phone:" + tm.getPhone());
        if(newUser)
        {
            log("sender is a new user");
            user = new UserObj(tm.getPhone(), tm.getDomain()); //create inactive user
            userObjFacade.create(user);
        }
        else
        {
            if(user.getEmail_domain() == null)
            {
                user.setEmail_domain(tm.getDomain());
                userObjFacade.edit(user);
            }
            if(user.getUnsubscribe())
            {
                log("Sender is unsubscribed");
                if(tm.getType() == TextMessage.RESUBSCRIBE)
                    resubscribe(user);
                return;
            }
        }


        switch(tm.getType()){

            case TextMessage.NEW_ACCOUNT:
            case TextMessage.RESET_PASS:
                newAccountOrReset(user, newUser);
                return;
            case TextMessage.UNSUBSCRIBE:
                unsubscribe(user);
                return;
            case TextMessage.RESUBSCRIBE:
                resubscribe(user);
                return;
        }

        //make sure user is authorized for other types...
        
        if(newUser) {
            replyUnregisteredUser(tm.getFrom());
            return;
        }

        if(tm.getType() == TextMessage.ACCEPT_BILL)
        {
            acceptBill(user);
            return;
        }

        else if(!userAuthenticated) {
            replyWrongPasswordUser(user.getPassword(), tm.getFrom());
            return;
        }

        
        switch(tm.getType()){
            case TextMessage.NEW_FRIEND:
                newFriend(user, tm.getFriendPhone(), tm.getFriendNick());
                return;
            case TextMessage.NEW_BILL:
                newBill(user, tm);
                return;

            case TextMessage.REPORT_BILLS:
                reportBills(user);
                return;
        }
    }

    private void newAccountOrReset(UserObj user, boolean newUser)
    {
        log("new account or reset");
        user.setActive(true);
        user.setPassword(PasswordGenerator.generatePassword());
        user.setUnsubscribe(false);
        userObjFacade.edit(user);
        log("sending password to " + user.getEmail());
        if(newUser)
            this.replyNewUser(user.getPassword(), user.getEmail());
        else
            this.replyResetPassword(user.getPassword(), user.getEmail());

    }

    private void unsubscribe(UserObj user)
    {
        log("unsubscribe");
        user.setUnsubscribe(Boolean.TRUE);
        userObjFacade.edit(user);
        this.replyUnsubscribe(user.getEmail());
    }

    private void resubscribe(UserObj user)
    {
        log("resubscribe");
        user.setUnsubscribe(Boolean.FALSE);
        userObjFacade.edit(user);
        this.replyResubscribe(user.getEmail());
    }

    private void newFriend(UserObj user, long friendPhone, String friendNick)
    {
        log("new friend");
        String email = user.getEmail();
        //only one way
        setFriendNickName(friendPhone, friendNick, user);
        this.replyAddFriend(friendPhone, friendNick, email);
    }

    private void newBill(UserObj user, TextMessage tm)
    {
        log("new bill");
        ArrayList<UserObj> toBeBilled = new ArrayList<UserObj>();
        for(int i = 0; i < tm.getNumBills(); i++){
            UserObj friendUser = this.identifierToUserObj(user, tm.getBillFriend(i));
            //a nickname is used but the user doesn't have a friend with the nickname
            if(friendUser == null) {
                this.replyIdentifierWrong(tm.getBillFriend(i), user.getEmail());
                return;
            }
            else
                toBeBilled.add(friendUser);

        }
        for(int i = 0; i < tm.getNumBills(); i++) {
            UserObj friendUser = toBeBilled.get(i);
            user.loanTo(friendUser, tm.getBillMoney(i));
            userObjFacade.edit(user);

            if(friendUser.getUnsubscribe()){
                this.replyFriendUnsubscribed(readableFriend(user, friendUser), user.getEmail());
            }
            else {
                this.replyBillRequest(tm.getBillMoney(i), readableFriend(user, friendUser), user.getEmail(), 0);

                ArrayList<String> emailsToTry = new ArrayList<String>();
                String newAddress;
                if((newAddress = friendUser.getEmail()) != null)
                    emailsToTry.add(newAddress);
                else
                    for(String domain:POSSIBLE_DOMAINS)
                        emailsToTry.add(friendUser.getPhone() + "@" + domain);
                for(String address: emailsToTry)
                    this.replyBillRequest(tm.getBillMoney(i), readableFriend(friendUser, user) , address, 1);
            }
        }
    }

    private void reportBills(UserObj user)
    {
        log("report bills");
        HashMap<UserObj, Long> nets = new HashMap<UserObj, Long>();
        HashMap<UserObj, String> nicknames = new HashMap<UserObj, String>();

        String text = "";
        String newline = "\r\n";
        List<Bill> assets = user.getAssets();
        for(Bill asset: assets)
        {
            if(asset.getApproved() && !asset.getPaid())
            {
                UserObj borrower = asset.getBorrower();
                long amount = asset.getAmount();
                Long oldAmount = nets.get(borrower);
                long oldVal = (oldAmount == null)? 0 : oldAmount.longValue();
                Long newAmount = new Long(oldVal + amount);
                nets.put(borrower, newAmount);
            }
        }

        List<Bill> debts = user.getDebts();
        for(Bill debt: debts)
        {
            if(debt.getApproved() && !debt.getPaid())
            {
                UserObj borrower = debt.getBorrower();
                long amount = debt.getAmount();
                Long oldAmount = nets.get(borrower);
                long oldVal = (oldAmount == null)? 0 : oldAmount.longValue();
                Long newAmount = new Long(oldVal - amount);
                nets.put(borrower, newAmount);
            }
        }
        ArrayList<UserObj> owesUser = new ArrayList<UserObj>();
        ArrayList<UserObj> userOwes = new ArrayList<UserObj>();
        Set<UserObj> users = nets.keySet();

        List<Friend> friends = user.getFriends();


        //populate nicknames hash
        for(Friend f : friends)
        {
            UserObj friend = f.getFriend();
            if(nets.containsKey(friend))
                nicknames.put(friend, f.getNickname());
        }

        for(UserObj u : users)
        {
            if(nets.get(u).longValue() > 0)
                owesUser.add(u);
            else if (nets.get(u).longValue() < 0)
                userOwes.add(u);

        }

        if(!owesUser.isEmpty())
        {
            text += "They owe you: ";
            for(UserObj u : owesUser)
            {

                String money = readableAmount( nets.get(u).longValue() );

                String identifier = readableFriend(nicknames.get(u), u.getPhone());
                text += identifier + "-$" + money +", ";
            }
        }
        if(!userOwes.isEmpty())
        {
            if(text.length() > 0)
                text += ". "+ newline;

            text += "You owe: ";
            for(UserObj u : userOwes)
            {

                String money = readableAmount( nets.get(u).longValue() );
                String identifier = readableFriend(nicknames.get(u), u.getPhone());

                text += identifier + "-$" + money +", ";
            }
        }
        if(userOwes.isEmpty() && owesUser.isEmpty())
            text = "no current debts or loans";

        replyReport(text, user.getEmail());
    }

    private void acceptBill(UserObj user)
    {
        log("accept bill");
        List<Bill> debts = user.getDebts();
        Bill most_recent_bill = null;
        Date most_recent = null;
        for(Bill b : debts)
        {
            Date next_date = b.getTimeCreated();
            if( most_recent == null || next_date.after(most_recent))
            {
                most_recent = next_date;
                most_recent_bill = b;
            }

        }

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);
        if(most_recent.after(c.getTime()))
        {
            most_recent_bill.setApproved(true);
            UserObj lender = most_recent_bill.getLender();
            if(lender.getUnsubscribe())
                this.replyFriendUnsubscribed(readableFriend(user, lender), user.getEmail());
            //TODO: settle any other bills
            long recentBillBalance = most_recent_bill.getAmount();
            long newBalance = 0;

            List<Bill> assets = user.getAssets();
            for(Bill b : assets)
                if(b.getApproved() && !b.getPaid() && b.getBorrower().equals(lender))
                {
                    long newAmount = b.getAmount();
                    if(recentBillBalance >= newAmount) //if he loaned me $x, any previous bills up to $x are settled
                    {
                        b.setPaid(true);
                        recentBillBalance -= newAmount;
                    }

                    newBalance += newAmount;
                }
                        
            for(Bill b : debts)
                if(b.getApproved() && !b.getPaid() && b.getLender().equals(lender))
                    newBalance -= b.getAmount();

            userObjFacade.edit(user);

            replyAcceptBill(most_recent_bill, newBalance);
        }
        else
        {
            replyBillTooOld(user); //perhaps also allow debtor to refersh bill...
        }
    }

    private String readableAmount(long val)
    {
        String money;
        if(val % 100 == 0)
            money = ""+ (val / 100);
        else
            money = (new BigDecimal(BigInteger.valueOf(val), 2)).toPlainString();

        return money;
    }
    private String readableFriend(UserObj user, UserObj friend)
    {
        return readableFriend(user.getNickname(friend), friend.getPhone());
    }

    private String readableFriend(String nickname, long phone)
    {
        if(nickname == null)
            return "@ "+phone;
        else
            return nickname;
    }

    private void setFriendNickName(long phone, String nickname, UserObj user){

            UserObj friendUser = userObjFacade.find(phone);
            Boolean found = Boolean.FALSE;

            if(friendUser == null){

                friendUser = new UserObj(phone);
                user.addFriend(friendUser, nickname);

                userObjFacade.create(friendUser);
                userObjFacade.edit(user);
            }
            else{

                Friend newFriend = new Friend();
                newFriend.setParent(user);
                newFriend.setFriend(friendUser);

                for(Friend f : user.getFriends()){
                    if(f.getFriend().getPhone() == phone){
                        f.setNickname(nickname);
                        found = Boolean.TRUE;
                        break;
                    }
                }
                if(found == Boolean.FALSE){
                    newFriend.setNickname(nickname);
                    user.getFriends().add(newFriend);
                }

                userObjFacade.edit(user);

            }
    }

    private void replyUnregisteredUser(String address){
        String text = "You are not registered yet. ";
        text += "Please text "+EmailInfo.NEW_ACCOUNT_ADDR+"@mycheapfriend.com to register.";
        replyReport(text, address);
    }

    private void replyWrongPasswordUser(String password, String address){
        String text = "Please text your own address. ";
        text += "Your unique address is <" + password+"@mycheapfriend.com>. ";
        text += "Please add it to your address book.";
        replyReport(text, address);
    }

    private void replyNewUser(String password, String address){
        String text = "Welcome to MyCheapFriend! ";
        text += "Your unique address is <" + password+"@mycheapfriend.com>. ";
        text += "Please add it to your address book.";
        replyReport(text, address);
    }

    private void replyResetPassword(String password, String address){
        String text = "Your new address is <" + password+"@mycheapfriend.com>. ";
        text += "Please add it to your address book.";
        replyReport(text, address);
    }

    private void replyUnsubscribe(String address) {

        String text = "You have unsubscribed from mycheapfriend now. ";
        replyReport( text, address);
    }

    private void replyResubscribe(String address) {

        String text = "You have subscribed from mycheapfriend now. ";
        replyReport( text, address);
    }

    private void replyAddFriend(long phone, String nick, String address){
        String text = "Your have set your friend "+phone+"'s nickname to "+nick;
        replyReport(text, address);
    }

    private void replyIdentifierWrong(Object id, String address){

        String text = "Your don't have a friend with identifier"+id;
        replyReport(text, address);
    }

    private void replyFriendUnsubscribed(String id, String address)
    {
        replyReport("Your friend " + id + "has unsubscribed from mycheapfriend", address);
    }

    private void replyBillRequest(long money, String id, String address, int type){
        String text = "";
        if(type == 0){
            text = "You have sent a bill of " + money + " to your friend " + id + ".";
        }
        else if(type == 1){
            text = "Your friend " + id + " says you owe " + money + "to them.";
        }
        else ;
        replyReport( text, address);
    }

    private void replyReport(String message, String address){
        emailSend.setAll("", message, address);
        emailSend.send();
    }


    //TODO: INCOMPLETE!! need to deal with accepted bills / balances
    private void replyAcceptBill(Bill b, long newBalance)
    {
        long amount = b.getAmount();
        UserObj borrower = b.getBorrower();
        UserObj lender = b.getLender();

        String readableAmount = readableAmount(amount);
        String readableNewBalance = readableAmount(newBalance);
        String borrowerText = "You have confirmed that you paid your friend " + readableFriend(borrower, lender) + " $" + readableAmount + ".";
        String lenderText = "Your friend " + readableFriend(lender, borrower) + " has confirmed that you paid them " + readableAmount +".";

        if(newBalance > 0)
        {
            borrowerText += "  They now owe you $" + readableNewBalance +".";
            lenderText += "  You now owe them $" + readableNewBalance +".";
        }
        else if(newBalance < 0)
        {
            lenderText += "  They now owe you $" + readableNewBalance+".";
            borrowerText += "  You now owe them $" + readableNewBalance+".";
        }
        else //new_balance == 0
        {
            String suffix = " You guys have now settled all of your debts.";
            lenderText +=  suffix;
            borrowerText += suffix;
        }

        this.replyReport(lenderText, lender.getEmail());
        this.replyReport(borrowerText, borrower.getEmail());
    }

    //TODO: INCOMPLETE!! NEED TO DEAL WITH bills that are more than a day old
    private void replyBillTooOld(UserObj u)
    {
        replyReport("You tried to accept a bill, but you don't have bills from the last 24 hours to accept", u.getEmail());
    }

    /**
     * getErrorMessage(int) is used to handle syntax error in a message.
     * @param errorType
     * @return
     */
    private String getErrorMessage(int errorType){

        switch(errorType) {
            case TextMessage.INVALID_SENDER:
                return "Please use a cell phone to send the message.";
            case TextMessage.LEXICAL_ERROR:
            case TextMessage.SYNTAX_ERROR:
                return "Please check the format of your message.";
            default: //undefined error
                return "Please check your message.";
        }
    }

    private UserObj identifierToUserObj(UserObj user, Object id)
    {
        if(id instanceof String)
            return user.getFriend((String)id);
        else
        {
            long phone = ((Long)id).longValue();
            UserObj friendUser = userObjFacade.find(phone);
            if(friendUser == null)
            {
                friendUser = new UserObj(phone);
                userObjFacade.create(friendUser);
            }
            return user;
        }
    }

    private void log(String message)
    {
        System.out.println(message);
    }

}