package elevator;

/**
 *
 * @author Yi Zheng
 */

public class Request implements Comparable<Request>{

    private int floorNum;
    private Direction dir;

    public Request(int fn, Direction dir) throws InvalidParamException{
        this.dir = dir;
        setFloorNum(fn);
    }

    private void setFloorNum(int floorNum) throws InvalidParamException{
        if(floorNum>0 && floorNum<=Building.getNumFloors()) {
            this.floorNum = floorNum;
        }
        else {
            throw new InvalidParamException("Floor number out of bounds.");
        }
    }

    public int getFloorNum() {
        return floorNum;
    }

    public Direction getDir() {
        return dir;
    }

    @Override
    public int compareTo(Request r) {
        return this.floorNum - r.floorNum;
    }


    @Override
    public boolean equals(Object o){
        if (o instanceof Request) {
            Request r = (Request)o;
            return this.floorNum == r.floorNum && this.dir == r.dir;
        }
        return false;
    }

    @Override
    public int hashCode(){
        int hash = 7;
        hash = 97*hash + floorNum;
        hash = 97*hash + dir.hashCode();
        return hash;
    }
}

