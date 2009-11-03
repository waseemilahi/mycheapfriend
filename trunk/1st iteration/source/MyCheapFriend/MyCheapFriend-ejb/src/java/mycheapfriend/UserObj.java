/* The User Object Entity. */

package mycheapfriend;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author Waseem Ilahi
 */
@Entity
public class UserObj implements Serializable {

    @OneToMany(mappedBy = "borrower", fetch=FetchType.EAGER, cascade={CascadeType.REMOVE})
    private List<Bill> debts;

    @OneToMany(mappedBy = "lender", fetch=FetchType.EAGER, cascade={CascadeType.REMOVE})
    private List<Bill> assets;

    @OneToMany(mappedBy = "parent", fetch=FetchType.EAGER, cascade={CascadeType.REMOVE})
    private List<Friend> friends;

    private static final long serialVersionUID = 1L;

    @Id
    private long phone;

    private String password;

    private String salt;

    private Boolean active;

    private Boolean unsubscribe;

    private String email_domain;

    public UserObj() {
       
        this.password = null;
        this.salt = null;
        this.active = Boolean.FALSE;
        this.unsubscribe = Boolean.FALSE;
        this.email_domain = null;
    }

     public UserObj(long phone) {
        
        this.password = null;
        this.salt = null;
        this.active = Boolean.FALSE;
        this.unsubscribe = Boolean.FALSE;
        this.email_domain = null;
    }

    public UserObj(long phone,String domain) {

//        this.password = password;
//        this.salt = "";
        this.active = Boolean.FALSE;
        this.unsubscribe = Boolean.FALSE;
        this.email_domain = domain;
    }

    public UserObj(long phone,String domain, String password) {
        
        this.password = password;
        this.salt = "";
        this.active = Boolean.TRUE;
        this.unsubscribe = Boolean.FALSE;
        this.email_domain = domain;
    }

    public long getFriendId(Object obj){
        
         if(obj instanceof String ){
             for(Friend f : this.friends)
                 if((f.getNickname()).equals((String)obj))
                     return f.getFriend().getPhone();
         }
         else if(obj instanceof Long){
             Long id = (Long)obj;
             return id.longValue();
         }
        return 0;         
    }

    public UserObj getFriend(String nickname){
        for(Friend f: this.friends)
            if((f.getNickname()).equals(nickname))
                return f.getFriend();

        return null;
    }
    
    public Boolean hasFriend(long phone) {
        for(Friend f : this.friends)
            if(f.getFriend().phone == phone)
                return Boolean.TRUE;
        return Boolean.FALSE;
    }

    public void addFriend(UserObj newUser,String nickname){

        Friend newFriend = new Friend();
        newFriend.setFriend(newUser);
        newFriend.setParent(this);
        newFriend.setNickname(nickname);
        this.getFriends().add(newFriend);
    }

    public String getNickname(UserObj friend)
    {
        for(Friend f: friends)
        {
            if(f.getFriend().equals(friend))
                return f.getNickname();
        }
        return null;
    }

    public void borrowFrom(UserObj friend , long amount){
        
        Bill loan = new Bill();
        loan.setAmount(amount);
        loan.setApproved(Boolean.FALSE);
        loan.setPaid(Boolean.FALSE);

//        Date today = new Date();
//        String today_date = new String();
//        today_date = "";
//        today_date.concat(Integer.toString(today.getMonth()));
//        today_date.concat("/");
//        today_date.concat(Integer.toString(today.getDate()));
//        today_date.concat("/");
//        today_date.concat(Integer.toString(today.getYear()));
//
//        loan.setBill_date(today_date);

        loan.setLender(this);
        loan.setBorrower(friend);

        (this.getAssets()).add(loan);
    }

     public void loanTo(UserObj friend , long amount){
        friend.borrowFrom(this, amount);
//        Bill debt = new Bill();
//        debt.setAmount(amount);
//        debt.setApproved(Boolean.FALSE);
//        debt.setPaid(Boolean.FALSE);
//
////        Date today = new Date();
////        String today_date = new String();
////        today_date = "";
////        today_date.concat(Integer.toString(today.getMonth()));
////        today_date.concat("/");
////        today_date.concat(Integer.toString(today.getDate()));
////        today_date.concat("/");
////        today_date.concat(Integer.toString(today.getYear()));
////
////        debt.setBill_date(today_date);
//
//        debt.setLender(friend);
//        debt.setBorrower(this);
//
//        this.getDebts().add(debt);
    }

    public List<Bill> getDebts() {
        return debts;
    }

    public void setDebts(List<Bill> debts) {
        this.debts = debts;
    }

    public List<Bill> getAssets() {
        return assets;
    }

    public void setAssets(List<Bill> loans) {
        this.assets = loans;
    }

    public List<Friend> getFriends() {
        return friends;
    }

    public void setFriends(List<Friend> friends) {
        this.friends = friends;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getEmail() {
        if(email_domain != null)
            return phone + "@" + email_domain;
        else
            return null;
    }

    public String getEmail_domain() {
        return email_domain;
    }

    public void setEmail_domain(String email_domain) {
        this.email_domain = email_domain;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Boolean getUnsubscribe() {
        return unsubscribe;
    }

    public void setUnsubscribe(Boolean unsubscribe) {
        this.unsubscribe = unsubscribe;
    }

    public Long getPhone() {
        return phone;
    }

    public void setPhone(Long phone) {
        this.phone = phone;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (phone != 0 ? phone : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof UserObj)) {
            return false;
        }
        UserObj other = (UserObj) object;
        if(this.phone == 0 || other.phone == 0)
                return false;
        else
            return this.phone == other.phone;
    }

    @Override
    public String toString() {
        return "ejb.UserObj[phone=" + phone + "]";
    }

}
