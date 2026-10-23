import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;


public class Cliente {
    private static final String ARCHIVO_CLIENTE = "clientes.txt";

    private int numCliente;
    private String nombreCliente;
    private String fechaAlta;
    //Contructor vacio
    public Cliente() {
        this.numCliente = 0;
        this.nombreCliente = null;
        this.fechaAlta = null;
    }
    //Contructor con atributos
    public Cliente(int numCliente, String nombreCliente, String fechaAlta) {
        this.numCliente = numCliente;
        this.nombreCliente = nombreCliente;
        this.fechaAlta = fechaAlta;
    }

    public static void mostrarClienteRegistrados(List<Cliente> clientes) {
        System.out.println("| Número de cliente | Nombre Cliente | Fecha de alta");
        System.out.println("-----------------------------------------------------");

        for(Cliente cliente : clientes) { //Si no estoy mal esto es un for each lo que hace es que por cada objeto cliente en cliente ejecute el bloque
            System.out.printf(" %d, %s, %s \n",cliente.getNumCliente(), cliente.getNombreCliente(), cliente.getFechaAlta());
        }
    }

    public static int obtenerNumCliente() {
        File file = new File(ARCHIVO_CLIENTE);
        if(!file.exists()) {
            return 1; // Si el archivo no existe devuelve uno por que es el primer cliente
        }

        int ultimoNumero = 0;
        try(BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_CLIENTE))) {
            String line;
            boolean primero = true;
            //Asigna el valor que lee el reader a line
            while((line = br.readLine()) != null) {
                if(primero) { //Esta es para ignorar el encabezado
                    primero = false;
                    continue;
                }
                //usamos .isBlank para mejor eficiencia que es lo mismo que sin .trim().isEmpty()
                if(!line.isBlank()) {
                    String[] parts = line.split(",");
                    
                    if(parts.length >= 3) { //Validacion rapida para evitar indexoutofboundsexception
                        try {
                            ultimoNumero = Integer.parseInt(parts[0]);
                        } catch (NumberFormatException e) { // Manejo de posible error al querer converitr a entero
                            System.out.println(e.getMessage());
                        }
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage() + "Al leer el archivo");
        }
        return ultimoNumero + 1;
    }
    
    //Este es la funcion para imprimir el cliente en el txt
    public String toTxtLine() {
        return numCliente + "," + nombreCliente + "," + fechaAlta;
    }

    //Respectivos setters y getters
    public int getNumCliente() {
        return numCliente;
    }

    public void setNumCliente(int numCliente) {
        this.numCliente = numCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(String fechaAlta) {
        this.fechaAlta = fechaAlta;
    }  
}
