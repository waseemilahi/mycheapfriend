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
public interface TextMessage {

//    private int type; //defined in Global
//    private int errorType; //syntax error type
//    private long phone;    //347-244-6971
//    private String fromAddress; //address = 347-244-6971@att.com or other address
//                            //in case that people don't use cellphone to send msg
//                            //if not start with 9 digits, it's a syntax error
//    private String toAddress; //pass@cheapfriend.com will check whether password is for the phone
//
//    private long friendPhone; //to set nickname for a friend
//    private String friendNick;
//
//    private List<String> billFriend; //to request/settle a bill
//    private List<Float> billMoney;

    public int getType();
    public int getErrorType();
    public long getPhone();

    public String getFrom();

    public String getDomain();
    public String getPassword();

    public long getFriendPhone();
    public String getFriendNick();
    
    public int getNumBills();
    public Object getBillFriend(int index);
    public long getBillMoney(int index);

}
