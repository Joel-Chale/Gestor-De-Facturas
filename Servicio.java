public abstract class Servicio {
    protected String descripcion;
    protected double importe;

    //respectivo contructor
    public Servicio(String descripcion, double importe) {
        this.descripcion = descripcion;
        this.importe = importe;
    }

    //Método para aplicar el la clase hija
    public abstract double calcularTotal(double importe, double iva);

    public abstract double calcularIva(double valor);

    //Agregamos esto por que creímos que necesitabamos tener herencia
}
