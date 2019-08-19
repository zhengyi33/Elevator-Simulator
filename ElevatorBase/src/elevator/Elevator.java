package elevator;

import gui.ElevatorDisplay;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Yi Zheng
 */

public class Elevator {

    private int id;
    private static int capacity;
    private Direction direction;
    private int currentFloor;
    private ArrayList<Person> people = new ArrayList<>();
    private ArrayList<Request> floorRequests = new ArrayList<>();
    private ArrayList<Request> riderRequests = new ArrayList<>();
    private boolean doorsOpen;
    private static long timePerFloor;
    private long doorOpenTime;
    private long timeOut;
    private int idleCount;
    private int timeTillClose;
    private int timeLeftOnFloor;



    public Elevator(int id) throws InvalidParamException{
        if (id<0){
            throw new InvalidParamException("id must be positive.");
        }
        this.id = id;
        currentFloor = 1;
        direction = Direction.IDLE;
        capacity = BuildingConfiguration.getInstance().getCapacity();
        if(capacity<1){
            throw new InvalidParamException("capacity must be positive.");
        }
        timePerFloor = BuildingConfiguration.getInstance().getTimePerFloor();
        if(timePerFloor<0){
            throw new InvalidParamException("timePerFloor must be positive.");
        }
        doorOpenTime = BuildingConfiguration.getInstance().getDoorOpenTime();
        if(doorOpenTime<0){
            throw new InvalidParamException("doorOpenTime must be positive.");
        }
        timeOut = BuildingConfiguration.getInstance().getTimeout();
        if(timeOut<0){
            throw new InvalidParamException("timeOut must be positive.");
        }
    }

    public boolean isFull(){
        return (people.size() == capacity);
    }

    private void setDirection(Direction direction) {
        this.direction = direction;
    }

    private void setToIdle() throws InvalidParamException{

        checkPendingList();
        if (floorRequests.isEmpty() && riderRequests.isEmpty()){
            setDirection(Direction.IDLE);
        }
    }

