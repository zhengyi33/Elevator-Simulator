package elevator;

/**
 *
 * @author Yi Zheng
 */

public enum Direction {

    UP,DOWN,IDLE;

    public static Direction determineDirection(int start, int end) throws InvalidParamException{
        if(start<=0){
            throw new InvalidParamException("Floor number must be positive.");
        }
        if(end<=0){
            throw new InvalidParamException("Floor number must be positive.");
        }
        int diff = end - start;
        if(diff>0){
            return Direction.UP;
        }
        else if (diff<0){
            return Direction.DOWN;
        }
        else{
            return Direction.IDLE;
        }
    }
}
