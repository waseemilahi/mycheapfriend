/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mycheapfriend;

import java.util.Collection;
import java.util.Iterator;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;

/**
 *
 * @author Waseem Ilahi
 */
@Stateless
public class PollerBean implements PollerRemote {

        @Resource
        private SessionContext ctx;

        public void startTimer() {

            ctx.getTimerService().createTimer(5000, 5000, null);
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
        try {
            EmailRead.read();
        } catch (Exception ex) {
            Logger.getLogger(PollerBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
