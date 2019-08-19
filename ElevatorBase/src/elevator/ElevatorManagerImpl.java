package elevator;


import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Yi Zheng
 */

public class ElevatorManagerImpl implements ElevatorManager {

    private int numElevators;
    private HashMap<Integer,Integer> elevatorsFloor;
    private HashMap<Integer,Direction> elevatorsDirection;
    private HashMap<Integer, Direction> elevatorsFloorRequestDirection;
    private HashMap<Integer, Boolean> elevatorsRiderReqIsEmpty;



    public ElevatorManagerImpl(int numElevator) throws InvalidParamException{
        if(numElevator<1){
            throw new InvalidParamException("numElevator must be positive.");
        }
        this.numElevators = numElevator;
        elevatorsFloor = new HashMap<>();
        elevatorsDirection = new HashMap<>();
        elevatorsFloorRequestDirection = new HashMap<>();
        elevatorsRiderReqIsEmpty = new HashMap<>();
    }

    private int getIdElevatorOnFloorInDirection(int floor, Direction d){
        for (int i=1; i<=this.numElevators; i++){
            if (elevatorsFloor.get(i) == floor && elevatorsDirection.get(i) == d){
                return i;
            }
        }
        return -1;
    }



    private boolean hasMovingElevator(){
        for (int i=1; i<=this.numElevators; i++){
            if(elevatorsDirection.get(i) != Direction.IDLE){
                return true;
            }
        }
        return false;
    }


    private int getIdIdleElevator(){
        for (int i=1; i<=this.numElevators; i++){
            if(elevatorsDirection.get(i) == Direction.IDLE){
                return i;
            }
        }
        return -1;
    }

    private void setState(ArrayList<ElevatorDTO> list){
        for (int i=1; i<=this.numElevators; i++){
            ElevatorDTO e = list.get(i-1);
            this.elevatorsFloor.put(i, e.fn);
            this.elevatorsDirection.put(i, e.d);
            this.elevatorsFloorRequestDirection.put(i, e.floorRequestDirection);
            this.elevatorsRiderReqIsEmpty.put(i,e.riderRequestIsEmpty);
        }
    }



    private boolean isGoingInDesiredDirection(int i, int fl, Direction d) throws InvalidParamException{
        if (!elevatorsRiderReqIsEmpty.get(i)) {
            if (elevatorsDirection.get(i) == Direction.determineDirection(elevatorsFloor.get(i), fl) || elevatorsFloor.get(i) == fl) {
                if (d == elevatorsDirection.get(i)) {
                    return true;
                }
            }

        } else if (elevatorsDirection.get(i) == Direction.determineDirection(elevatorsFloor.get(i), fl) || elevatorsFloor.get(i) == fl) {
            if (elevatorsDirection.get(i) == d) {
                if (elevatorsFloorRequestDirection.get(i) == d) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public int chooseElevator(int start, Direction d, ArrayList<ElevatorDTO> list) throws InvalidParamException{

        setState(list);

        for (int i = 1; i <= numElevators; i++) {
            if (elevatorsFloor.get(i) == start) {
                if (elevatorsDirection.get(i) == Direction.IDLE) {
                    return i;
                } else if (isGoingInDesiredDirection(i, start, d)) {
                    return i;
                }
            }
        }
        for (int i = 1; i <= numElevators; i++) {
            if (elevatorsDirection.get(i) != Direction.IDLE) {
                if (isGoingInDesiredDirection(i, start, d)) {
                    return i;
                }
            }
        }
        for (int i = 1; i <= numElevators; i++) {
            if (elevatorsDirection.get(i) == Direction.IDLE) {
                return i;
            }
        }

        return -1;


    }
}
