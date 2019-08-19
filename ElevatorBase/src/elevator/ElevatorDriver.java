package elevator;

import gui.ElevatorDisplay;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Yi Zheng
 */

public class ElevatorDriver {

    private static long initTime;
    private static ArrayList<Person> people = new ArrayList<>();
    private static int personCounter;
    private static Random randomObj = new Random(1234);
    private static int simulationDurationTime;
    private static int personCreationRate;
    private static ArrayList<Request> crowdedOutRequests = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException{

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
//
//        int numFloors = (int)(long) jObj.get("numFloors");
//        int numElevators = (int)(long) jObj.get("numElevators");
//        int capacity = (int)(long) jObj.get("capacity");
//        long timePerFloor = (long) jObj.get("timePerFloor");
//        long doorOpenTime = (long) jObj.get("doorOpenTime");
//        long timeout = (long) jObj.get("timeout");
        simulationDurationTime = (int)(long) jObj.get("simulationDurationTime");
        personCreationRate = (int)(long) jObj.get("personCreationRate");
//        //System.out.printf("%d %d %d %d %d %d",numFloors, numElevators,capacity,timePerFloor,doorOpenTime,timeout);
//
        try {
            Building.getInstance();
            ElevatorController.getInstance();
        } catch (InvalidParamException e) {
            System.out.println("Please check input file: "+e.getMessage());
            return;
        }


        try {
            ElevatorDisplay.getInstance().initialize(Building.getInstance().getNumFloors());
        } catch (InvalidParamException e) {
            e.printStackTrace();
            return;
        }
//        ElevatorDisplay.getInstance().addElevator(1,1);
//        ElevatorDisplay.getInstance().addElevator(2,1);
//        ElevatorDisplay.getInstance().addElevator(3,1);
//        ElevatorDisplay.getInstance().addElevator(4,1);

//        for (int i=0; i<Building.getInstance().numElevators; i++){
//            ElevatorDisplay.getInstance().addElevator(i+1,1);
//        }
        try {
            for (int i=0; i<ElevatorController.getInstance().getNumElevators(); i++){
                ElevatorDisplay.getInstance().addElevator(i+1,1);
            }
        } catch (InvalidParamException e) {
            System.out.println("Please check input file: "+e.getMessage());
        }
//        try {
//            ElevatorController.getInstance();
//        } catch (InvalidParamException e) {
//            System.out.println("Please check input file: "+e.getMessage());
//            return;
//        }
        try {
            part2();
        } catch (InvalidParamException | InvalidStateException e) {
            System.out.println(e.getMessage());
        }
        ElevatorDisplay.getInstance().shutdown();
    }

