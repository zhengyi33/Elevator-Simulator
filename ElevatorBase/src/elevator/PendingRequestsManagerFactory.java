package elevator;

import java.util.ArrayList;

/**
 *
 * @author Yi Zheng
 */

public class PendingRequestsManagerFactory {
    public static PendingRequestsManager buildPendingRequestsManager (String algName) throws InvalidParamException
    {
        if (algName.equals("basic")){
            return new PendingRequestsManagerImpl();
        }
        else
            throw new InvalidParamException("Currently, only the basic pending request selecting algorithm is implemented.");
    }
}
