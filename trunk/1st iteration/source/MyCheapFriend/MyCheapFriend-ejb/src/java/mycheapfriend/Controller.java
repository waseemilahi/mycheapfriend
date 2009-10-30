/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mycheapfriend;

import java.util.*;
/**
 *
 * @author Shaoqing Niu
 */
public class Controller{

    EmailSend emailSend;
    UserObjFacade userObjFacade;

    public void handle(TextMessage tm) {
        if(tm.getErrorType() != TextMessage.NO_ERROR) {
            emailSend.setAll("", getErrorMessage(tm.getErrorType()), tm.getFrom());
            emailSend.send();
            return;
        }
        else
            handleMessage(tm);
    }

    public String getErrorMessage(int errorType){ //Only for syntax Error
        switch(errorType) {
            case TextMessage.INVALID_SENDER:
                return "Please use a cell phone to send the message.";
            case TextMessage.LEXICAL_ERROR:
            case TextMessage.SYNTAX_ERROR:
                return "Please check the format of your message.";
            default:
                return "Undefined Error";
        }
    }

    public void handleMessage(TextMessage tm)
    {
        UserObj user;
        String text;
        List<Long> friendPhones;
        
        switch(tm.getType()){
            case TextMessage.NEW_ACCOUNT:
                user = userObjFacade.find(tm.getPhone());        //define =    //find by phone
                if(user == null ) {
                    user = new UserObj(tm.getPhone(),PasswordGenerator.generatePassword()); //add a constructor by phone #
                    userObjFacade.create(user);
                }
                else if( (user != null) && (user.getPassword() == null) ){
                    user.setPassword(PasswordGenerator.generatePassword());
                    user.setActive(Boolean.TRUE);
                }
                text = "Welcome to MyCheapFriend! Your pass is"+user.getPassword(); //treat all users as new users, but only create account for really new users
                emailSend.setAll("", text, tm.getFrom());
                emailSend.send();
                return;
            case TextMessage.RESET_PASS:
                user = userObjFacade.find(tm.getPhone());
                if(user == null) {
                    text = "You are not a user! Please register by ...";
                    emailSend.setAll("", text, tm.getFrom());
                    emailSend.send();
                }
                else {
                    user.setPassword(PasswordGenerator.generatePassword());
                    userObjFacade.edit(user);
                    //there should be a function to generate password randomly
                    text = "Your new password is"+user.getPassword();
                    emailSend.setAll("", text, tm.getFrom());
                    emailSend.send();
                }
                return;
            case TextMessage.UNSUBSCRIBE:
                user = userObjFacade.find(tm.getPhone());
                if(user == null) {
                    text = "You are not a user! Please register by ...";
                    emailSend.setAll("", text, tm.getFrom());
                    emailSend.send();
                }
                else {
                    user.setUnsubscribe(Boolean.TRUE);
                    userObjFacade.edit(user);
                    text = "You have unsubscribed from mycheapfriend.";
                    emailSend.setAll("", text, tm.getFrom());
                    emailSend.send();
                }
                return;
            case TextMessage.RESUBSCRIBE:
                user = userObjFacade.find(tm.getPhone());
                if(user == null) {
                    text = "You are not a user! Please register by ...";
                    emailSend.setAll("", text, tm.getFrom());
                    emailSend.send();
                }
                else {
                    user.setUnsubscribe(Boolean.FALSE);
                    userObjFacade.edit(user);
                    text = "You have resubscribed now";
                    emailSend.setAll("", text, tm.getFrom());
                    emailSend.send();
                }
                return;
            case TextMessage.NEW_FRIEND:
                user = userObjFacade.find(tm.getPhone());
                if(user == null) {
                    text = "You are not a user! Please register by ...";
                    emailSend.setAll("", text, tm.getFrom());
                    emailSend.send();
                }
                else if(!tm.getPassword().equalsIgnoreCase(user.getPassword())){
                    text = "Your password is"+user.getPassword()+". Please send email to ***";
                    emailSend.setAll("", text, tm.getFrom());
                    emailSend.send();
                }
                else{                 
                    setFriendNickName(tm.getFriendPhone(), tm.getFriendNick(),user); //add new function                   
                    text = "Your have set your friend "+tm.getFriendPhone()+"'s nickname to "+tm.getFriendNick();
                    emailSend.setAll("", text, tm.getFrom());
                    emailSend.send();
                }
                return;
            case TextMessage.NEW_BILL:
                user = userObjFacade.find(tm.getPhone());
                if(user == null) {
                    text = "You are not a user! Please register by ...";
                    emailSend.setAll("", text, tm.getFrom());
                    emailSend.send();
                }
                else if(!tm.getPassword().equalsIgnoreCase(user.getPassword())){
                    text = "Your password is"+user.getPassword()+". Please send email to ***";
                    emailSend.setAll("", text, tm.getFrom());
                    emailSend.send();
                }
                else{
                    int i;
                    for(i = 0; i < tm.getNumBills(); i++){
                    if(user.getFriendId(tm.getBillFriend(i)) == 0){    //add a function to change id to phonenumber
                    // if String is a nick, it might not exist. all phone# are considered right
                    userObjFacade.edit(user);
                    text = "Your don't have a friend with identifier"+tm.getBillFriend(i);
                    emailSend.setAll("", text, tm.getFrom());
                    emailSend.send();
                    }
                    else {
                    if(userObjFacade.find(user.getFriendId(tm.getBillFriend(i))) == null){ //phone# is new
                        
                        UserObj newUser = new UserObj(user.getFriendId(tm.getBillFriend(i)));
                        userObjFacade.create(newUser);
                        user.addFriend(newUser,tm.getFriendNick());                       //addFriend
                        //newUser.addFriend(user,tm.getFriendNick());
                        userObjFacade.edit(user);
                        
                     //   text = "Welcome to use cheapFriend! Your pass is"+user.getPassword(); //treat all users as new users, but only create account for really new users
                     //   emailSend.setAll("", text, );
                     //   emailSend.send();
                    }

                    user.addLoan(userObjFacade.find(user.getFriendId(tm.getBillFriend(i))), tm.getBillMoney(i)); //add function addLoan, addDebt
                    userObjFacade.find(user.getFriendId(tm.getBillFriend(i))).addDebt(user, tm.getBillMoney(i));
                    userObjFacade.edit(user);
                        
                    //add getFriend(friend's identifier) to return the instance of userObj for a user's friend
                    //first get a instance of Friend of a user's friend, then get the instance of userObj by calling getFriend()
                    text = "You have sent a bill of " + tm.getBillMoney(i) +" to your friend "+tm.getBillFriend(i);
                    emailSend.setAll("", text, tm.getFrom());
                    emailSend.send();
                    text = "Your friend " + tm.getPhone() + "request a bill of " + tm.getBillMoney(i) + "to you.";
                    emailSend.setAll("", text, userObjFacade.find(user.getFriendId(tm.getBillFriend(i))).getEmail_domain());
                    //we dont know email domain for new user
                    emailSend.send();
                    }
                    }
                }
                return;

            case TextMessage.REPORT_BILLS:
                user = userObjFacade.find(tm.getPhone());
                if(user == null) {
                    text = "You are not a user! Please register by ...";
                    emailSend.setAll("", text, tm.getFrom());
                    emailSend.send();
                }
                else if(!tm.getPassword().equalsIgnoreCase(user.getPassword())){
                    text = "Your password is"+user.getPassword()+". Please send email to ***";
                    emailSend.setAll("", text, tm.getFrom());
                    emailSend.send();
                }
                else{
                    if( (user.getFriends()).isEmpty()){
                        text = "No Friends Found.";
                        emailSend.setAll("", text, tm.getFrom());
                        emailSend.send();
                    }
                    else if( ((user.getLoans()).isEmpty()) && ((user.getDebts()).isEmpty())){
                        text = "No Bills Found.";
                        emailSend.setAll("", text, tm.getFrom());
                        emailSend.send();
                    }
                    else{
                        long each_loan ;
                        long each_debt ;
                        long final_loan ;
                        Boolean bills_exist = Boolean.FALSE;
                        text = "";
                        String newline = System.getProperty("line.separator");

                        for(Friend f : user.getFriends()){
                            each_loan = 0;
                            each_debt = 0;
                            for(Bill loan : user.getLoans()){
                                if(loan.getPaid() == Boolean.FALSE)
                                    each_loan += loan.getAmount();
                            }
                            for(Bill debt : user.getDebts()){
                                if(debt.getPaid() == Boolean.FALSE)
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

                        if(bills_exist){
                            emailSend.setAll("", text, tm.getFrom());
                            emailSend.send();
                        }
                        else{
                            text = "You are even with all your friends.";
                            emailSend.setAll("", text, tm.getFrom());
                            emailSend.send();
                        }

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
}