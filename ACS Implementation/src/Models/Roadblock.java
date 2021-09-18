package Models;
import java.util.ArrayList;

public class Roadblock {
    private int diaIni;
    private int horaIni;
    private int minIni;
    private int diaFin;
    private int horaFin;
    private int minFin;
    private ArrayList<int []> nodes;


    public Roadblock() {
        nodes = new ArrayList<>();
    }

    public Roadblock(int diaIni, int horaIni, int minIni, int diaFin, int horaFin, int minFin) {
        this.diaIni = diaIni;
        this.horaIni = horaIni;
        this.minIni = minIni;
        this.diaFin = diaFin;
        this.horaFin = horaFin;
        this.minFin = minFin;
        nodes = new ArrayList<>();
    }

    public int getDiaIni() {
        return diaIni;
    }

    public void setDiaIni(int diaIni) {
        this.diaIni = diaIni;
    }

    public int getHoraIni() {
        return horaIni;
    }

    public void setHoraIni(int horaIni) {
        this.horaIni = horaIni;
    }

    public int getMinIni() {
        return minIni;
    }

    public void setMinIni(int minIni) {
        this.minIni = minIni;
    }

    public int getDiaFin() {
        return diaFin;
    }

    public void setDiaFin(int diaFin) {
        this.diaFin = diaFin;
    }

    public int getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(int horaFin) {
        this.horaFin = horaFin;
    }

    public int getMinFin() {
        return minFin;
    }

    public void setMinFin(int minFin) {
        this.minFin = minFin;
    }

    public ArrayList<int[]> getNodes() {
        return nodes;
    }

    public void appendNode(int x, int y){
        this.nodes.add(new int[]{x,y});
    }
}
