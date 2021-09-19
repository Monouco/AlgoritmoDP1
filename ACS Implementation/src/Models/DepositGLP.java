package Models;

public class DepositGLP {

    private int xPos;
    private int yPos;
    private double capacity;
    private double remaining;

    public DepositGLP(int xPos, int yPos, double capacity){
        this.xPos = xPos;
        this.yPos = yPos;
        this.capacity = capacity;
        this.remaining = capacity;
    }

    public int getxPos() {
        return xPos;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public void setyPos(int yPos) {
        this.yPos = yPos;
    }

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public double getRemaining() {
        return remaining;
    }

    public void setRemaining(double remaining) {
        this.remaining = remaining;
    }

    public int [] getCoor(){
        int [] coor = new int[2];
        coor[0] = xPos;
        coor[1] = yPos;
        return coor;
    }

}
