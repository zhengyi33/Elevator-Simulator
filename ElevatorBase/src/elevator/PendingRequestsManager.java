package elevator;

import java.util.ArrayList;

/**
 *
 * @author Yi Zheng
 */

public interface PendingRequestsManager {
    ArrayList<Integer> choosePendingRequests(ArrayList<PendingRequestDTO> list) throws InvalidParamException;

}
