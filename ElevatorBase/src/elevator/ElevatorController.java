package elevator;

import gui.ElevatorDisplay;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Yi Zheng
 */

public class ElevatorController {

    private static int numElevators;
    private static HashMap<Integer,Elevator> elevators;
    private static ElevatorController instance;
    private ElevatorManager em;
    private PendingRequestsManager prm;
    private ArrayList<Request> pendingRequests;


    private ElevatorController() throws InvalidParamException {

        setNumElevators();

        elevators = new HashMap<>();
        for (int i=1; i<=numElevators; i++){

            elevators.put(i, new Elevator(i));
        }
        pendingRequests = new ArrayList<>();
        em = ElevatorManagerFactory.buildElevatorManager("basic", numElevators);
        prm = PendingRequestsManagerFactory.buildPendingRequestsManager("basic");
    }

    public static int getNumElevators() {
        return numElevators;
    }

    public int getElevatorFloor(int id) throws InvalidParamException{
        if(id<1 || id>numElevators){
            throw new InvalidParamException("id is from 1 to the number of elevators.");
        }
        return elevators.get(id).getCurrentFloor();
    }

    public Direction getElevatorDirection(int id) throws InvalidParamException{
        if(id<1 || id>numElevators){
            throw new InvalidParamException("id is from 1 to the number of elevators.");
        }
        return elevators.get(id).getDirection();
    }

    public boolean isDone(){
        for(int i=1; i<= numElevators; i++) {
            if((!elevators.get(i).riderRequestIsEmpty()) || (!elevators.get(i).floorRequestIsEmpty()) || (elevators.get(i).getCurrentFloor() != 1)){
                return false;
            }
            if(!pendingRequests.isEmpty()){
                return false;
            }
        }
        return true;
    }


    private static void setNumElevators() throws InvalidParamException {
        int numElevators = BuildingConfiguration.getInstance().getNumElevators();
        if (numElevators>0) {
            ElevatorController.numElevators = numElevators;
        }
        else {
            throw new InvalidParamException("Number of elevators must be greater than 0.");
        }
    }

    private ElevatorDTO getElevatorDTO(int i) throws InvalidParamException{

        Elevator e = elevators.get(i);
        return new ElevatorDTO(e.getCurrentFloor(), e.getDirection(), e.getNextFloorRequestDirection(), e.riderRequestIsEmpty());
    }

    private PendingRequestDTO getPendingRequestDTO(int i) throws InvalidParamException{

        Request r = pendingRequests.get(i);
        return  new PendingRequestDTO(r.getFloorNum(),r.getDir());
    }

    public void addFloorRequest(Request r) throws InvalidParamException {
        if (r.getDir() == Direction.IDLE){
            throw new InvalidParamException("Idle request cannot be added.");
        }
        if(r.getFloorNum() < 1 || r.getFloorNum() > Building.getNumFloors()){
            throw new InvalidParamException("Floor number of the request out of range.");
        }
        int fl = r.getFloorNum();
        Direction d = r.getDir();
        ArrayList<ElevatorDTO> list = new ArrayList<>();
        for (int i=1; i<=numElevators; i++){
            list.add(getElevatorDTO(i));
        }
        int selectedElevatorId = em.chooseElevator(fl, d, list);
        if(selectedElevatorId>0) {
            elevators.get(selectedElevatorId).addFloorRequest(r);
        }
        else {
            pendingRequests.add(r);
        }
    }

    public ArrayList<Request> selectPendingRequests() throws InvalidParamException{
        ArrayList<Request> requestsToAdd = new ArrayList<>();
        if (!pendingRequests.isEmpty()) {
            ArrayList<PendingRequestDTO> list = new ArrayList<>();
            for (int i = 0; i < pendingRequests.size(); i++) {
                list.add(getPendingRequestDTO(i));
            }
            ArrayList<Integer> selectedRequests = prm.choosePendingRequests(list);

            for (int i=0; i<selectedRequests.size(); i++){
                requestsToAdd.add(pendingRequests.get(selectedRequests.get(i)));
            }
            for (Request r: requestsToAdd){
                pendingRequests.remove(r);
            }
        }
        return  requestsToAdd;
    }

    public static ElevatorController getInstance() throws InvalidParamException {
        if (instance == null){
            instance = new ElevatorController();
        }
        return instance;
    }

    public static void operateElevators(long time) throws InvalidParamException, InvalidStateException {
        if (time<0){
            throw new InvalidParamException("time must be positive.");
        }

        for (int i=1; i<=numElevators; i++){

            elevators.get(i).move(1000);
            Direction d = elevators.get(i).getDirection();
            ElevatorDisplay.Direction dir;
            if (d == Direction.UP){
                dir = ElevatorDisplay.Direction.UP;
            }
            else if (d == Direction.DOWN){
                dir = ElevatorDisplay.Direction.DOWN;
            }
            else {
                dir = ElevatorDisplay.Direction.IDLE;
            }

            ElevatorDisplay.getInstance().updateElevator(i,elevators.get(i).getCurrentFloor(),elevators.get(i).getNumRiders(),dir);
        }
    }

    private void removeFulfilledRequests(int id, Request r) throws InvalidParamException{
        for (int i=1; i<=numElevators; i++){
            if (i != id) {
                elevators.get(i).removeFulfilledRequests(r);
            }
        }
        while (pendingRequests.contains(r)){
            pendingRequests.remove(r);
        }
    }

    public void requestFulfilled(int id, Request r) throws InvalidParamException{
        if (id < 0){
            throw new InvalidParamException("id must not be negative.");
        }
        if(r.getFloorNum() < 1 || r.getFloorNum() > Building.getNumFloors()){
            throw new InvalidParamException("Floor number of the request out of range.");
        }
        removeFulfilledRequests(id, r);
    }
}
