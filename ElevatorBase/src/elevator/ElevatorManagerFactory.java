package elevator;

/**
 *
 * @author Yi Zheng
 */

public class ElevatorManagerFactory {
    public static ElevatorManager buildElevatorManager(String algName, int numElevators) throws InvalidParamException {
        if (numElevators<0){
            throw new InvalidParamException("Number of elevators cannot be negative.");
        }
        if (algName.equals("basic")) {
            return new ElevatorManagerImpl(numElevators);
        }
        else{
            throw new InvalidParamException("Currently, there is only the basic algorithm to use.");
        }
    }
}
