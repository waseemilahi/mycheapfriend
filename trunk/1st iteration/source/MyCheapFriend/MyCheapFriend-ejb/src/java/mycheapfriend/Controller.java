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
                if(user == null) {
                    user = new UserObj(tm.getPhone(),PasswordGenerator.generatePassword()); //add a constructor by phone #
                    userObjFacade.create(user);
                }
                text = "Welcome to use cheapFriend! Your pass is"+user.getPassword(); //treat all users as new users, but only create account for really new users
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
                else if(user.getFriendId(tm.getBillFriend(0)) == 0){    //add a function to change id to phonenumber
                    // if String is a nick, it might not exist. all phone# are considered right
                    userObjFacade.edit(user);
                    text = "Your don't have a friend with identifier"+tm.getBillFriend(0);
                    emailSend.setAll("", text, tm.getFrom());
                    emailSend.send();
                }
                else {
                    if(userObjFacade.find(user.getFriendId(tm.getBillFriend(0))) == null){ //phone# is new
                        long id;
                        Object actual_id = tm.getBillFriend(0);
                        if(actual_id instanceof String )
                            id = user.getFriendId((String) actual_id);
                        else
                            id = ( (Long) actual_id ).longValue();

                        UserObj newUser = new UserObj(id, PasswordGenerator.generatePassword());
                        userObjFacade.create(newUser);
                        user.addFriend(newUser,tm.getFriendNick());                       //addFriend
                        newUser.addFriend(user,tm.getFriendNick());
                        userObjFacade.edit(newUser);
                        
                     //   text = "Welcome to use cheapFriend! Your pass is"+user.getPassword(); //treat all users as new users, but only create account for really new users
                     //   emailSend.setAll("", text, );
                     //   emailSend.send();
                    }

                    user.addLoan(tm.getBillFriend(0), tm.getBillMoney(0)); //add function addLoan, addDebt
                    userObjFacade.find(user.getFriendId(tm.getBillFriend(0))).addDebt(tm.getPhone(), tm.getBillMoney(0));
                    userObjFacade.edit(user);
                        
                    //add getFriend(friend's identifier) to return the instance of userObj for a user's friend
                    //first get a instance of Friend of a user's friend, then get the instance of userObj by calling getFriend()
                    text = "You have sent a bill of " + tm.getBillMoney(0) +" to your friend "+tm.getBillFriend(0);
                    emailSend.setAll("", text, tm.getFrom());
                    emailSend.send();
                    text = "Your friend " + tm.getPhone() + "request a bill of " + tm.getBillMoney(0) + "to you.";
                    emailSend.setAll("", text, userObjFacade.find(user.getFriendId(tm.getBillFriend(0))).getEmail_domain());
                    //we dont know email domain for new user
                    emailSend.send();
                }
                return;

            case TextMessage.REPORT_BILLS: return;
            default: break;
        }
    }
    public void setFriendNickName(long phone, String nickname, UserObj user){

            UserObj newUser = userObjFacade.find(phone);
            Boolean found = Boolean.FALSE;

            if(newUser == null){

                newUser = new UserObj(phone);
                Friend newFriend = new Friend();
                newFriend.setParent(user);
                newFriend.setFriend(newUser);
                newFriend.setNickname(nickname);
                user.getFriends().add(newFriend);

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