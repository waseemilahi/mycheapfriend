/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mycheapfriend;

import javax.ejb.Stateful;

/**
 *
 * @author Waseem Ilahi
 */
@Stateful
public class AdminLoginBean implements AdminLoginRemote {
    
    UserObjFacadeRemote userObjFacade;

    public Boolean check_login(long phone){
        return Boolean.FALSE;
    }
    
}
