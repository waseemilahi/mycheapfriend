/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mycheapfriend;

import java.util.List;
/**
 *
 * @author Shaoqing Niu
 */
public class Controller{

    EmailSend emailSend;
    UserObjFacade userObjFacade;

    public void handle(TextMessage tm) {
        if(tm.getErrorType() != Global.ERROR_NOERROR) {
            emailSend.setAll("", getErrorMessage(tm.getErrorType()), tm.getFrom());
            emailSend.send();
            return;
        }
        else
            handleMessage(tm);
    }

    public String getErrorMessage(int errorType){ //Only for syntax Error
        switch(errorType) {
            case Global.ERROR_FROMILLIGLEADDRESS:
                return "Please use a cell phone to send the message.";
            case Global.ERROR_INDISCERNIBLE:
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
            case Global.CREATACCONT:
                user = userObjFacade.find(tm.getPhone());        //define =    //find by phone
                if(user == null) {
                    user = new UserObj(tm.getPhone(),PasswordGenerator.generatePassword()); //add a constructor by phone #
                    userObjFacade.create(user);
                }
                text = "Welcome to use cheapFriend! Your pass is"+user.getPassword(); //treat all users as new users, but only create account for really new users
                emailSend.setAll("", text, tm.getFrom());
                emailSend.send();
                return;
            case Global.CHANGEPASSWORD:
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
            case Global.UNSUBSCRIBE:
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
            case Global.RESUBSCRIBE:
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
            case Global.SETNICKNAME:
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
                    user.setFriendNickName(tm.getFriendPhone(), tm.getFriendNick()); //add new function
                    userObjFacade.edit(user);
                    //if the phone is already in table, update its nick, else create a friend
                    text = "Your have set your friend "+tm.getFriendPhone()+"'s nickname to "+tm.getFriendNick();
                    emailSend.setAll("", text, tm.getFrom());
                    emailSend.send();
                }
                return;
            case Global.SINGLEREQUEST:
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
                else if(user.idToPhone(tm.getBillFriend(0)) == 0){    //add a function to change id to phonenumber
                    // if String is a nick, it might not exist. all phone# are considered right
                    userObjFacade.edit(user);
                    text = "Your don't have a friend with identifier"+tm.getBillFriend(0);
                    emailSend.setAll("", text, tm.getFrom());
                    emailSend.send();
                }
                else {
                    if(userObjFacade.find(user.idToPhone(tm.getBillFriend(0))) == null){ //phone# is new
                        long id;
                        Object actual_id = tm.getBillFriend(0);
                        if(actual_id instanceof String )
                            id = user.idToPhone((String) actual_id);
                        else
                            id = ( (Long) actual_id ).longValue();

                        UserObj newUser = new UserObj(id, PasswordGenerator.generatePassword());
                        userObjFacade.create(newUser);
                        user.addFriend(newUser);                       //addFriend
                        newUser.addFriend(user);
                        userObjFacade.edit(newUser);
                        
                     //   text = "Welcome to use cheapFriend! Your pass is"+user.getPassword(); //treat all users as new users, but only create account for really new users
                     //   emailSend.setAll("", text, );
                     //   emailSend.send();
                    }

                    user.addLoan(tm.getBillFriend(0), tm.getBillMoney(0)); //add function addLoan, addDebt
                    userObjFacade.find(user.idToPhone(tm.getBillFriend(0))).addDebt(tm.getPhone(), tm.getBillMoney(0));
                    userObjFacade.edit(user);
                        
                    //add getFriend(friend's identifier) to return the instance of userObj for a user's friend
                    //first get a instance of Friend of a user's friend, then get the instance of userObj by calling getFriend()
                    text = "You have sent a bill of " + tm.getBillMoney(0) +" to your friend "+tm.getBillFriend(0);
                    emailSend.setAll("", text, tm.getFrom());
                    emailSend.send();
                    text = "Your friend " + tm.getPhone() + "request a bill of " + tm.getBillMoney(0) + "to you.";
                    emailSend.setAll("", text, userObjFacade.find(user.idToPhone(tm.getBillFriend(0))).getEmail_domain);
                    //we dont know email domain for new user
                    emailSend.send();
                }
                return;
            case Global.MULTIREQUEST: return;
            case Global.RECEIVEBILL:
                
            case Global.SETTLEBILL: return;
            case Global.REPORT: return;
            default: break;
        }
    }
}