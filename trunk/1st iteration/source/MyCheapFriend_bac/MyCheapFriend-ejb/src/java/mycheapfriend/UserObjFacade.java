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
public class UserObjFacade {
    @PersistenceContext
    private EntityManager em;

    public void create(UserObj UserObj) {
        em.persist(UserObj);
    }

    public void edit(UserObj UserObj) {
        em.merge(UserObj);
    }

    public void remove(UserObj UserObj) {
        em.remove(em.merge(UserObj));
    }

    public UserObj find(Object id) {
        return em.find(UserObj.class, id);
    }

    public List<UserObj> findAll() {
        return em.createQuery("select object(o) from UserObj as o").getResultList();
    }

}