    private void checkPendingList() throws InvalidParamException {
        if (floorRequestIsEmpty() && riderRequestIsEmpty()) { //this line added 7.17.2019 in case it is not REALLY idle
            ArrayList<Request> pendingRequests = ElevatorController.getInstance().selectPendingRequests();
            if (!pendingRequests.isEmpty()) {
                for (int i = 0; i < pendingRequests.size(); i++) {
                    addFloorRequest(pendingRequests.get(i));
                }
            }
        }
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getNumRiders(){
        return people.size();
    }

    public boolean riderRequestIsEmpty(){
        return riderRequests.isEmpty();
    }

    public boolean floorRequestIsEmpty(){
        return floorRequests.isEmpty();
    }

//    public boolean isIdle(){
//        return this.direction == Direction.IDLE;
//    }

    public Direction getNextFloorRequestDirection() {
        if (!floorRequests.isEmpty()){
            return floorRequests.get(0).getDir();
        }
        else {
            return Direction.IDLE;
        }

    }

    private ArrayList<Person> movePeopleFromElevatorToFloor(){
        ArrayList<Person> fromElevator = new ArrayList<>();
        ArrayList<Person> peopleToRemove = new ArrayList<>();
        for (int i=0; i<people.size(); i++){
            if(people.get(i).getEndFloor() == this.currentFloor){
                fromElevator.add((people.get(i)));
                peopleToRemove.add(people.get(i));
            }
        }
        for (Person p: peopleToRemove){
            people.remove(p);
            ElevatorDriver.logOutput("Person "+p+" has left "+this+" [Riders: "+peopleString());
        }

        return fromElevator;
    }

    private void floorButtonPressed(int fn) throws InvalidParamException{
        if (fn == currentFloor){
            //already at fn
            return;
        }
        Direction d = Direction.determineDirection(currentFloor,fn);
        if (direction != d){
            if (direction == Direction.IDLE){
                setDirection(d);
            }
            else {
                return;
            }
        }
        else{
            addRiderRequest(new Request(fn, d));
        }
    }

    private void addPerson(Person p) {
        if (people.size()<capacity){
            people.add(p);
            try {
                p.setWaitEnd(System.currentTimeMillis());
            } catch (InvalidParamException e) {
                e.printStackTrace();
            }
            try {
                p.setRideStart(System.currentTimeMillis());
            } catch (InvalidParamException e) {
                e.printStackTrace();
            }
        }
    }

    private void movePeopleFromFloorToElevator(int currentFloor, Direction d) throws InvalidParamException {
        Iterable<Person> fromFloor = Building.getInstance().movePeopleFromFloorInDirection(currentFloor,d);
        for (Person p: fromFloor){
            //check if is not full
            if (!isFull()) {
                ElevatorDriver.logOutput("Person "+p+" entered "+this+" [Riders: "+peopleString());
                floorButtonPressed(p.getEndFloor());
                addPerson(p);
            }
            else{
                //add back people to floor; add back requests;
                Building.getInstance().addPerson(p, currentFloor);
                ElevatorDriver.putBackCrowdedOutRequest(new Request(currentFloor,d));
            }
        }
    }

    private void closeDoors(){
        doorsOpen = false;
        ElevatorDisplay.getInstance().closeDoors(id);
        ElevatorDriver.logOutput(this.toString()+" Doors Close");
    }

    private void openDoors(){
        doorsOpen = true;
        ElevatorDisplay.getInstance().openDoors(id);
        ElevatorDriver.logOutput(this.toString()+" Doors Open");
    }



    private void addRiderRequest(Request r) {
        if (!riderRequests.contains(r)) {
            riderRequests.add(r);
            Collections.sort(riderRequests);
            if (r.getDir() == Direction.DOWN){
                Collections.reverse(riderRequests);
            }
            ElevatorDriver.logOutput(this.toString()+" Rider Request made for Floor "+r.getFloorNum()+ " [Current Floor Requests: "+requestString(floorRequests)+"[Current Rider Requests:"+requestString(riderRequests));
        }
    }




    private boolean hasPeopleInSameDirection(){
        if (direction == Direction.UP){
            for (Request r: floorRequests){
                if (r.getFloorNum() > currentFloor){
                    return false;
                }
                else if (r.getFloorNum() == currentFloor && r.getDir() == direction){
                    return true;
                }
            }
        }
        else if (direction == Direction.DOWN){
            for (int i = floorRequests.size() - 1; i >= 0; i--) {
                if (floorRequests.get(i).getFloorNum() < currentFloor) {
                    return false;
                } else if (floorRequests.get(i).getFloorNum() == currentFloor && floorRequests.get(i).getDir() == direction) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean currentFloorWantedByRider(){

        for (Request r: riderRequests){
            if (r.getFloorNum() == currentFloor){
                return true;
            }
        }
        return false;
    }

    public void addFloorRequest(Request r)throws InvalidParamException{

        if (r.getDir() == Direction.IDLE){
            throw new InvalidParamException("A request cannot have an idle direction.");
        }
        if (!floorRequests.contains(r)) {
            floorRequests.add(r);
            Collections.sort(floorRequests);

            if (r.getDir() == Direction.DOWN){
                Collections.reverse(floorRequests);
            }
            ElevatorDriver.logOutput(this+" is going to Floor "+r.getFloorNum()+ " for "+r.getDir()+" request "+"[Current Floor Requests: "+requestString(floorRequests)+"[Current Rider Requests:"+requestString(riderRequests));
        }
    }

    private String requestString(ArrayList<Request> requests){
        String s = "";
        for (Request r: requests){
            s = s+r.getFloorNum()+", ";
        }
        if (s.length() >= 2) {
            s = s.substring(0,s.length()-2);
        }
        s = s+"]";
        return s;
    }

    private String peopleString(){
        String s = "";
        for (Person p: people){
            s = s+p.toString()+", ";
        }
        if (s.length() >= 2) {
            s = s.substring(0,s.length()-2);
        }
        s = s+"]";
        return s;
    }

    private Request getNextReq() throws InvalidStateException, InvalidParamException{
        if (floorRequests.isEmpty() && riderRequests.isEmpty()){
            throw new InvalidStateException("An idle elevator with empty lists does not have a request.");
        }
        else if (floorRequests.isEmpty() && !riderRequests.isEmpty()){
            return riderRequests.get(0);
        }
        else if (!floorRequests.isEmpty() && riderRequests.isEmpty()){
            return floorRequests.get(0);
        }
        else {
            int fr = floorRequests.get(0).getFloorNum();
            int rr = riderRequests.get(0).getFloorNum();
            Direction frDir = floorRequests.get(0).getDir();
            Direction rrDir = riderRequests.get(0).getDir();

            if (Direction.determineDirection(currentFloor,fr) != rrDir && Direction.determineDirection(currentFloor,fr) != Direction.IDLE) {
                throw new InvalidStateException("It is not allowed for requests to have different directions.");
            }

            if (rrDir == Direction.UP)
                if (fr < rr) {
                    return floorRequests.get(0);
                } else {
                    return riderRequests.get(0);
                }
            else if (rrDir == Direction.DOWN) {
                if (fr > rr) {
                    return floorRequests.get(0);
                } else {
                    return riderRequests.get(0);
                }
            }
            else{
                throw new InvalidStateException("The direction of a rider request cannot be idle.");
            }
        }
    }

    private String stringFloorRequests(){
        return "[Current Floor Requests: "+requestString(floorRequests);
    }

    private String stringRiderRequests(){
        return "[Current Rider Requests:"+requestString(riderRequests);
    }

    @Override
    public String toString(){
        return "Elevator "+id;
    }

    public void move(long time) throws InvalidParamException, InvalidStateException {
        if(time<0){
            throw new InvalidParamException("time must be positive.");
        }
        if (timeTillClose > 0){  //decrement elevator waiting time
            timeTillClose -= time;
        }
        if (timeLeftOnFloor > 0){  //time elevator passes a floor
            timeLeftOnFloor -= time;
        }
        if (timeTillClose > 0 || timeLeftOnFloor > 0){ //can't move if doors are open
            return;
        }
        else if (doorsOpen){
            closeDoors();
            return;
        }
        if (floorRequests.isEmpty() && riderRequests.isEmpty()){
            idleCount++;
            if (idleCount > timeOut/1000){
                if(currentFloor != 1 && direction == Direction.IDLE){
                    direction = Direction.DOWN;
                    addRiderRequest(new Request(1, Direction.DOWN));
                    idleCount = 0;
                }
                else if (currentFloor == 1){
                    if(direction != Direction.IDLE) {

                        setToIdle();
                    }
                    idleCount = 0;
                    return;
                }
            }
            else{
                if(direction != Direction.IDLE) {

                    setToIdle();
                    idleCount = 0;
                }
                return;
            }
        }

        Request NextReq = getNextReq();
        int nextFn = NextReq.getFloorNum();
        Direction nextReqDir = NextReq.getDir();

        if (currentFloor != nextFn) {
            
            if (direction != Direction.determineDirection(currentFloor, nextFn)){
                direction = Direction.determineDirection(currentFloor, nextFn);
            }
            if (direction == Direction.UP) {
                if (currentFloor < Building.getInstance().getNumFloors() && currentFloor != nextFn) {
                    currentFloor++;
                    ElevatorDriver.logOutput(toString() + " moving from Floor " + (currentFloor - 1) + " to Floor " + currentFloor + " [Current Floor Requests: " + requestString(floorRequests) + "[Current Rider Requests:" + requestString(riderRequests));
                }
            } else if (direction == Direction.DOWN) {
                if (currentFloor > 1) {
                    currentFloor--;
                    ElevatorDriver.logOutput(toString() + " moving from Floor " + (currentFloor + 1) + " to Floor " + currentFloor + " [Current Floor Requests: " + requestString(floorRequests) + "[Current Rider Requests:" + requestString(riderRequests));
                }
            }
        }
        else {
            if(!doorsOpen){
                ElevatorDriver.logOutput(toString()+" has stopped at Floor "+currentFloor+" "+stringFloorRequests()+stringRiderRequests());
            }
            openDoors();
            timeTillClose += doorOpenTime;
            timeLeftOnFloor += doorOpenTime;
            if (direction != nextReqDir){
                direction = nextReqDir;
            }

            movePeopleFromFloorToElevator(currentFloor,direction);

            //code for notifying controller of fulfilled requests
            requestFulfilled();

            ArrayList<Person> donePeople = movePeopleFromElevatorToFloor();
            Building.getInstance().addDonePeopleForFloor(currentFloor,donePeople);
            riderRequests.remove(new Request(currentFloor,direction));
            floorRequests.remove(new Request(currentFloor,direction));
        }



        timeLeftOnFloor += timePerFloor;


    }

    public void removeFulfilledRequests(Request r) throws InvalidParamException{
        if(r.getDir()==Direction.IDLE){
            throw new InvalidParamException("Request cannot be idle.");
        }
        if (floorRequests.contains(r)){
            floorRequests.remove(r);
        }
    }

    private void requestFulfilled() throws InvalidParamException{
        ElevatorController.getInstance().requestFulfilled(id, new Request(currentFloor,direction));
    }
}
