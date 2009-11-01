/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mycheapfriend;


/**
 * Controller is used to handle incoming messages sent by a cell phone.
 * @author Shaoqing Niu
 */
public class Controller{

    EmailSend emailSend;
    UserObjFacade userObjFacade;

    /**
     * handle(TextMessage) is used to handle a parsed message.
     * @param tm
     */
    public void handle(TextMessage tm) {

        if(tm.getErrorType() != TextMessage.NO_ERROR) {
            emailSend.setAll("", getErrorMessage(tm.getErrorType()), tm.getFrom());
            emailSend.send();
            return;
        }
        else
            handleMessage(tm);
    }

    /**
     * getErrorMessage(int) is used to handle syntax error in a message.
     * @param errorType
     * @return
     */
    public String getErrorMessage(int errorType){

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

    /**
     * handleMessage(TextMessage) is used to handle a message free from syntax error
     * @param tm
     */
    public void handleMessage(TextMessage tm)
    {
        UserObj user;
        String text;
        
        switch(tm.getType()){

            case TextMessage.NEW_ACCOUNT:
                //the message is sent to EmailInfo.NEW_ACCOUNT_ADDR
                user = userObjFacade.find(tm.getPhone());
                if(user == null) {

                    user = new UserObj(tm.getPhone(),PasswordGenerator.generatePassword()); //set active
                    userObjFacade.create(user);
                }
                else if(user.getActive() == Boolean.FALSE){

                    user.setPassword(PasswordGenerator.generatePassword());
                    user.setActive(Boolean.TRUE);
                    userObjFacade.edit(user);
                }
                else {
                //else it's already an active user, we don't modify it
                }

                this.replyNewUser(user.getPassword(), tm.getFrom());
                return;

            case TextMessage.RESET_PASS:
                //the message is sent to EmailInfo.RERSET_PASS_ADDR
                user = userObjFacade.find(tm.getPhone());
                if(user == null) {

                    replyUnregisteredUser(tm.getFrom());
                }
                else if(user.getActive() == Boolean.FALSE){

                    replyInactiveUser(tm.getFrom());
                }
                else {

                    user.setPassword(PasswordGenerator.generatePassword());
                    userObjFacade.edit(user);
                    this.replyResetPassword(user.getPassword(), tm.getFrom());
                }
                return;

            case TextMessage.UNSUBSCRIBE:
                //the message is sent to EmailInfo.UNSUBSCRIBE_ADDR
                user = userObjFacade.find(tm.getPhone());
                if(user == null) {

                    replyUnregisteredUser(tm.getFrom());
                }
                /*else if(user.getActive() == Boolean.FALSE){
                    //an inactive user could unsubscribe
                    replyInactiveUser(tm.getFrom());
                }*/
                else {

                    user.setUnsubscribe(Boolean.TRUE);
                    userObjFacade.edit(user);
                    this.replyUnsubscribe(tm.getFrom());
                }
                return;

            case TextMessage.RESUBSCRIBE:
                //the message is sent to EmailInfo.RESUBSCRIBE_ADDR
                user = userObjFacade.find(tm.getPhone());
                if(user == null) {

                    replyUnregisteredUser(tm.getFrom());
                }
                else if(user.getActive() == Boolean.FALSE){

                    replyInactiveUser(tm.getFrom());
                }
                else {

                    user.setUnsubscribe(Boolean.FALSE);
                    userObjFacade.edit(user);

                    this.replyResubscribe(user.getPassword(), tm.getFrom());
                }
                return;

            case TextMessage.NEW_FRIEND:
                //the message is sent to XXX@mycheapfriend
                //the message contains a phone number and a nickname
                user = userObjFacade.find(tm.getPhone());
                if(user == null) {

                    replyUnregisteredUser(tm.getFrom());
                }
                else if(user.getActive() == Boolean.FALSE){

                    replyInactiveUser(tm.getFrom());
                }
                else if(!tm.getPassword().equalsIgnoreCase(user.getPassword())){

                    replyWrongPasswordUser(user.getPassword(), tm.getFrom());
                }
                else{
                    //mutal friend
                    setFriendNickName(tm.getFriendPhone(), tm.getFriendNick(), user);
                    setFriendNickName(user.getPhone(),""+user.getPhone(),userObjFacade.find(tm.getFriendPhone()));
                    this.replyAddFriend(tm.getFriendPhone(), tm.getFriendNick(), tm.getFrom());
                }
                return;

            case TextMessage.NEW_BILL:
                //the message is sent to XXX@mycheapfriend
                user = userObjFacade.find(tm.getPhone());
                if(user == null) {

                    replyUnregisteredUser(tm.getFrom());
                }
                else if(user.getActive() == Boolean.FALSE){

                    replyInactiveUser(tm.getFrom());
                }
                else if(!tm.getPassword().equalsIgnoreCase(user.getPassword())){

                    replyWrongPasswordUser(user.getPassword(), tm.getFrom());
                }
                else{
                    for(int i = 0; i < tm.getNumBills(); i++){
                        //a nickname is used but the user doesn't have a friend with the nickname
                        if(user.getFriendId(tm.getBillFriend(i)) == 0){
                            this.replyIdentifierWrong(tm.getBillFriend(i), tm.getFrom());
                            return;
                        }
                    }
                    for(int i = 0; i < tm.getNumBills(); i++) {

                        Long friendPhone = user.getFriendId(tm.getBillFriend(i));
                        UserObj newUser = userObjFacade.find(friendPhone);

                        if(user.hasFriend(friendPhone)) {
                            //don't change if user already has this friend
                        }
                        else{
                            //add as a friend
                            if(newUser == null) {
                                newUser = new UserObj(friendPhone);
                                userObjFacade.create(newUser);
                            }
                            newUser.addFriend(user, ""+user.getPhone());
                            user.addFriend(newUser, ""+friendPhone);
                        }
                        user.addLoan(newUser, tm.getBillMoney(i));
                        newUser.addDebt(user, tm.getBillMoney(i));
                        userObjFacade.edit(user);
                        userObjFacade.edit(newUser);

                        if(newUser.getUnsubscribe()){
                            this.replyBillRequest(tm.getBillMoney(i), tm.getBillFriend(i), tm.getFrom(), -1);
                        }
                        else {
                            this.replyBillRequest(tm.getBillMoney(i), tm.getBillFriend(i), tm.getFrom(), 0);
                            this.replyBillRequest(tm.getBillMoney(i), tm.getPhone(), newUser.getEmail_domain(), 1);
                        }
                    }
                }
                return;

            case TextMessage.REPORT_BILLS:
                user = userObjFacade.find(tm.getPhone());
                if(user == null) {

                    replyUnregisteredUser(tm.getFrom());
                }
                else if(user.getActive() == Boolean.FALSE){

                    replyInactiveUser(tm.getFrom());
                }
                else if(!tm.getPassword().equalsIgnoreCase(user.getPassword())){

                    replyWrongPasswordUser(user.getPassword(), tm.getFrom());
                }
                else{
                    if( (user.getFriends()).isEmpty()){
                        this.replyNoFriend(tm.getFrom());
                    }
                    else if( ((user.getLoans()).isEmpty()) && ((user.getDebts()).isEmpty())){
                        this.replyNoBill(tm.getFrom());
                    }
                    else{
                        long each_loan ;
                        long each_debt ;
                        long final_loan ;
                        Boolean bills_exist = Boolean.FALSE;
                        text = "";
                        String newline = System.getProperty("line.separator");
                        //the system is not the user's system

                        for(Friend f : user.getFriends()){
                            each_loan = 0;
                            each_debt = 0;
                            for(Bill loan : user.getLoans()){
                                each_loan += loan.getAmount();
                            }
                            for(Bill debt : user.getDebts()){
                                each_debt += debt.getAmount();
                            }

                            final_loan = each_loan - each_debt;

                            if(final_loan > 0){
                                bills_exist = Boolean.TRUE;
                                text.concat("Your friend " + f.getNickname() + " owes you " + final_loan + " dollars" + newline);

                            }
                            else if(final_loan < 0){
                                bills_exist = Boolean.TRUE;
                                text.concat("You owe " + final_loan + " dollars to your friend " + f.getNickname() + newline);
                            }

                        }

                        if(!bills_exist){
                            text = "You are even with all your friends.";
                        }
                        this.replyReport(text, tm.getFrom());
                    }

                }
                return;

            default: break;
        }
    }
    public void setFriendNickName(long phone, String nickname, UserObj user){

            UserObj newUser = userObjFacade.find(phone);
            Boolean found = Boolean.FALSE;

            if(newUser == null){

                newUser = new UserObj(phone);
                user.addFriend(newUser, nickname);

                userObjFacade.create(newUser);
                userObjFacade.edit(user);
            }
            else{

                Friend newFriend = new Friend();
                newFriend.setParent(user);
                newFriend.setFriend(newUser);

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

    public void replyUnregisteredUser(String address){
        String text = "You are not registered yet. ";
        text += "Please text "+EmailInfo.NEW_ACCOUNT_ADDR+"@mycheapfriend.com to register.";
        emailSend.setAll("", text, address);
        emailSend.send();
    }

    public void replyInactiveUser(String address){
        String text = "You are not registered yet. ";
        text += "Please text "+EmailInfo.NEW_ACCOUNT_ADDR+"@mycheapfriend.com to register.";
        emailSend.setAll("", text, address);
        emailSend.send();
    }

    public void replyWrongPasswordUser(String password, String address){
        String text = "Please text your own address. ";
        text += "Your unique address is <" + password+"@mycheapfriend.com>. ";
        text += "Please add it to your address book.";
        emailSend.setAll("", text, address);
        emailSend.send();
    }

    public void replyNewUser(String password, String address){
        String text = "Welcome to MyCheapFriend! ";
        text += "Your unique address is <" + password+"@mycheapfriend.com>. ";
        text += "Please add it to your address book.";
        emailSend.setAll("", text, address);
        emailSend.send();
    }

    public void replyResetPassword(String password, String address){
        String text = "Your new address is <" + password+"@mycheapfriend.com>. ";
        text += "Please add it to your address book.";
        emailSend.setAll("", text, address);
        emailSend.send();
    }

    public void replyUnsubscribe(String address) {

        String text = "You have unsubscribed from mycheapfriend now. ";
        emailSend.setAll("", text, address);
        emailSend.send();
    }

    public void replyResubscribe(String password, String address) {

        String text = "You have subscribed from mycheapfriend now. ";
        text = "Your unique address is <" + password+"@mycheapfriend.com>. ";
        text += "Please add it to your address book.";
        emailSend.setAll("", text, address);
        emailSend.send();
    }

    public void replyAddFriend(Long phone, String nick, String address){
        String text = "Your have set your friend "+phone+"'s nickname to "+nick;
        emailSend.setAll("", text, address);
        emailSend.send();
    }

    public void replyIdentifierWrong(Object id, String address){

        String text = "Your don't have a friend with identifier"+id;
        emailSend.setAll("", text, address);
        emailSend.send();
    }

    public void replyBillRequest(Long money, Object id, String address, int type){
        String text = "";
        if(type == -1){
            text = "Your friend " + id + "has unsubscribed from mycheapfriend";
        }
        else if(type == 0){
            text = "You have sent a bill of " + money + " to your friend " + id + ".";
        }
        else if(type == 1){
            text = "Your friend " + id + "requests a bill of " + money + "to you.";
        }
        else ;
        emailSend.setAll("", text, address);
        emailSend.send();
    }

    public void replyNoFriend(String address){
        emailSend.setAll("", "You don't have any friend at this point", address);
        emailSend.send();
    }

    public void replyNoBill(String address){
        emailSend.setAll("", "You don't have any bill at this point", address);
        emailSend.send();
    }

    public void replyReport(String message, String address){
        emailSend.setAll("", message, address);
        emailSend.send();
    }
}