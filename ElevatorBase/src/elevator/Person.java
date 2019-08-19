package elevator;

/**
 *
 * @author Yi Zheng
 */

public class Person {


    private String id;
    private int startFloor;
    private int endFloor;

    Direction d;

    private long waitStart;
    private long waitEnd;
    private long rideStart;
    private long rideEnd;

    private long waitTime;
    private long rideTime;

    private long totalTime;

    private void computeTotal(){
        totalTime = waitTime+rideTime;
    }

    public void printStats(){

        System.out.printf("  %3s            %2d               %2d            %4s           %4.1f           %4.1f           %4.1f\n", id, startFloor,endFloor,d,waitTime/1000.0,rideTime/1000.0,totalTime/1000.0);
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof Person) {
            Person p = (Person)o;
            return this.id == p.id;
        }
        return false;
    }

    public long computeWaitTime(){
        waitTime = this.waitEnd - this.waitStart;
        return waitTime;
    }

    public long computeRideTime(){
        rideTime = this.rideEnd - this.rideStart;
        return rideTime;
    }

    public void setWaitStart(long waitStart) throws InvalidParamException{
        if (waitStart<=0){
            throw new InvalidParamException("Time must be positive.");
        }
        this.waitStart = waitStart;
    }

    public void setWaitEnd(long waitEnd) throws InvalidParamException{
        if (waitEnd<=0){
            throw new InvalidParamException("Time must be positive.");
        }
        this.waitEnd = waitEnd;
    }

    public void setRideStart(long rideStart) throws InvalidParamException{
        if(rideStart<=0){
            throw new InvalidParamException("Time must be positive.");
        }
        this.rideStart = rideStart;

    }

    public void setRideEnd(long rideEnd) throws InvalidParamException{
        if(rideEnd<=0){
            throw new InvalidParamException("Time must be positive.");
        }
        this.rideEnd = rideEnd;
        computeWaitTime();
        computeRideTime();
        computeTotal();
    }

    public int getEndFloor() {
        return endFloor;
    }

    public Person(String id, int startFloor, int endFloor) throws InvalidParamException {

        setStartFloor(startFloor);
        setEndFloor(endFloor);
        this.d = Direction.determineDirection(startFloor, endFloor);
        this.id = id;
        setWaitStart(System.currentTimeMillis());
    }



    @Override
    public String toString(){
        return id;
    }

    private void setStartFloor(int startFloor) throws InvalidParamException {
        if (startFloor<=Building.getInstance().getNumFloors() && startFloor>0) {
            this.startFloor = startFloor;
        }
        else{
            throw new InvalidParamException("Start floor must be greater than 0 and less than max floor number.");
        }
    }

    private void setEndFloor(int endFloor) throws InvalidParamException {
        if (endFloor<=Building.getInstance().getNumFloors() && endFloor>0) {
            this.endFloor = endFloor;
        }
        else{
            throw new InvalidParamException("End floor must be greater than 0 and less than max floor number.");
        }
    }

}
