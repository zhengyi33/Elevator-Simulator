package elevator;

import java.util.HashMap;

/**
 *
 * @author Yi Zheng
 */

public class Building {


    private static ElevatorController ec;
    private static int numFloors;

    private static HashMap<Integer,Floor> floors;
    private static Building instance;

    private Building() throws InvalidParamException {

        setNumFloors();
        floors = new HashMap<>();
        for (int i = 1; i<= numFloors; i++){
            floors.put(i,new Floor(i));
        }
    }

    private static void setNumFloors() throws InvalidParamException {
        int numFloors = BuildingConfiguration.getInstance().getNumFloors();
        if(numFloors>0) {
            Building.numFloors = numFloors;
        }
        else {
            throw new InvalidParamException("Number of floors must be greater than 0.");
        }
    }

    public void addPerson(Person p, int floorNum) throws InvalidParamException{

        if (floorNum<1 || floorNum>numFloors){
            throw new InvalidParamException("Floor number must be within reasonable range.");
        }
        Floor f = floors.get(floorNum);
        f.addPerson(p);
    }

    public static int getNumFloors() {
        return numFloors;
    }



    public Iterable<Person> movePeopleFromFloorInDirection(int fn, Direction d) throws InvalidParamException{
        if(fn<0||fn>numFloors){
            throw new InvalidParamException("fn must be within reasonable range.");
        }
        return floors.get(fn).movePeopleFromFloor(d);
    }

    public void addDonePeopleForFloor(int fn,Iterable<Person> donePeople) throws InvalidParamException{
        if(fn<0||fn>numFloors){
            throw new InvalidParamException("fn must be within reasonable range.");
        }
        floors.get(fn).addDonePeople(donePeople);
    }

    public static Building getInstance() throws InvalidParamException {
        if (instance == null){
            instance = new Building();
        }
        return instance;
    }

}
