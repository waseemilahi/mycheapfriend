/* The User Object Entity. */

package mycheapfriend;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author Waseem Ilahi
 */
@Entity
public class UserObj implements Serializable {

    @OneToMany(mappedBy = "debtor", fetch=FetchType.EAGER, cascade={CascadeType.REMOVE})
    private List<Bill> debts;

    @OneToMany(mappedBy = "lender", fetch=FetchType.EAGER, cascade={CascadeType.REMOVE})
    private List<Bill> loans;

    @OneToMany(mappedBy = "parent", fetch=FetchType.EAGER, cascade={CascadeType.REMOVE})
    private List<Friend> friends;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long phone;

    private String password;

    private String salt;

    private Boolean active;

    private Boolean unsubscribe;

    private String email_domain;

    public List<Bill> getDebts() {
        return debts;
    }

    public void setDebts(List<Bill> debts) {
        this.debts = debts;
    }

    public List<Bill> getLoans() {
        return loans;
    }

    public void setLoans(List<Bill> loans) {
        this.loans = loans;
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
        hash += (phone != null ? phone.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof UserObj)) {
            return false;
        }
        UserObj other = (UserObj) object;
        if ((this.phone == null && other.phone != null) || (this.phone != null && !this.phone.equals(other.phone))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ejb.UserObj[phone=" + phone + "]";
    }

}
