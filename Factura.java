import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

public class Factura extends Servicio {
    // Cargamos datos
    private static ArrayList<Cliente> ListCliente = gestionDatos.cargarDatos();
    // Esto es por comodidad realmente, y por limpieza del código
    private static final String ARCHIVO_CLIENTES = "clientes.txt";
    private static final String ARCHIVO_FACTURAS = "facturas.txt";
    //Variables
    private int numFactura;
    private double iva;
    private double total;
    //Agregué estas 2 variables
    private String fechaEmision;
    private Cliente cliente; 

    public Factura(Cliente cliente, String descripcion, double importe, int numFactura, double iva, double total) {
        super(descripcion,importe);
        this.cliente = cliente;
        this.numFactura = numFactura;
        this.iva = iva;
        this.total = total; 
    }
    
    public static void crearFactura(Scanner scanner) {
        Factura factura;
        Cliente cliente = null;
        double importe = 0;
        int opcion = 0;
        
        while (true) {
            System.out.println("1) Mostrar usuarios registrados \n2) Crear la factura \n3) Salir");
            
            try {
            opcion = scanner.nextInt();
            scanner.nextLine(); // agregue para la limpieza del enter
            } catch (InputMismatchException e) {
                System.err.println("Error " + e.getMessage() + " Se ingreso un valor no valido");
                scanner.nextLine();
                return;
            }

            switch (opcion) {
                case 1:
                    Cliente.mostrarClienteRegistrados(ListCliente);
                    break;
                
                case 2:
                    System.out.println("ingresa el numero de cliente para crear la factura");
                    try {
                    cliente = buscarNum(scanner.nextInt());
                    scanner.nextLine(); // igual para limpiar "Enter" 
                    } catch (InputMismatchException e) {
                        System.err.println("Error: " + e.getMessage() + " Se ingreso un valor no válido");
                        scanner.nextLine();
                        return;
                    }
                    // hice una validación para saber si el cliente existe
                    if (cliente.getNombreCliente() == null) {
                        System.out.println("Error: El cliente no existe. Por favor, registralo.");
                        break;
                    }

                    // Para crear la factura
                    System.out.println("---------------------- Factura ----------------------------");
                    System.out.println("Número de factura " + Factura.obtenerNumFactura()); //obternerNumFactura es el metodo dinamico para comprobar la ultima factura

                    System.out.println("Ingresa la fecha de emisión (AAAA-MM-DD): ");
                    String fecha = scanner.nextLine();

                    System.err.println("Ingresa la descripción: ");
                    String descripcion = scanner.nextLine();

                    System.out.println("Ingresa el importe: ");
                    try {
                    importe = scanner.nextDouble();
                    scanner.nextLine(); // Limpia el "Enter" final del importe **
                    } catch (InputMismatchException e) {
                        System.err.println("Error: " + e.getMessage());
                        return;
                    }

                    // inicializa el objeto factura real con todos los datos correctos
                    factura = new Factura(cliente, descripcion, importe, 0, 0, 0);
                    factura.setNumFactura(Factura.obtenerNumFactura());
                    factura.setIva(factura.calcularIva(importe));
                    factura.setTotal(factura.calcularTotal(importe, factura.getIva()));
                    factura.setFechaEmision(fecha);
                    //factura.setCliente(cliente);

                    // aquí se guarda la factura en el txt
                    gestionDatos.guardarFactura(factura);
                    break;

                case 3:
                    return; // y esto para salir del bucle while
                default:
                    System.out.println("Opción no válida.");
                    break;  
            }
        }   
    }

