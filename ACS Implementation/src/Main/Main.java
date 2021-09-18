package Main;

import Models.Ant;
import Models.Map;
import Models.Order;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.lang.Math;

public class Main {
    public static void main(String[] args) {

        //Lectura de archivo e inicializacion del mapa
        Map mapa1 = new Map(50, 70);

        //Leer mapa
        System.out.println("");
        //mapa1.readRoadblocksFromFile("D:\\other things\\dp1\\ACS Implementation\\bloqueos.txt");
        //System.out.println("MAPA BLOQUEADO");
        //mapa1.printMap();
        //Otros parametros
        int numAlmacenes = 1;
        double [][] pheromone;
        ArrayList<Order> orders;
        int numOrders = 0;

        //Conseguimos la cantidad de ordenes
        orders = new ArrayList<>();
        //Leer pedidos
        orders = readOrdersFromFile("C:\\Users\\leo\\Desktop\\loe\\2021-2\\DP1\\AlgoritmoDP1\\ACS Implementation\\pedidos.txt");
        numOrders = orders.size();

        //Inicializamos la feromona
        //System.out.println(pheromone[0][0]);

        //Considerando los almacenes dentro de la feromona
        pheromone = new double[numOrders + numAlmacenes][numOrders + numAlmacenes];

        /*
        ##################################################################
        Consideremos que el petroleo es ilimitado y no hay averias
        por ahora feromona = # nodos
        ##################################################################
         */

        //Definicion de otros parametros
        int k; //numero de hormigas de la colonia
        int tipoa = 2;
        int tipob = 4;
        k = tipoa + tipob;
        int steps = numOrders*3/2; //numero de ciclos en los que se calcularan las rutas
        int cycles = 2000;

        //Inicializamos la flota
        ArrayList<Ant> camiones = new ArrayList<Ant>();

        //CAMIONES TIPO A
        for(int i=0; i<tipoa; i++){
            //Inicializamos con valores fijos para todos
            Ant camion = new Ant(25, 25, 25/150, mapa1.getPlantaPrincipal()[0], mapa1.getPlantaPrincipal()[1], 30);
            camiones.add(camion);
        }

        //CAMIONES TIPO B
        for(int i=0; i<tipob; i++){
            //Inicializamos con valores fijos para todos
            Ant camion = new Ant(15, 25, 25/150, mapa1.getPlantaPrincipal()[0], mapa1.getPlantaPrincipal()[1], 30);
            camiones.add(camion);
        }

        int siguienteOrden = 0;
        int ordenAnterior = 0;
        int numAtendidos = 0;

        int bestAnt;
        double bestFit;
        //Probar despues con las n mejores hormigas por ciclo
        //int nBest = (int) Math.ceil((double) k/2);

        int xIni, xDes, yIni, yDes;
        int [] coordenate;
        ArrayList<int []> ruta = new ArrayList<>();
        double glpDisponible = 0;
        double fitnessTemp, fitnessCur;
        int highestNum = 0;
        Order lastOrden = null;
        Order curOrden = null;
        Ant camion = null;
        double globalFitness = 0.0;
        //Procedimiento ciclico
        for(int i = 0; i < cycles; i++){
            //falta ordenar
            ArrayList<Order> ordenes = (ArrayList<Order>) orders.clone();
            //ponemos el glp aca
            double [] pedidos = new double[numOrders];
            for(int n = 0; n < numOrders; n++){
                pedidos[n] = ordenes.get(n).getGlp();
            }

            numAtendidos = 0;

            //Armamos las soluciones por pasos
            for(int j = 0; j < steps; j++){
                //Por cada una de las hormigas
                for(int l = 0; l < k; l++){
                    //Si ya se atendieron todos los pedidos
                    if(numAtendidos == numOrders) break;

                    camion = camiones.get(l);

                    glpDisponible = camion.getUsedCapacity();
                    //Aca va lo de las 8 horas
                    if(glpDisponible == 0.0) continue;

                    siguienteOrden = getNextOrder(camion, pheromone, ordenes, numOrders, pedidos, numAlmacenes, mapa1);

                    //Esto es el colapso logistico
                    if(siguienteOrden == -1) continue;

                    //Se aumenta la ruta
                    ordenAnterior = camion.getLastSolution(numAlmacenes);

                    camion.addSolution(siguienteOrden);

                    if(pedidos[siguienteOrden] <= glpDisponible){
                        camion.setUsedCapacity(glpDisponible - pedidos[siguienteOrden]);
                        pedidos[siguienteOrden] = 0;
                    }
                    else{
                        pedidos[siguienteOrden] -= glpDisponible;
                        camion.setUsedCapacity(0);
                    }
                    if(pedidos[siguienteOrden] == 0.0) numAtendidos++;
                    curOrden = ordenes.get(siguienteOrden);

                    if(ordenAnterior < 0){
                        xIni = camion.getxPos();
                        yIni = camion.getyPos();
                        coordenate = new int[2];
                        coordenate[0] = xIni;
                        coordenate[1] = yIni;
                        camion.addRoute(coordenate);
                    }
                    else{
                        lastOrden = ordenes.get(ordenAnterior);
                        xIni = lastOrden.getDesX();
                        yIni = lastOrden.getDesY();
                    }
                    xDes = curOrden.getDesX();
                    yDes = curOrden.getDesY();
                    //Calculo de Manhattan
                    //Recordar hacer esto con A*
                    ruta = manhattanPath(xIni,yIni,xDes,yDes);
                    camion.addRoute(ruta);
                    //camiones.set(l,camion);
                }
                // Se atendieron todos los pedidos
                if(numAtendidos == numOrders) break;
            }

            fitnessTemp = 0;
            fitnessCur = 0;
            bestAnt = 0;
            bestFit = 0;
            //En otro momento hare lo de la wea de A*
            for(int l = 0; l < k; l++){
                camion = camiones.get(l);
                ordenAnterior = camion.getLastSolution(numAlmacenes);
                if(ordenAnterior < 0){
                    if(ordenAnterior == -1-numAlmacenes){
                        xIni = camion.getxPos();
                        yIni = camion.getyPos();
                        coordenate = new int[2];
                        coordenate[0] = xIni;
                        coordenate[1] = yIni;
                        camion.addRoute(coordenate);
                    }
                    else{
                        //Se encuentra en una planta, verificar
                        xIni = mapa1.getPlantaPrincipal()[0];
                        yIni = mapa1.getPlantaPrincipal()[1];
                    }
                }
                else{
                    lastOrden = ordenes.get(ordenAnterior);
                    xIni = lastOrden.getDesX();
                    yIni = lastOrden.getDesY();
                }
                //Posiciones del almacen principal
                xDes = mapa1.getPlantaPrincipal()[0];
                yDes = mapa1.getPlantaPrincipal()[1];
                ruta = manhattanPath(xIni,yIni,xDes,yDes);
                camion.addRoute(ruta);

                //calculando fitnessGlobal
                fitnessCur = camion.calcFitness();
                fitnessTemp = fitnessTemp + fitnessCur;
                //System.out.println("Para el camion "+l+" en it "+i+" el fitness: "+fitnessTemp);

                //Aca guardamos a la mejor hormiga, pero opino que deberiamos tener las n mejores hormigas
                if(fitnessCur > bestFit){
                    bestAnt = l;
                    bestFit = fitnessCur;
                }

                //Recordar este cambio para el momento en que se tengan que agregar pedidos espontaneos
                camion.setUsedCapacity(camion.getCapacity());

            }

            if(numAtendidos != numOrders){
                //Es solucion inviable y colapso logistico
                fitnessTemp = -1;
                bestFit = -1;
            }

            //Actualizamos la feromona
            if(bestFit >= 0){
                highestNum = numOrders;
                updatePheromone(pheromone, 1, numOrders, bestFit, camiones.get(bestAnt),numAlmacenes);
            }


            //Cambiando la mejor solucion de todos los camiones
            for(int l = 0; l < k; l++){
                //Cambiando a mejor solucion
                if(fitnessTemp > globalFitness){
                    camiones.get(l).changeSolution();
                }
                camiones.get(l).clearSolution();
            }

        }

        System.out.println("Pedidos atendidos: "+ highestNum);

        for(int l = 0; l < k; l++){
            camion = camiones.get(l);
            System.out.println("Camion: "+ l);
            for (int pedido:
                    camion.getBestSolution()
                 ) {
                System.out.print(pedido + " -> ");
            }
            System.out.println(" |");

            for (int [] x:
                    camion.getBestRoute()
                 ) {
                System.out.print("(" + x[0] + "," + x[1] + ") -> ");
            }
            System.out.println(" |");
            camion.printSolution(mapa1,(char) (l + '@') );
            //mapa1.insertSolution(camion, (char) (l + '@'));
            //mapa1.printMap();

        }

        //mapa1.printMap();

    }

