/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mycheapfriend;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateful;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author Waseem Ilahi
 */
@Stateful(mappedName="ejb.AdminLoginBean")
public class AdminLoginBean implements AdminLoginRemote {
    
    UserObjFacadeRemote userObjFacade;
    InitialContext context;

    public Boolean check_login(long phone, String password){

         try {

            context = new InitialContext();
            userObjFacade = (UserObjFacadeRemote) context.lookup("ejb.UserObjFacade");
        } catch (NamingException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);

        }

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
