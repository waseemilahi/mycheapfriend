
package stateless;

import javax.ejb.Stateless;

/**
 *
 * @author Waseem Ilahi
 */
@Stateless
public class CheapToyBean implements CheapToyRemote {


    /**
     * Returns the Name of our Team.
     *
     * @return Team Name
     */
    public String getName() {
        return "We are CheapSkates";
    }
     
}
