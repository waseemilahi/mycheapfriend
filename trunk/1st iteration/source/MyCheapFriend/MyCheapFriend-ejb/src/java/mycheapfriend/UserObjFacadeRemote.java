/* The Session Bean Interface. */

package mycheapfriend;

import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author Waseem Ilahi
 */
@Remote
public interface UserObjFacadeRemote {

    void create(UserObj userObj);

    void edit(UserObj userObj);

    void remove(UserObj userObj);

    UserObj find(long telephone);

    List<UserObj> findAll();

}
