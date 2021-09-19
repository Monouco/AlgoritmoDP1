package Main;

import Models.Ant;
import Models.DepositGLP;
import Models.Map;
import Models.Order;
import Solutions.ACSAlgorithm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.lang.Math;

public class Main {
    public static void main(String[] args) {

        //Lectura de archivo e inicializacion del mapa
        Map mapa1 = new Map(50, 70);
        DepositGLP principal = new DepositGLP(10, 8, 100);
        mapa1.addDeposit(principal);

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
        //pheromone = new double[numOrders + numAlmacenes][numOrders + numAlmacenes];

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
            Ant camion = new Ant(25, 25,  mapa1.getPlantaPrincipal()[0], mapa1.getPlantaPrincipal()[1], 30, 2.5, 12.5);
            camiones.add(camion);
        }

        //CAMIONES TIPO B
        for(int i=0; i<tipob; i++){
            //Inicializamos con valores fijos para todos
            Ant camion = new Ant(15, 25,  mapa1.getPlantaPrincipal()[0], mapa1.getPlantaPrincipal()[1], 30, 2, 7.5);
            camiones.add(camion);
        }


        int bestAnt;
        double bestFit;
        //Probar despues con las n mejores hormigas por ciclo
        //int nBest = (int) Math.ceil((double) k/2);

        Ant camion = camiones.get(0);
        int [] plantaPrincipal = mapa1.getPlantaPrincipal();
        ACSAlgorithm solucion = new ACSAlgorithm(numAlmacenes, numOrders, plantaPrincipal);
        //System.out.println("Pedidos atendidos: "+ highestNum);

        ArrayList<Ant> secuencia = solucion.findSolution(camiones, orders, mapa1, cycles, steps, k, 0.3);

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
