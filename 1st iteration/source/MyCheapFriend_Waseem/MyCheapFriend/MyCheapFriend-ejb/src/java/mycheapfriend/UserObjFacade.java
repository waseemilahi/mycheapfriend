/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mycheapfriend;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author michaelglass
 */
@Stateless(mappedName="ejb.UserObjFacade")
public class UserObjFacade implements UserObjFacadeRemote {

   @PersistenceContext
    private EntityManager em;
   
    public void create(UserObj obj) {
        em.persist(obj);
    }

    public void edit(UserObj obj) {
        em.merge(obj);
    }


    public void remove(UserObj obj) {
        obj = em.merge(obj);

        List<Friend> ImTheseFriends = em.createQuery("select object(f) from Friend f where f.friend.phone = "+obj.getPhone() +"").getResultList();

        for(Friend f: ImTheseFriends)
            em.remove(f);

        List<Friend> fs= obj.getFriends();
        for(Friend f : fs)
            em.remove(f);
            
        fs.clear();
        
        
        em.remove(em.merge(obj));
    }

    public UserObj find(long telephone) {
        return em.find(UserObj.class, telephone);
    }

    public List<UserObj> findAll() {
        return em.createQuery("select object(o) from UserObj as o").getResultList();
    }


}
