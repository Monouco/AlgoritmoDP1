package Solutions;

import Models.Ant;
import Models.DepositGLP;
import Models.Map;
import Models.Order;

import java.util.ArrayList;
import java.util.Random;

public class ACSAlgorithm {
    private int numAlmacenes;
    private double[][] pheromone;
    private int[] coorAlmacen;
    private int hTurno;
    private int highestNum;
    private double bestFitness;
    //private Random rand;

    public ACSAlgorithm(int numAlmacenes, int numOrders, int [] coorAlmacen, int hTurno){
        this.numAlmacenes = numAlmacenes;
        this.coorAlmacen = coorAlmacen;
        //Considerando los almacenes dentro de la feromona
        this.pheromone = new double[numOrders + numAlmacenes][numOrders + numAlmacenes];
        this.hTurno = hTurno;
        //rand = new Random(seed);
    }

    public ArrayList<Ant> findSolution(ArrayList<Ant> camiones, ArrayList<Order> ordenes, Map mapa1, int cycles, int steps, int k, double evaporationRate) {
        int xIni, xDes, yIni, yDes, numAtendidos, siguienteOrden, ordenAnterior, numOrders = ordenes.size();
        int[] coordenate;
        ArrayList<int[]> ruta = new ArrayList<>();
        double glpDisponible = 0;
        double fitnessTemp, fitnessCur;
        int  bestAnt;
        Order lastOrden = null;
        Order curOrden = null;
        Ant camion = null;
        ArrayList<DepositGLP> depositos = mapa1.getDeposits();
        DepositGLP deposito;
        double globalFitness = 0.0, bestFit, reservaActual;
        double [] reservas;
        double[] pedidos;
        AstarSearch aStar = new AstarSearch(mapa1);
        //Procedimiento ciclico
        for (int i = 0; i < cycles; i++) {
            //ponemos el glp aca
            pedidos = new double[numOrders];
            for (int n = 0; n < numOrders; n++) {
                pedidos[n] = ordenes.get(n).getGlp();
            }
            reservas = new double[numAlmacenes];

            for (int n = 0; n < numAlmacenes; n++){
                //Obtenemos el glp disponible de cada almacen
                reservas[n] = depositos.get(n).getRemaining();
            }

            numAtendidos = 0;

            //Armamos las soluciones por pasos
            //for (int j = 0; j < steps; j++) {
                //Por cada una de las hormigas
                for (int l = 0; l < k; l++) {
                    //Si ya se atendieron todos los pedidos
                    if (numAtendidos == numOrders) break;

                    camion = camiones.get(l);

                    while(numAtendidos != numOrders) {

                        glpDisponible = camion.getUsedCapacity();
                        //Aca va lo de las 8 horas y petroleo restante
                        //if (glpDisponible == 0.0 ) continue;

                        siguienteOrden = getNextOrder(camion, ordenes, numOrders, pedidos, numAlmacenes, mapa1);

                        //Se aumenta la ruta
                        ordenAnterior = camion.getLastSolution(numAlmacenes);

                        //Esto es el colapso logistico o no se puede atender por capacidad actual
                        if (siguienteOrden == -1 - numAlmacenes) {
                            //Si el anterior era negativo, entonces es porque no ha salido o ha ido a recargar antes y aun asi no pudo
                            if (ordenAnterior < 0) {
                                reservaActual = camion.deleteLast();
                                if (ordenAnterior > -numAlmacenes) {
                                    reservas[ordenAnterior + numAlmacenes] = reservas[ordenAnterior + numAlmacenes] + reservaActual;
                                }
                                break;
                            }
                            //Consideraremos mandarlo a una cisterna intermedia o al almacen principal
                            siguienteOrden = refill(camion, mapa1, reservas, ordenes, depositos);
                            if (siguienteOrden == -1 - numAlmacenes)
                                break;

                            camion.addSolutionGLP(camion.getUsedCapacity());

                            reservaActual = camion.refill(reservas[siguienteOrden + numAlmacenes]);

                            if (siguienteOrden != -numAlmacenes) {
                                reservas[siguienteOrden + numAlmacenes] = reservaActual;
                            }

                        }

                        camion.addSolution(siguienteOrden);


                        if (siguienteOrden >= 0) {

                            camion.addSolutionGLP(camion.getUsedCapacity());

                            if (pedidos[siguienteOrden] <= glpDisponible) {
                                camion.setUsedCapacity(glpDisponible - pedidos[siguienteOrden]);
                                pedidos[siguienteOrden] = 0;
                            } else {
                                pedidos[siguienteOrden] -= glpDisponible;
                                camion.setUsedCapacity(0.0);
                            }

                            //Atendemos el pedido actual
                            if (pedidos[siguienteOrden] == 0.0) numAtendidos++;
                            //Obteniendo la orden
                            curOrden = ordenes.get(siguienteOrden);

                            xDes = curOrden.getDesX();
                            yDes = curOrden.getDesY();

                        } else {
                            //obtenemos los destinos al almacen
                            deposito = depositos.get(siguienteOrden + numAlmacenes);
                            xDes = deposito.getxPos();
                            yDes = deposito.getyPos();

                        }

                        //Se encuentra en la posicion inicial, no ha salido
                        if (ordenAnterior < 0) {
                            if (ordenAnterior == -1 - numAlmacenes) {
                                xIni = camion.getxPos();
                                yIni = camion.getyPos();
                                coordenate = new int[2];
                                coordenate[0] = xIni;
                                coordenate[1] = yIni;
                                camion.addRoute(coordenate);
                            } else {//esta en un deposito
                                deposito = depositos.get(ordenAnterior + numAlmacenes);
                                xIni = deposito.getxPos();
                                yIni = deposito.getyPos();
                            }
                        } else {
                            lastOrden = ordenes.get(ordenAnterior);
                            xIni = lastOrden.getDesX();
                            yIni = lastOrden.getDesY();
                        }


                        //Calculo de Manhattan
                        //Recordar hacer esto con A*
                        ruta = manhattanPath(xIni, yIni, xDes, yDes);
                        //ruta = aStar.astar_search(new int[]{xIni, yIni}, new int[]{xDes, yDes});
                        //Calculamos el costo de petroleo por hacer esta ruta
                        coordenate = new int[3];
                        coordenate[0] = ruta.get(0)[0];
                        coordenate[1] = ruta.get(0)[1];
                        coordenate[2] = ordenAnterior;

                        ruta.set(0, coordenate);
                        camion.calcCost(ruta.size());
                        camion.addRoute(ruta);
                        //camiones.set(l,camion);
                    }
                }
                // Se atendieron todos los pedidos
                //if (numAtendidos == numOrders) break;
            //}

            fitnessTemp = 0;
            fitnessCur = 0;
            bestAnt = 0;
            bestFit = 0;
            //En otro momento hare lo de A*
            for (int l = 0; l < k; l++) {
                camion = camiones.get(l);
                ordenAnterior = camion.getLastSolution(numAlmacenes);
                camion.addSolutionGLP(camion.getUsedCapacity());
                if (ordenAnterior < 0) {
                    if (ordenAnterior == -1 - numAlmacenes) {
                        xIni = camion.getxPos();
                        yIni = camion.getyPos();
                        coordenate = new int[2];
                        coordenate[0] = xIni;
                        coordenate[1] = yIni;
                        camion.addRoute(coordenate);
                    } else {
                        //Se encuentra en una planta, verificar (esto no puede suceder)
                        xIni = mapa1.getPlantaPrincipal()[0];
                        yIni = mapa1.getPlantaPrincipal()[1];
                    }
                } else {
                    lastOrden = ordenes.get(ordenAnterior);
                    xIni = lastOrden.getDesX();
                    yIni = lastOrden.getDesY();
                }
                //Posiciones del almacen principal
                xDes = mapa1.getPlantaPrincipal()[0];
                yDes = mapa1.getPlantaPrincipal()[1];
                ruta = manhattanPath(xIni, yIni, xDes, yDes);
                camion.addRoute(ruta);

                //calculando fitnessGlobal
                fitnessCur = camion.calcFitness();
                fitnessTemp = fitnessTemp + fitnessCur;
                //System.out.println("Para el camion "+l+" en it "+i+" el fitness: "+fitnessTemp);

                //Aca guardamos a la mejor hormiga, pero opino que deberiamos tener las n mejores hormigas
                if (fitnessCur > bestFit) {
                    bestAnt = l;
                    bestFit = fitnessCur;
                }

                //Recordar este cambio para el momento en que se tengan que agregar pedidos espontaneos
                camion.setUsedCapacity(camion.getCapacity());

            }

            if (numAtendidos != numOrders) {
                updatePheromone(evaporationRate, numOrders, bestFit/2, camiones.get(bestAnt), numAlmacenes);
                //Es solucion inviable y colapso logistico
                //fitnessTemp = -1;
                //bestFit = -1;
            }
            else{
            //Actualizamos la feromona
            //if (bestFit >= 0) {
                //highestNum = numOrders;
                updatePheromone(evaporationRate, numOrders, bestFit, camiones.get(bestAnt), numAlmacenes);
            }


            //Cambiando la mejor solucion de todos los camiones
            for (int l = 0; l < k; l++) {
                //Cambiando a mejor solucion
                if (fitnessTemp >= globalFitness) {
                    globalFitness = fitnessTemp;
                    highestNum = numOrders;
                    camiones.get(l).changeSolution();
                }
                camiones.get(l).clearSolution();
            }

        }

        //Procederemos a generar la nueva solucion utilizando el algoritmo A*
        for (int l =0; l < k; l++){
            camion = camiones.get(l);
            ruta = reCalcBestSolution(camion,ordenes,depositos,aStar);
            camion.setBestRoute(ruta);
        }

        this.bestFitness = globalFitness;
        return camiones;
    }

