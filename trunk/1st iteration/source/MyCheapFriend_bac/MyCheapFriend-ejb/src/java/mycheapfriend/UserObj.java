/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mycheapfriend;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author michaelglass
 */
@Entity
public class UserObj implements Serializable {
    @OneToMany(mappedBy = "parent")
    private List<Friend> friends;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String telephone;
    
    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String id) {
        this.telephone = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (telephone != null ? telephone.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserObj)) {
            return false;
        }
        UserObj other = (UserObj) object;
        if ((this.telephone == null && other.telephone != null) || (this.telephone != null && !this.telephone.equals(other.telephone))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "mycheapfriend.User[id=" + telephone + "]";
    }

}
