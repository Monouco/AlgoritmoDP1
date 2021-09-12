package Models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.io.*;

public class Map {
    private char [][] map;
    private int height;
    private int width;
    private int [] plantaPrincipal={10,8};

    public Map(int height, int width) {
        this.height = height;
        this.width = width;
        this.map = new char [height][width];
        this.initializeMap();
    }

    //Get and sets
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public char[][] getMap() {
        return map;
    }

    public int[] getPlantaPrincipal() {
        return plantaPrincipal;
    }

    public void setPlantaPrincipal(int[] plantaPrincipal) {
        this.plantaPrincipal = plantaPrincipal;
    }

    //Inicializa el mapa como nodos disponibles
    private void initializeMap(){
        for (int i = 0; i < this.height; i++){
            for (int j = 0; j < this.width; j++){
                this.map[i][j] = '.';
            }
        }
        this.map[this.plantaPrincipal[1]][this.plantaPrincipal[0]] = 'O';
    }

    //Imprime el juego de caracteres del mapa
    public void printMap(){
        for (int i = 0; i < this.height; i++){
            for (int j = 0; j < this.width; j++){
                System.out.print(map[i][j]);
                System.out.print("  ");
            }
            System.out.println("");
        }
    }


    public void readRoadblocksFromFile(String filename){
        Roadblock rb = new Roadblock();

        try{
            BufferedReader br = new BufferedReader(new FileReader(filename));
                    String s;
                    while((s = br.readLine()) != null){
                        rb = parseRoadblock(s);
                        this.insertRoadBlock(rb);
                    }
                    br.close();
        }catch(Exception e){
            System.out.println(e);
            return;
        }

    }


    private Roadblock parseRoadblock(String line){
        Roadblock rb = new Roadblock();
        String xi_s="";
        String yi_s="";
        int xi=0;
        int yi=0;

        //Datos estaticos
        rb.setDiaIni(Integer.parseInt(line.substring(0,2)));
        rb.setHoraIni(Integer.parseInt(line.substring(3,5)));
        rb.setMinIni(Integer.parseInt(line.substring(6,8)));

        rb.setDiaFin(Integer.parseInt(line.substring(9,11)));
        rb.setHoraIni(Integer.parseInt(line.substring(12,14)));
        rb.setMinIni(Integer.parseInt(line.substring(15,17)));

        //Parseo de nodos
        for(int i = 18; i < line.length();){

            //Parseo de xi
            for(int j = i;j < line.length();j++){
                if(line.charAt(j)==','){
                    xi = Integer.parseInt(xi_s);

                    i=j+1; //Me quedo en la sig posicion
                    break;
                }
                xi_s += line.charAt(j);
            }

            //Parseo de yi
            for(int j = i;j < line.length();j++){
                if(line.charAt(j)==','){
                    yi = Integer.parseInt(yi_s);
                    i=j+1; //Me quedo en la sig posicion
                    break;
                }
                else if(j+1==line.length()){
                    yi_s += line.charAt(j);
                    yi = Integer.parseInt(yi_s);
                    i=j+1;
                    break;
                }
                yi_s += line.charAt(j);
            }
            rb.appendNode(xi,yi);
            xi_s = "";
            yi_s = "";
        }

        return rb;
    }

    private void insertRoadBlock(Roadblock rb){
        /*
        VERSION SIMPLIFICADA:
        Realmente, deberia recibir 2 nodos en una misma recta, no necesariamente consecutivos
        y deberia agregar como nodo bloqueado a todos los nodos intermedios
        * */
        for (int[] i:rb.getNodes()) {
            this.map[i[1]][i[0]] = '#';
        }
    }

    public void insertSolution (Ant camion, char car){
        for (int [] x:
                camion.getBestRoute()
             ) {
            this.map[x[1]][x[0]] = car;
        }
    }

}