    private int getNextOrder(Ant camion,  ArrayList<Order> ordenes, int numOrders, double[] pedidos, int numAlmacenes, Map mapa){
        double prob = 0.0;
        double chosen = 0.0;
        double sum = 0.0;
        int next = -1-numAlmacenes;
        double [] probabilidades = new double [numOrders];
        int camino = camion.getRoute().size();
        double tiempoActual = ( ( (camino == 0 ) ? 1 : camino) - 1) / camion.getVelocity();

        //verificando que el camion tenga glp
        if(camion.getUsedCapacity() != 0.0){

            //calculando las probabilidades de todos
            for(int m = 0; m < numOrders; m++){
                //pedido atendido
                if(pedidos[m] == 0.0 || camion.getLastSolution(numAlmacenes) == m)
                    probabilidades[m]=0;
                else
                    //pedido no atendido, evaluar
                    probabilidades[m] = calcProb(camion, ordenes, m, tiempoActual, numAlmacenes, mapa); // atractividad + feromona
                sum += probabilidades[m];
            }

            chosen = Math.random();
            //chosen = rand.nextDouble();

            for(int m = 0; m < numOrders; m++){
                //normalizando y hallando la verdadera probabilidad
                prob += (probabilidades[m])/sum;
                if(chosen < prob){
                    next = m;
                    break;
                }

            }
        }

        return next;
    }

