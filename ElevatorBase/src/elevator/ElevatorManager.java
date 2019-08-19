package elevator;


import java.util.ArrayList;

/**
 *
 * @author Yi Zheng
 */

public interface ElevatorManager {

    int chooseElevator(int start, Direction d, ArrayList<ElevatorDTO> list) throws InvalidParamException;

}
