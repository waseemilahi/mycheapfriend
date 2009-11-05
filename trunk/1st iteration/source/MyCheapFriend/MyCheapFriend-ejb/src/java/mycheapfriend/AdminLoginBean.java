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

    public Boolean check_login(long phone, String password){
        UserObj user = userObjFacade.find(phone);
        if(user == null){
            return Boolean.FALSE;
        }
        else if(!((user.getPassword()).equals(password))){
                return Boolean.FALSE;
        }
        else if(!(user.getActive())){
                return Boolean.FALSE;
        }

            return Boolean.TRUE;

    }
    
}
