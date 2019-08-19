package elevator;

import java.util.ArrayList;

/**
 *
 * @author Yi Zheng
 */

public class Floor {
    private int floorNum;
    private ArrayList<Person> waiters = new ArrayList<>();
    private ArrayList<Person> donePeople = new ArrayList<>();

    public Floor(int floorNum) throws InvalidParamException{
        setFloorNum(floorNum);
    }

    @Override
    public String toString(){
        return "Floor "+floorNum;
    }

    public void setFloorNum(int fn)throws InvalidParamException{
        if (fn<1){
            throw new InvalidParamException("fn must be positive.");
        }
        this.floorNum = fn;
    }

    public void addDonePeople(Iterable<Person> donePeople) {

        for (Person p: donePeople){
            this.donePeople.add(p);
            try {
                p.setRideEnd(System.currentTimeMillis());
            } catch (InvalidParamException e) {
                e.printStackTrace();
            }
            ElevatorDriver.logOutput("Person "+p+" entered "+this);
        }
    }

    public void addPerson(Person p){
        waiters.add(p);
    }

    public Iterable<Person> movePeopleFromFloor(Direction d) throws InvalidParamException{
        ArrayList<Person> fromFloor = new ArrayList<>();
        for (int i=0; i<waiters.size(); i++){
            Direction directionOfWaiter = Direction.determineDirection(this.floorNum,waiters.get(i).getEndFloor());
            if (directionOfWaiter==d){
                fromFloor.add(waiters.get(i));
            }
        }
        for (int i=0; i<fromFloor.size(); i++){
            ElevatorDriver.logOutput("Person "+fromFloor.get(i)+" has left "+this);
            waiters.remove(fromFloor.get(i));
        }
        return fromFloor;
    }

}
