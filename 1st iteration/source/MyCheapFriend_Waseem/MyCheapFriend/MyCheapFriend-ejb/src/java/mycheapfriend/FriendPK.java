/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mycheapfriend;

import java.io.Serializable;

/**
 *
 * @author Waseem Ilahi
 */
public class FriendPK implements Serializable {

    private UserObj parent;
    private UserObj friend;

    public UserObj getFriend() {
        return friend;
    }

    public void setFriend(UserObj friend) {
        this.friend = friend;
    }

    public UserObj getParent() {
        return parent;
    }

    public void setParent(UserObj parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (!(obj instanceof FriendPK)) return false;
        FriendPK pk = (FriendPK) obj;
        return pk.parent.equals(this.parent) && pk.friend.equals(this.friend);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += ( (( this.parent != null ) && ( this.friend != null)) ? (this.parent.hashCode() + this.friend.hashCode()) : 0);
        return hash;
    }



}
