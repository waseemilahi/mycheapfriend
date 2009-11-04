/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mycheapfriend;

import java.util.Collection;
import java.util.Iterator;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.*;

/**
 *
 * @author Waseem Ilahi
 */
@Stateless(mappedName="ejb.PollerBean")
public class PollerBean implements PollerRemote {
        
        @Resource
        private SessionContext ctx;
        private boolean started = false;
        public void startTimer() {
            if(started)
                return;
            started = true;
            ctx.getTimerService().createTimer(0, 5000, null);
            System.out.println("Timers set");

        }
    public String checkTimerStatus() {
        Timer timer = null;
        Collection timers = ctx.getTimerService().getTimers();
        Iterator iter = timers.iterator();
        while (iter.hasNext()) {
            timer = (Timer) iter.next();
            return ("Timer will expire after " + timer.getTimeRemaining() + " milliseconds.");

        }

        return ("No timer found");

    }

    @Timeout
    public void handleTimeout(Timer timer) {
       //call read(), process each email in the return arraylist.
        Controller c = new Controller();
        try {
            List<EmailInfo> messages = EmailRead.read();
            for(EmailInfo e : messages)
                c.handle(e);
        } catch (Exception ex) {
            Logger.getLogger(PollerBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("HandleTimeout called.");
    }

}