    private static int getNextOrder(Ant camion, double[][] pheromone, ArrayList<Order> ordenes, int numOrders, double[] pedidos, int numAlmacenes, Map mapa){
        double prob = 0.0;
        double chosen = 0.0;
        double sum = 0.0;
        int next = -1;
        double [] probabilidades = new double [numOrders];
        int camino = camion.getRoute().size();
        double tiempoActual = ( ( (camino == 0 ) ? 1 : camino) - 1) / camion.getVelocity();
        //calculando las probabilidades de todos
        for(int m = 0; m < numOrders; m++){
            //pedido atendido
            if(pedidos[m] == 0.0 || camion.getLastSolution(numAlmacenes) == m)
                probabilidades[m]=0;
            else
                //pedido no atendido, evaluar
                probabilidades[m] = calcProb(camion, ordenes, m, pheromone, tiempoActual, numAlmacenes, mapa); // atractividad + feromona
            sum += probabilidades[m];
        }

        chosen = Math.random();

        for(int m = 0; m < numOrders; m++){
            //normalizando y hallando la verdadera probabilidad
            prob += (probabilidades[m])/sum;
            if(chosen < prob){
                next = m;
                break;
            }

        }

        return next;
    }

    private static double calcProb(Ant camion, ArrayList<Order> ordenes, int ordenPedido, double [][] pheromone, double tiempoActual, int numAlmacenes, Map mapa){
        double atractividad = 0;
        double pheromoneCur;
        double prob = 0;
        //Calculando atractividad
        int last = camion.getLastSolution(numAlmacenes);
        Order pedido = ordenes.get(ordenPedido);
        Order pedidoAnt = null;
        int manhattan = 0;
        int coor [] = new int[2];
        double tiempoRestante = 0;
        //No olvidar inicializar localizacion del camion
        if(last <= -1){
            if(last == -1-numAlmacenes)
                last++;
            //Cambiar metodo para obtener el almacen actual
            coor = mapa.getPlantaPrincipal();
            //x = posX almacen actual, y = posY almacen actual
            manhattan = Math.abs(coor[0] - pedido.getDesX()) + Math.abs(coor[1] - pedido.getDesY());
        }
        else{
            pedidoAnt = ordenes.get(last);
            manhattan = Math.abs(pedidoAnt.getDesX() - pedido.getDesX()) + Math.abs(pedidoAnt.getDesY() - pedido.getDesY());

        }

        //Calculo temporal
        tiempoRestante = pedido.getDeadLine() - tiempoActual;
        //Si sale negativo, inviable
        if(tiempoRestante <= 0){
            return 0;
        }

        //Analizar el caso xd
        if(manhattan == 0) return 0;

        pheromoneCur = pheromone[last + numAlmacenes][ordenPedido + numAlmacenes];

        prob = 2 * 1 / tiempoRestante + 1 / manhattan + pheromoneCur;

        return prob;
    }

