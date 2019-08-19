package elevator;

import java.util.ArrayList;

/**
 *
 * @author Yi Zheng
 */

public class ElevatorDTO {
    public int fn;
    public Direction d;
    public Direction floorRequestDirection;
    public boolean riderRequestIsEmpty;

    public ElevatorDTO(int fn, Direction d, Direction floorRequestDirection, boolean riderRequestIsEmpty) throws InvalidParamException{
        if(fn<0 || fn > Building.getNumFloors()){
            throw new InvalidParamException("Floor number out of bounds.");
        }

        this.fn = fn;
        this.d = d;
        this.floorRequestDirection = floorRequestDirection;
        this.riderRequestIsEmpty = riderRequestIsEmpty;
    }
}
