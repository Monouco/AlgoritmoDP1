package Models;


import java.util.ArrayList;

public class Ant {

    private ArrayList<Integer> solution;
    private ArrayList<Double> solutionGLP;
    private ArrayList<int[]> route;
    private ArrayList<Integer> bestSolution;
    private ArrayList<Double> bestSolutionGLP;
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
    private double antWeight;
    private double glpWeight;
    private double fuelPrev;

    public Ant (double capacity, double fuelCapacity, int xPos, int yPos, double velocity, double antWeight, double glpWeight){
        solution = new ArrayList<>();
        bestSolution = new ArrayList<>();
        route = new ArrayList<>();
        bestRoute = new ArrayList<>();
        solutionGLP = new ArrayList<>();
        bestSolutionGLP = new ArrayList<>();
        this.capacity = capacity;
        //El camion sale con el tanque lleno
        this.usedCapacity = capacity;
        this.fuelCapacity = fuelCapacity;
        this.fuel = fuelCapacity;
        this.fuelConsumption = 0;
        this.xPos = xPos;
        this.yPos = yPos;
        this.velocity = velocity;
        this.malfunction = false;
        this.antWeight = antWeight;
        this.glpWeight = glpWeight;
        this.fuelPrev = 0;
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

    public double getAntWeight() {
        return antWeight;
    }

    public void setAntWeight(double antWeight) {
        this.antWeight = antWeight;
    }

    public double getGlpWeight() {
        return glpWeight;
    }

    public void setGlpWeight(double glpWeight) {
        this.glpWeight = glpWeight;
    }

    public void changeSolution(){
        bestRoute = route;
        bestSolution = solution;
        bestSolutionGLP = solutionGLP;
        //Limpiando el actual
        route = new ArrayList<>();
        solution = new ArrayList<>();
        solutionGLP = new ArrayList<>();

    }

    public double calcFitness(){
        //Calcular mejor fitness
        double fitnessCur = 0 ;
        int numCl = numClients();
        //No usaremos el tamaño de la ruta, sino la cantidad de petroleo gastado
        double sizeSolution = route.size();
        if(sizeSolution != 0){
            //fitnessCur = 1/sizeSolution;
            fitnessCur = numCl*this.fuelCapacity/2 - this.fuelConsumption;
            if(fitnessCur<0){
                //Evitamos que haya fitness negativo
                fitnessCur = -1/fitnessCur;
            }
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

    public void clearSolution(){
        this.solution = new ArrayList<>();
        this.route = new ArrayList<>();
        this.solutionGLP = new ArrayList<>();
    }

    public void printSolution(Map map, char car){
        Map solMap = new Map(map);
        solMap.insertSolution(this,car);
        solMap.printMap();

    }

    public ArrayList<Double> getSolutionGLP() {
        return solutionGLP;
    }

    public void setSolutionGLP(ArrayList<Double> solutionGLP) {
        this.solutionGLP = solutionGLP;
    }

    public ArrayList<Double> getBestSolutionGLP() {
        return bestSolutionGLP;
    }

    public void setBestSolutionGLP(ArrayList<Double> bestSolutionGLP) {
        this.bestSolutionGLP = bestSolutionGLP;
    }

    public void addSolutionGLP(double glp){
        this.solutionGLP.add(glp);
    }

    public void calcCost(int size){
        double consumption = calcFuelConsumption(size);

        this.fuel -= consumption;
        this.fuelConsumption += consumption;

    }

    private double calcFuelConsumption(int size){
        int solSize = solutionGLP.size();
        double consumption;
        if(solSize > 0){
            consumption = size * //distancia recorrida
                    (antWeight + (glpWeight/capacity) //peso actual del camion
                            * solutionGLP.get(solSize-1))/150;
        }
        else{
            consumption = size * //distancia recorrida
                    (antWeight + (glpWeight/capacity) //peso actual del camion
                            * capacity)/150; //tomaremos como que el camion sale con el tanque lleno
        }
        return consumption;
    }

    private int numClients(){
        int numCl = 0;
        for (int c:
             this.solution) {
            if(c >=0) numCl++;
        }
        return numCl;
    }

    public boolean tryOrder(int size){
        double consumption = calcFuelConsumption(size);
        if (fuel - consumption > 0)
            return true;//es posible
        return false;
    }

    public double deleteLast(){
        int size = solution.size();
        int lastOrd;
        int [] lastCoor;
        int solDist = route.size();
        double consumption;
        int dist = 0;
        double glp;
        //no hay que eliminar
        if(size == 0)
            return 0;
        //quitamos la ultima solucion
        lastOrd = solution.remove(size-1);
        //Tenemos con cuanto fuimos
        glp = solutionGLP.remove(size-1);
        //volvemos a asignar el glp al de antes
        usedCapacity = glp;
        fuel = fuelPrev;
        while(solDist > 0){
            lastCoor = route.remove(solDist - 1);
            dist++;
            //Si se encontro el primer elemento agregado por la nueva ruta
            if(lastCoor.length == 3)
                break;
            solDist--;
        }

        consumption = calcFuelConsumption(dist);
        fuelConsumption -= consumption;

        return this.capacity - glp;

    }

    public double refill(double available){
        double leftGLP;
        double required = this.capacity - this.usedCapacity;
        this.fuelPrev = this.fuel;
        this.fuel = this.fuelCapacity;
        if(available > required) {
            leftGLP = available - required;
            this.usedCapacity = this.capacity;
        }
        else{
            this.usedCapacity = this.usedCapacity + available;
            leftGLP = 0;
        }

        return leftGLP;
    }

}
