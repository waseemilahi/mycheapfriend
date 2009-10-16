/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mycheapfriend;

import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author michaelglass
 */
@Remote
public interface UserObjFacadeRemote {
    public void create(UserObj obj);
    public void edit(UserObj obj);
    public void remove(UserObj obj);
UserObj find(long telephone);
    List<UserObj> findAll();
}
