/*
 * idea taken from the tutorial at url: http://java-x.blogspot.com/2007/01/ejb-3-timer-service.html
 */

package mycheapfriend;

import javax.ejb.Remote;

/**
 *
 * @author Waseem Ilahi
 */
@Remote
public interface PollerRemote {

    public void startTimer();
    
}