    private static ArrayList<int[]> manhattanPath(int xIni, int yIni, int xDes, int yDes){
        ArrayList<int[]> ruta = new ArrayList<>();
        int [] nodo = {xIni,yIni};
        int xTemp, yTemp;
        for(int i=0; Math.abs(i) < Math.abs(xDes - xIni);){
            if(xIni<xDes){
                i++;
            }
            else{
                i--;
            }
            nodo = new int[2];
            nodo[0] = xIni+i;
            nodo[1] = yIni;
            ruta.add(nodo);

        }
        xTemp = nodo[0];
        yTemp = nodo[1];
        for(int i=0; Math.abs(i) < Math.abs(yDes - yTemp);){
            if(yTemp<yDes){
                i++;
            }
            else{
                i--;
            }
            nodo = new int[2];
            nodo[0] = xTemp;
            nodo[1] = yTemp + i;
            ruta.add(nodo);

        }

        return ruta;

    }

    private static void updatePheromone(double pheromone[][], double evaporationRate, int numOrders, double fitness, Ant bestAnt, int numAlmacenes){
        int x = -100;
        //(1-P)*Fitness + fitness
        for (int p:
                bestAnt.getSolution()
             ) {
            //Si es el primer elemento del arreglo, x se tiene que cambiar
            if(x == -100){
                x = p;
            }
            else{
                //Se calcula la feromona
                pheromone[x+numAlmacenes][p+numAlmacenes] = (1-evaporationRate) * pheromone[x+numAlmacenes][p+numAlmacenes] + fitness;
                x = p;
            }
        }
    }

