package Models;

public class Order {

    private int dia;
    private int hora;
    private int minuto;
    private int desX;
    private int desY;
    private double glp;
    private double deadLine;

    public Order() {
    }

    public Order(int dia, int hora, int minuto, int desX, int desY, double glp, double deadLine){
        this.dia = dia;
        this.hora = hora;
        this.minuto = minuto;
        this.desX = desX;
        this.desY = desY;
        this.glp = glp;
        this.deadLine = deadLine;
    }

    public int getDesX() {
        return desX;
    }

    public void setDesX(int desX) {
        this.desX = desX;
    }

    public int getDesY() {
        return desY;
    }

    public void setDesY(int desY) {
        this.desY = desY;
    }

    public double getGlp() {
        return glp;
    }

    public void setGlp(double glp) {
        this.glp = glp;
    }

    public double getDeadLine() {
        return deadLine;
    }

    public void setDeadLine(double deadLine) {
        this.deadLine = deadLine;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public int getHora() {
        return hora;
    }

    public void setHora(int hora) {
        this.hora = hora;
    }

    public int getMinuto() {
        return minuto;
    }

    public void setMinuto(int minuto) {
        this.minuto = minuto;
    }
}