    private double calcProb(Ant camion, ArrayList<Order> ordenes, int ordenPedido,  double tiempoActual, int numAlmacenes, Map mapa){
        double atractividad = 0;
        double pheromoneCur;
        double prob = 0;
        //Calculando atractividad
        int last = camion.getLastSolution(numAlmacenes);
        Order pedido = ordenes.get(ordenPedido);
        Order pedidoAnt = null;
        int manhattan = 0;
        int manhattanBack = 0;
        int coor [] = new int[2];
        double tiempoRestante;
        double tiempoVuelta;
        double tiempoEstimado;
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
        //calculamos la distancia de vuelta
        manhattanBack = Math.abs(coorAlmacen[0] - pedido.getDesX()) + Math.abs( coorAlmacen[1]-pedido.getDesX());
        //Consideraremos que no deja combustible, asi tendremos un margen para hacerle frente a algun imprevisto
        if(!camion.tryOrder(manhattan + manhattanBack)) return 0;
        tiempoEstimado = manhattan/camion.getVelocity();
        tiempoVuelta = manhattanBack/camion.getVelocity();
        //Calculo temporal
        tiempoRestante = pedido.getDeadLine() - tiempoActual;
        //Si sale negativo o sale igual o menor al estimado, inviable
        if(tiempoRestante <= 0 || tiempoRestante <= tiempoEstimado){
            return 0;
        }

        //Si se pasa de las 8 horas, no es recomendable
        if(tiempoActual + tiempoEstimado + tiempoVuelta > this.hTurno)
            return 0;

        //Analizar el caso de que vuelva al mismo lado
        if(manhattan == 0) return 0;

        pheromoneCur = pheromone[last + numAlmacenes][ordenPedido + numAlmacenes];

        prob = (double) 2* 1 / tiempoRestante + (double) 1 / manhattan + pheromoneCur;

        return prob;
    }

    private int refill(Ant camion, Map mapa, double [] reservas , ArrayList<Order> ordenes, ArrayList<DepositGLP> depositos){
        double prob = 0.0;
        double chosen = 0.0;
        double sum = 0.0;
        int next = -1-numAlmacenes;
        double [] probabilidades = new double [numAlmacenes];
        //Por aca se obtiene la secuencia de plantas
        int [] planta;
        for(int m = 0; m < numAlmacenes; m++){
            //tanque vacio
            if(reservas[m] == 0.0 || camion.getLastSolution(numAlmacenes) < 0)
                probabilidades[m]=0;
            else{
                planta = depositos.get(m).getCoor();
                //tanque con almacenamiento
                probabilidades[m] = calcProbAlmacen(camion, planta, m, mapa, ordenes); // atractividad + feromona
            }
            sum += probabilidades[m];
        }

        chosen = Math.random();
        //chosen = rand.nextDouble();

        for(int m = 0; m < numAlmacenes; m++){
            //normalizando y hallando la verdadera probabilidad
            prob += (probabilidades[m])/sum;
            if(chosen < prob){
                next = m - numAlmacenes;
                break;
            }

        }

        return next;
    }

