package elevator;

import java.util.ArrayList;

/**
 *
 * @author Yi Zheng
 */

public class PendingRequestsManagerImpl implements PendingRequestsManager {

    private ArrayList<PendingRequestDTO> pendingList;

    public PendingRequestsManagerImpl(){}

    @Override
    public ArrayList<Integer> choosePendingRequests(ArrayList<PendingRequestDTO> list) throws InvalidParamException{
        this.pendingList = list;
        ArrayList<Integer> selected = new ArrayList<>();
        selected.add(0);
        PendingRequestDTO first = pendingList.get(0);
        if (pendingList.size()>1){
            for (int i=1; i<pendingList.size(); i++){
                if (pendingList.get(i).d == first.d && pendingList.get(i).fl == first.fl){
                    selected.add(i);
                }
                else if (pendingList.get(i).d == first.d){
                    if (first.d == Direction.UP){
                        if (Direction.determineDirection(first.fl,pendingList.get(i).fl)==Direction.UP){
                            selected.add(i);
                        }
                    }
                    else{
                        if (Direction.determineDirection(first.fl,pendingList.get(i).fl)==Direction.DOWN){
                            selected.add(i);
                        }
                    }
                }
            }
        }
        return selected;
    }
}
