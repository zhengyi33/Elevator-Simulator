package elevator;

import java.util.ArrayList;

/**
 *
 * @author Yi Zheng
 */

public class PendingRequestDTO {

    public int fl;
    public Direction d;

    public PendingRequestDTO(int fl, Direction d) throws InvalidParamException{
        if (fl<0 || fl>Building.getNumFloors()){
            throw new InvalidParamException("Floor number out of bounds");
        }
        if (d == Direction.IDLE){
            throw new InvalidParamException("Request direction cannot be idle.");
        }
        this.fl = fl;
        this.d = d;
    }

}
