/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cheaptoy;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import stateless.CheapToyRemote;

/**
 *
 * @author Waseem Ilahi
 */
public class Main {

    @EJB
    private static CheapToyRemote testEJB;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       // try {
            Properties props = new Properties();
            //props.load(new FileInputStream("jndi.properties"));
            InitialContext ctx;
        try {
            ctx = new InitialContext(props);

            testEJB = (CheapToyRemote) ctx.lookup("stateless.CheapToyRemote");
        } catch (NamingException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
      // try{
            System.out.println(testEJB.getName());
       //}catch (Exception e){
        //   System.out.println("exception");
       //}
       // }// catch (NamingException nex) {
         //   nex.printStackTrace();
       // } catch (FileNotFoundException fnfex) {
        //    fnfex.printStackTrace();
      //  } catch (IOException ioex) {
      //      ioex.printStackTrace();
      //  }
    }

}
