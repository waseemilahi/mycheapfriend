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
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;

/**
 *
 * @author michaelglass
 */
@IdClass(FriendPK.class)
@Entity
public class Friend implements Serializable {

    @Id
    private Long id;
    private static final long serialVersionUID = 1L;
   
    

    @ManyToOne
    @Id private UserObj parent;

    @ManyToOne
    @Id private UserObj friend;

    
    private String nickname;

    
    public UserObj getFriend() {
        return friend;
    }

    public void setFriend(UserObj friend) {
        this.friend = friend;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public UserObj getParent() {
        return parent;
    }

    public void setParent(UserObj parent) {
        this.parent = parent;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += ( (( this.parent != null ) && ( this.friend != null)) ? (this.parent.hashCode() + this.friend.hashCode()) : 0);
        return hash;
    }

     @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (!(obj instanceof FriendPK)) return false;
        Friend pk = (Friend) obj;
        return pk.parent.equals(this.parent) && pk.friend.equals(this.friend);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


}