    public static Cliente buscarNum(int numero) {
        Cliente cliente = new Cliente(); //creamos un cliente vacio para llenarlo posteriormente y regresarlo

        try(BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_CLIENTES))) {
            String line;
            boolean primero = true; // booleano de un solo uso para saltar el encabezado

            while((line = br.readLine()) != null) {
                if(primero) {
                    primero = false; // pone falso para que en la primera
                    continue;
                }

                if(!line.isBlank()) {
                    String[] parts = line.split(",");
                    
                    try {
                        // convertir el texto a entero para poder comparar números reales
                        int idArchivo = Integer.parseInt(parts[0].trim());
                        
                        if(idArchivo == numero) {
                            if (parts.length >= 3) { 
                                cliente.setNumCliente(idArchivo);
                                cliente.setNombreCliente(parts[1].trim());
                                cliente.setFechaAlta(parts[2].trim());
                                break; // Cuando encudentra al cliente, sale del ciclo
                            }
                        }
                    } catch (NumberFormatException e) {

                    }
                }
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return cliente;
    }

    public static int obtenerNumFactura() {
        File file = new File(ARCHIVO_FACTURAS); //Esto es obvio, simplemente es crear file con el nombre, ARCHVIO_FACTURAS es lo mismo que poner "facturas.txt"
        if(!file.exists()) { 
            return 1; // Este if sirve para validar si existia el archivo antes, en caso de no existir, dado que esta vacio pues devuelve 1
        }

        int ultimoNumero = 0;
        try(BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_FACTURAS))) { 
            String line;
            boolean primero = true; // Este booleano siver para ignorar el encabezado en el while

            while((line = br.readLine()) != null) {//asgina el valor de la linea a line y verifica la condición
                if(primero) {
                    primero = false; //Como es la primera vuelta, se pone en false para que en la segunda vuelta no se ejecute este if
                    continue; // Saltamos la lineas de abajo
                }
                //Misma cosa, mas eficiente que trim().siEmpty()
                if(!line.isBlank()) {
                    String[] parts = line.split(",");
                    
                    if(parts.length >= 3) { //Validación para evitar errores como el del indexOutOfBoundsException
                        try { //Try por si se recibe el primera valor que esta por alguna razon es algo que no sea un int
                            ultimoNumero = Integer.parseInt(parts[0].trim());
                        } catch (NumberFormatException e) {
                            System.out.println("Eror: " + e.getMessage() + " Al intentar convertir el número");
                        }
                    }
                }
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return ultimoNumero + 1; //Aquí ya que tiene el ultmo valor que se consiguio en el while le suma 1 y lo regresa
    }

    //Este método sirve para filtrar facturas de un solo cliente
    public static void filtrarFacturas(Scanner scanner) {
        int opcionFiltrar = 0;//Este es meramente para el menú
        int idBuscar = 0;//Este es el valor que buscara y que en el switch se le dara valor
        
        while (true){
            System.out.println("1) Buscar \n2) Salir");
            try {
            opcionFiltrar = scanner.nextInt(); // recibimos el valor del id
            scanner.nextLine();
            } catch(InputMismatchException e) {
                System.err.println("Error: " + e.getMessage());
                return;
            }
            switch (opcionFiltrar) {
                case 1 :
                    //Aqui pedimos los datos
                    System.out.println("Ingrese el numero de cliente para buscar sus facturas:");
                    try {   //Este try atrapa el InputMismatchException por que se esperaba un entero y se ingreso una letra
                    idBuscar = scanner.nextInt();
                    scanner.nextLine(); 
                    } catch(InputMismatchException e) {
                        System.err.println("Error: " + e.getMessage() + " no haz ingresado un valor valido");
                        scanner.nextLine();
                        return; //Esto es lo importante, limpia el enter para que no se presione algo equivocando, luego se sale al menú principal
                    }
                    ArrayList<Factura> todasLasFacturas = gestionDatos.cargarFacturas();    //Si el no da nungun error se carga los datos de facturas en un ArrayList
                    System.out.println("\n--- FACTURAS ENCONTRADAS DEL CLIENTE " + idBuscar + " ---");
                    boolean encontrado = false; //Esto es una "Validación" por encontrar una al menos una factura, empieza siendo false

                    for (Factura f : todasLasFacturas) {
                        if (f.getCliente().getNumCliente() == idBuscar) {
                        System.out.println("Factura #" + f.getNumFactura() + " | No."+ f.getCliente().getNumCliente() + " - " + f.getCliente().getNombreCliente() + " | Fecha: " + f.getFechaEmision() + " | Concepto: " + f.descripcion + " | Importe: $"+ f.getImporte() + "| Iva: $"+ f.getIva() + " | Total: $" + f.getTotal());
                        encontrado = true; // Si se encuentra la factura esto pasa a true
                        }
                    }

                    System.out.println("-----------------------------------------------------------------------------------");

                        if (encontrado == false) { //dado que si se encontro un factura y encontrado se volvio true entonces esto se ignora
                            System.out.println("No se encontraron facturas registradas para este cliente.");
                        }                    
                break;

                case 2 : 
                    return; //Simplemente sirve para salir

                default: 
                    System.out.println("Ingresa una opción valida");
        
            } 
        }
    }
    //Esto lo que hace es recibir las listas con los datos cargados de los txt desde el main
    public static void mostrarFacturasRegistradas(List<Factura> facturas) {
        facturas = gestionDatos.cargarFacturas();
        System.out.println("| Número de Factura | Cliente | Fecha | Concepto | Toa");
        System.out.println("-----------------------------------------------------");

       //Recorre la lista completa que se proporciona desde el main y los imprime
        for (Factura f : facturas) {
                System.out.println("Factura #" + f.getNumFactura() + " | No."+ f.getCliente().getNumCliente() + " - " + f.getCliente().getNombreCliente() + " | Fecha: " + f.getFechaEmision() + " | Concepto: " + f.descripcion + " | Importe: $"+ f.getImporte() + " | Iva: $"+ f.getIva() + " | Total: $" + f.getTotal());
        }
    }
   
    @Override
    public double calcularTotal(double importe, double iva) {
        // removí el código dejando una sola expresión limpia (sugerencia)
        this.total = importe + iva; 
        return this.total; 
    }

    @Override
    public double calcularIva(double importe) {
        // cambio de código (sugerencia)
        this.iva = importe * 0.16;
        return this.iva; 
    }
    //Respectivos setters y getters clasicos
    public int getNumFactura() {
        return numFactura;
    }

    public void setNumFactura(int numFactura) {
        this.numFactura = numFactura;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(Double importe) {
        this.importe = importe;
    }

    public double getIva() {
        return iva;
    }

    public void setIva(double iva) {
        this.iva = iva;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getFechaEmision() {
        return fechaEmision;
    }

    // cambie 
    public void setFechaEmision(String fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    //Estos son para poder obtener el cliente desde factura
    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
} 