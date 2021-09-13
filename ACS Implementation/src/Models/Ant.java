package Models;


import java.util.ArrayList;

public class Ant {

    private ArrayList<Integer> solution;
    private ArrayList<int[]> route;
    private ArrayList<Integer> bestSolution;
    private ArrayList<int[]> bestRoute;
    private double capacity;
    private double usedCapacity;
    private double fuelCapacity;
    private double fuel;
    private double fuelConsumption;
    private int xPos;
    private int yPos;
    private double velocity;
    private boolean malfunction;

    public Ant (double capacity, double fuelCapacity, double fuelConsumption, int xPos, int yPos, double velocity){
        solution = new ArrayList<>();
        bestSolution = new ArrayList<>();
        route = new ArrayList<>();
        bestRoute = new ArrayList<>();
        this.capacity = capacity;
        //El camion sale con el tanque lleno
        this.usedCapacity = capacity;
        this.fuelCapacity = fuelCapacity;
        this.fuel = fuelCapacity;
        this.fuelConsumption = fuelConsumption;
        this.xPos = xPos;
        this.yPos = yPos;
        this.velocity = velocity;
        this.malfunction = false;
    }

    public ArrayList<Integer> getSolution() {
        return solution;
    }

    public void setSolution(ArrayList<Integer> solution) {
        this.solution = solution;
    }

    public ArrayList<Integer> getBestSolution() {
        return bestSolution;
    }

    public void setBestSolution(ArrayList<Integer> bestSolution) {
        this.bestSolution = bestSolution;
    }

    public void addSolution(int pos){
        this.solution.add(pos);
    }

    public int getLastSolution(int numAlmacen){
        int last;
        int size = this.solution.size();
        if(size == 0)
            last = -1-numAlmacen;
        else
            last = this.solution.get(size - 1);
        return last;
    }

    public ArrayList<int[]> getBestRoute() {
        return bestRoute;
    }

    public void setBestRoute(ArrayList<int[]> bestRoute) {
        this.bestRoute = bestRoute;
    }

    public ArrayList<int[]> getRoute() {
        return route;
    }

    public void setRoute(ArrayList<int[]> route) {
        this.route = route;
    }

    public void addRoute(int [] pos){
        this.route.add(pos);
    }

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public double getUsedCapacity() {
        return usedCapacity;
    }

    public void setUsedCapacity(double usedCapacity) {
        this.usedCapacity = usedCapacity;
    }

    public double getFuelCapacity() {
        return fuelCapacity;
    }

    public void setFuelCapacity(double fuelCapacity) {
        this.fuelCapacity = fuelCapacity;
    }

    public double getFuel() {
        return fuel;
    }

    public void setFuel(double fuel) {
        this.fuel = fuel;
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

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public boolean isMalfunction() {
        return malfunction;
    }

    public void setMalfunction(boolean malfunction) {
        this.malfunction = malfunction;
    }

    public double getFuelConsumption() {
        return fuelConsumption;
    }

    public void setFuelConsumption(double fuelConsumption) {
        this.fuelConsumption = fuelConsumption;
    }

    public void changeSolution(){
        bestRoute = route;
        bestSolution = solution;
        //Limpiando el actual
        route = new ArrayList<>();
        solution = new ArrayList<>();

    }

    public double calcFitness(){
        //Calcular mejor fitness
        double fitnessCur = 0 ;
        double sizeSolution = route.size();
        if(sizeSolution != 0){
            fitnessCur = 1/sizeSolution;
        }
        return fitnessCur;
    }

    public void addRoute(ArrayList<int[]> routeTemp){
        for (int [] x:
                routeTemp
             ) {
            this.route.add(x);
        }
    }

}