    private double calcProbAlmacen(Ant camion, int[] planta, int m, Map mapa, ArrayList<Order> ordenes){
        double atractividad = 0;
        double pheromoneCur;
        double prob = 0;
        //Calculando atractividad
        int last = camion.getLastSolution(numAlmacenes);
        Order pedido = ordenes.get(last);
        int manhattan = 0;

        //calculamos la distancia
        manhattan = Math.abs(pedido.getDesX() - planta[0]) + Math.abs( pedido.getDesY() - planta[1]);
        //Consideraremos que no deja combustible, asi tendremos un margen para hacerle frente a algun imprevisto
        if(!camion.tryOrder(manhattan )) return 0;

        //Analizar el caso de que vuelva al mismo lado
        if(manhattan == 0) return 0;
        //System.out.println(last + " "+ m);
        pheromoneCur = pheromone[last + numAlmacenes][m];

        prob = (double) 1/ manhattan + pheromoneCur;

        return prob;
    }

    private  ArrayList<int[]> manhattanPath(int xIni, int yIni, int xDes, int yDes){
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

    private void updatePheromone( double evaporationRate, int numOrders, double fitness, Ant bestAnt, int numAlmacenes){
        int x = -100;
        //(1-P)*Fitness + fitness
        for (int p:
                bestAnt.getSolution()
        ) {
            //Si es el primer elemento del arreglo, x se tiene que cambiar
            if(x != -100){
                //Se calcula la feromona
                this.pheromone[x+numAlmacenes][p+numAlmacenes] = (1-evaporationRate) * this.pheromone[x+numAlmacenes][p+numAlmacenes] + fitness;
            }
            x = p;
        }
    }

    private ArrayList<int[]> reCalcBestSolution(Ant camion, ArrayList<Order> ordenes, ArrayList<DepositGLP> depositos, AstarSearch aStar){
        int ordenAnterior = -1-this.numAlmacenes;
        ArrayList<int[]> ruta = new ArrayList<>();
        ArrayList<int[]> rutaSol = new ArrayList<>();
        int [] coordenate;
        int xIni, yIni, xDes, yDes;
        Order curOrden, lastOrden;
        DepositGLP deposito;

        for (int siguienteOrden :
        camion.getBestSolution()) {

                if (siguienteOrden >= 0) {
                    //Obteniendo la orden
                    curOrden = ordenes.get(siguienteOrden);

                    xDes = curOrden.getDesX();
                    yDes = curOrden.getDesY();

                } else {
                    //obtenemos los destinos al almacen
                    deposito = depositos.get(siguienteOrden + numAlmacenes);
                    xDes = deposito.getxPos();
                    yDes = deposito.getyPos();

                }

                //Se encuentra en la posicion inicial, no ha salido
                if (ordenAnterior < 0) {
                    if (ordenAnterior == -1 - numAlmacenes) {
                        xIni = camion.getxPos();
                        yIni = camion.getyPos();
                        coordenate = new int[2];
                        coordenate[0] = xIni;
                        coordenate[1] = yIni;
                        rutaSol.add(coordenate);
                    } else {//esta en un deposito
                        deposito = depositos.get(ordenAnterior + numAlmacenes);
                        xIni = deposito.getxPos();
                        yIni = deposito.getyPos();
                    }
                } else {
                    lastOrden = ordenes.get(ordenAnterior);
                    xIni = lastOrden.getDesX();
                    yIni = lastOrden.getDesY();
                }

                //Recordar hacer esto con A*
                ruta = aStar.astar_search(new int[]{xIni, yIni}, new int[]{xDes, yDes});
                //Calculamos el costo de petroleo por hacer esta ruta
                coordenate = new int[3];
                coordenate[0] = ruta.get(0)[0];
                coordenate[1] = ruta.get(0)[1];
                coordenate[2] = ordenAnterior;

                ruta.set(0, coordenate);
                rutaSol.addAll(ruta);
                ordenAnterior = siguienteOrden;
        }

        //Para completar agregamos la ruta de vuelta al almacen
        if (ordenAnterior < 0) {
                xIni = camion.getxPos();
                yIni = camion.getyPos();
                coordenate = new int[2];
                coordenate[0] = xIni;
                coordenate[1] = yIni;
                rutaSol.add(coordenate);
        } else {
            lastOrden = ordenes.get(ordenAnterior);
            xIni = lastOrden.getDesX();
            yIni = lastOrden.getDesY();
        }
        //Posiciones del almacen principal
        xDes = depositos.get(0).getxPos();
        yDes = depositos.get(0).getyPos();
        ruta = aStar.astar_search(new int[]{xIni, yIni}, new int[]{xDes, yDes});
        rutaSol.addAll(ruta);

        return rutaSol;
    }

    public int getHighestNum(){
        return this.highestNum;
    }
    public double getBestFitness(){
        return this.bestFitness;
    }
}