    private static Order parseOrder(String line){
        Order or = new Order();

        String xi_s="";
        String yi_s="";
        String glp_s="";
        String plazo_s="";
        int x=0;
        int y=0;
        int glp=0;
        int plazo=0;

        //Datos estaticos
        or.setDia(Integer.parseInt(line.substring(0,2)));
        or.setHora(Integer.parseInt(line.substring(3,5)));
        or.setMinuto(Integer.parseInt(line.substring(6,8)));

        //Parseo de nodos
        for(int i = 9; i < line.length();){

            //Parseo de pos x
            for(int j = i;j < line.length();j++){
                if(line.charAt(j)==','){
                    x = Integer.parseInt(xi_s);
                    or.setDesX(x);
                    i=j+1; //Me quedo en la sig posicion
                    break;
                }
                xi_s += line.charAt(j);
            }

            //Parseo de y
            for(int j = i;j < line.length();j++){
                if(line.charAt(j)==','){
                    y = Integer.parseInt(yi_s);
                    or.setDesY(y);
                    i=j+1; //Me quedo en la sig posicion
                    break;
                }
                yi_s += line.charAt(j);
            }

            //Parseo de glp
            for(int j = i;j < line.length();j++){
                if(line.charAt(j)==','){
                    glp = Integer.parseInt(glp_s);
                    or.setGlp(glp);
                    i=j+1; //Me quedo en la sig posicion
                    break;
                }
                glp_s += line.charAt(j);
            }

            //Parseo de plazo/deadline
            for(int j = i;j < line.length();j++){
                if(j+1==line.length()){
                    plazo_s += line.charAt(j);
                    plazo = Integer.parseInt(plazo_s);
                    or.setDeadLine(plazo);
                    i=j+1; //Me quedo en la sig posicion
                    break;
                }
                plazo_s += line.charAt(j);
            }

        }

        return or;
    }

    public static ArrayList<Order> readOrdersFromFile(String filename){
        ArrayList<Order> pedidos = new ArrayList<>();
        Order pedido = null;

        try{
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String s;
            while((s = br.readLine()) != null){
                pedido = parseOrder(s);
                //System.out.println(pedido.getDesX() + " " + pedido.getDesY());
                pedidos.add(pedido);
            }
            br.close();
        }catch(Exception e){
            System.out.println(e);
            return pedidos;
        }

        return pedidos;
    }

}