    private static void part2() throws InvalidParamException, InvalidStateException{
        initTime = System.currentTimeMillis();
        for (int i=0; i<simulationDurationTime; i++){
            if (!crowdedOutRequests.isEmpty()){
                if (randomObj.nextInt()%4 == 0) {
                    for (Request r : crowdedOutRequests) {
                        addRequest(r);
                    }
                    crowdedOutRequests.clear();
                }
            }
            if(i%personCreationRate == 0){
                int startFloor = (int) (randomObj.nextDouble() * Building.getNumFloors()+1);
                int endFloor = (int) (randomObj.nextDouble() * Building.getNumFloors()+1);
                while(startFloor == endFloor){
                    endFloor = (int) (randomObj.nextDouble() * Building.getNumFloors()+1);
                }
                addPerson(startFloor, endFloor);
            }

            ElevatorController.getInstance().operateElevators(1000);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        while (!ElevatorController.getInstance().isDone()){
            System.out.println("*** Waiting for complete");
            if (!crowdedOutRequests.isEmpty()){
                if (randomObj.nextInt()%4 == 0) {
                    for (Request r : crowdedOutRequests) {
                        addRequest(r);
                    }
                    crowdedOutRequests.clear();
                }
            }
            ElevatorController.getInstance().operateElevators(1000);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long totalWaitTime = 0;
        long minWaitTime = people.get(0).computeWaitTime();
        long maxWaitTime = people.get(0).computeWaitTime();
        Person minWaitPerson = people.get(0);
        Person maxWaitPerson = people.get(0);

        long totalRideTime = 0;
        long minRideTime = people.get(0).computeRideTime();
        long maxRideTime = people.get(0).computeRideTime();
        Person minRidePerson = people.get(0);
        Person maxRidePerson = people.get(0);

        for (Person p: people){
            long waitTime = p.computeWaitTime();
            long rideTime = p.computeRideTime();

            if (waitTime<=minWaitTime){
                minWaitTime = waitTime;
                minWaitPerson = p;
            }
            if(waitTime>maxWaitTime){
                maxWaitTime = waitTime;
                maxWaitPerson = p;
            }

            if (rideTime<=minRideTime){
                minRideTime = rideTime;
                minRidePerson = p;
            }
            if(rideTime>maxRideTime){
                maxRideTime = rideTime;
                maxRidePerson = p;
            }
            totalWaitTime = totalWaitTime + waitTime;
            totalRideTime = totalRideTime + rideTime;
        }
        long averageWaitTime = totalWaitTime/people.size();

        System.out.printf("Avg Wait Time: %4.1f sec\n", (averageWaitTime/1000.0));

        long averageRideTime = totalRideTime/people.size();

        System.out.printf("Avg Ride Time: %4.1f sec\n", (averageRideTime/1000.0));

        System.out.printf("Min Wait Time: %4.1f sec (%s)\n", (minWaitTime/1000.0), minWaitPerson);

        System.out.printf("Min Ride Time: %4.1f sec (%s)\n", (minRideTime/1000.0), minRidePerson);

        System.out.printf("Max Wait Time: %4.1f sec (%s)\n", (maxWaitTime/1000.0), maxWaitPerson);

        System.out.printf("Max Ride Time: %4.1f sec (%s)\n", (maxRideTime/1000.0), maxRidePerson);

        System.out.println("Person     Start Floor        End Floor       Direction     Wait Time     Ride Time     Total Time");

        for (Person p: people){
            p.printStats();
        }
    }


//    private static void test1() throws InterruptedException, InvalidParamException {
//
//        for (int i = 0; i < 40; i++) { // This will run for 40 seconds
//
//            if (i == 0) {
//                addPerson(1, 10, 1);  // startFloor, EndFloor, elevNum)
//            }
//
//            try {
//                ElevatorController.getInstance().operateElevators(1000); // Tell elevators to operate for 1 sec
//            } catch (InvalidParamException e) {
//                System.out.println("Please check input file: "+e.getMessage());
//                return;
//            }
//            Thread.sleep(1000); // Sleep for 1 sec
//        }
//    }

//    private static void test2() throws InterruptedException, InvalidParamException {
//
//        for (int i = 0; i < 70; i++){
//            switch(i){
//                case 0:
//                    addPerson(20,5,2);
//                    break;
//
//                case 5:
//                    addPerson(15,19,2);
//                    break;
//            }
//            try {
//                ElevatorController.getInstance().operateElevators(1000); // Tell elevators to operate for 1 sec
//            } catch (InvalidParamException e) {
//                System.out.println("Please check input file: "+e.getMessage());
//                return;
//            }
//            Thread.sleep(1000); // Sleep for 1 sec
//        }
//    }

//    private static void test3() throws InterruptedException, InvalidParamException {
//
//        for (int i = 0; i < 70; i++){
//            switch(i){
//                case 0:
//                    addPerson(20,1,3);
//                    break;
//
//                case 25:
//                    addPerson(10,1,3);
//                    break;
//            }
//            try {
//                ElevatorController.getInstance().operateElevators(1000); // Tell elevators to operate for 1 sec
//            } catch (InvalidParamException e) {
//                System.out.println("Please check input file: "+e.getMessage());
//                return;
//            }
//            Thread.sleep(1000); // Sleep for 1 sec
//        }
//    }

//    private static void test4() throws InterruptedException, InvalidParamException {
//
//        for (int i = 0; i < 80; i++){
//            switch(i){
//                case 0:
//                    addPerson(1,10,1);
//                    break;
//
//                case 5:
//                    addPerson(8,17,1);
//                    break;
//
//                case 6:
//                    addPerson(1,9,4);
//                    break;
//
//                case 32:
//                    addPerson(3,1,4);
//                    break;
//            }
//            try {
//                ElevatorController.getInstance().operateElevators(1000); // Tell elevators to operate for 1 sec
//            } catch (InvalidParamException e) {
//                System.out.println("Please check input file: "+e.getMessage());
//                return;
//            }
//            Thread.sleep(1000); // Sleep for 1 sec
//        }
//    }

    public static void logOutput(String s){
        System.out.println(getTimeStamp() + " " +s);
    }

    public static void putBackCrowdedOutRequest(Request r) throws InvalidParamException{

        if (r.getDir() == Direction.IDLE){
            throw new InvalidParamException("Idle request cannot be added.");
        }
        if(r.getFloorNum() < 1 || r.getFloorNum() > Building.getNumFloors()){
            throw new InvalidParamException("Floor number of the request out of range.");
        }
        crowdedOutRequests.add(r);
    }

    private static String getTimeStamp(){
        long now = System.currentTimeMillis() - initTime;

        long hours = now/3600000;
        now -= (hours * 3600000);

        long minutes = now/60000;
        now -= (minutes*60000);

        long seconds = now/1000;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);

    }

    private static void addRequest(Request r) throws InvalidParamException{
        ElevatorController.getInstance().addFloorRequest(r);
    }

    private static void addPerson(int start, int end) throws InvalidParamException {
        Direction d = Direction.determineDirection(start,end);
        Person p = null;
        try {
            p = new Person("P" + (++personCounter), start, end);
        } catch (InvalidParamException e) {
            e.getMessage();
            return;
        }
        logOutput("Person "+p+" created on Floor "+start+", wants to go "+d+" to Floor "+end);
        logOutput("Person "+p+" presses "+d+" button on Floor "+start);
        people.add(p);
        Building.getInstance().addPerson(p,start);

        try {
            ElevatorController.getInstance().addFloorRequest(new Request(start,d));
        } catch (InvalidParamException e) {
            System.out.println("Please check input file: "+e.getMessage());
            return;
        }
    }
}
