
package stateless;

import javax.ejb.Remote;

/**
 *
 * @author Waseem Ilahi
 */
@Remote
public interface CheapToyRemote {

    String getName();
    
}
