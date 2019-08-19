package elevator;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Yi Zheng
 */

public class BuildingConfiguration {
    private int numFloors, numElevators, capacity;
    private long timePerFloor, doorOpenTime, timeout;
    private static BuildingConfiguration instance;

//    private BuildingConfiguration() throws InvalidParamException {
//        if(numFloors>0) {
//            this.numFloors = numFloors;
//        }
//        else {
//            throw new InvalidParamException("Number of floors must be greater than 0.");
//        }
//        if (numElevators>0) {
//            this.numElevators = numElevators;
//        }
//        else {
//            throw new InvalidParamException("Number of elevators must be greater than 0.");
//        }
//        if (capacity>0){
//            this.capacity = capacity;
//        }
//        else {
//            throw new InvalidParamException("Capacity must be greater than 0.");
//        }
//        if (tpf>0){
//            this.timePerFloor = tpf;
//        }
//        else {
//            throw new InvalidParamException("Time per floor must be greater than 0.");
//        }
//        if (dot>0){
//            this.doorOpenTime = dot;
//        }
//        else {
//            throw new InvalidParamException("Door open time must be greater than 0.");
//        }
//        if (timeout>0){
//            this.timeout = timeout;
//        }
//        else {
//            throw new InvalidParamException("Timeout must be greater than 0.");
//        }
//        initializeBuilding();
//    }

    public int getNumFloors() {
        return numFloors;
    }

    public int getNumElevators() {
        return numElevators;
    }

    public int getCapacity() {
        return capacity;
    }

    public long getTimePerFloor() {
        return timePerFloor;
    }

    public long getDoorOpenTime() {
        return doorOpenTime;
    }

    public long getTimeout() {
        return timeout;
    }

    private BuildingConfiguration() throws InvalidParamException {
        FileReader reader;
        try {
            // Create a FileReader object using your filename
            reader = new FileReader("input.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        JSONParser jsonParser = new JSONParser();
        JSONObject jObj;
        try {
            // Create a JSONParser using the FileReader
            jObj = (JSONObject) jsonParser.parse(reader);

        } catch (IOException | ParseException e) {
            System.out.println("Please check input file: "+e.getMessage());
            return;
        }

        int numFloors = (int)(long) jObj.get("numFloors");
        int numElevators = (int)(long) jObj.get("numElevators");
        int capacity = (int)(long) jObj.get("capacity");
        long timePerFloor = (long) jObj.get("timePerFloor");
        long doorOpenTime = (long) jObj.get("doorOpenTime");
        long timeout = (long) jObj.get("timeout");

//        try {
//            BuildingConfiguration.initializeBuilding(numFloors,numElevators,capacity,timePerFloor,doorOpenTime,timeout);
//            Building.getInstance();
//            ElevatorController.getInstance();
//        } catch (InvalidParamException e) {
//            System.out.println("Please check input file: "+e.getMessage());
//            return;
//        }

        if (numFloors<1){
            throw new InvalidParamException("numFloors must be positive.");
        }
        if(numElevators<1){
            throw new InvalidParamException("numElevators must be positive.");
        }
        if(capacity<1){
            throw new InvalidParamException("capacity must be positive.");
        }
        if(timePerFloor<=0){
            throw new InvalidParamException("tpf must be positive.");
        }
        if(doorOpenTime<=0){
            throw new InvalidParamException("dot must be positive.");
        }
        if(timeout<=0){
            throw new InvalidParamException("timeout must be positive.");
        }
//        if (instance == null){
//            instance = new BuildingConfiguration(numFloors, numElevators, capacity, timePerFloor, doorOpenTime, timeout);
//        }
        this.numFloors = numFloors;
        this.numElevators = numElevators;
        this.capacity = capacity;
        this.timePerFloor = timePerFloor;
        this.doorOpenTime = doorOpenTime;
        this.timeout = timeout;
    }

    public static BuildingConfiguration getInstance() throws InvalidParamException{
        //return instance;
        if (instance == null){
            instance = new BuildingConfiguration();
        }
        return instance;
    }

}
