/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mycheapfriend;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author michaelglass
 */
@Entity
public class Friend implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @ManyToOne
    private UserObj parent;
    private String nickname;
    private String telephone;

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (nickname != null ? nickname.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Friend)) {
            return false;
        }
        Friend other = (Friend) object;
        if ((this.nickname == null && other.nickname != null) || (this.nickname != null && !this.nickname.equals(other.nickname))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "mycheapfriend.Friend[id=" + nickname + "]";
    }

}
