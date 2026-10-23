import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;


public class gestionDatos {
    private static ArrayList<Cliente> ListCliente = gestionDatos.cargarDatos();
    private static final String ARCHIVO_CLIENTE = "clientes.txt";
    private static final String ARCHIVO_FACTURAS = "facturas.txt";
    private static final String ARCHIVO_CLIENTE_CSV = "clientes.csv";
    private static final String ARCHIVO_FACTURAS_CSV = "facturas.csv";
    
    //Constructor vacio por que no necesitamos instanciar por que todos los métodos son estaticos
    public gestionDatos() {
    }

    //Gestion de datos de Cliente
    //Guadardo de cleintes(No se pidio pero lo agregamos para mas dinamismo)
    public static void guardarCliente(Scanner scanner, List<Cliente> clientes) {
        File file = new File(ARCHIVO_CLIENTE);

        //Creamos el cliente para guardar tal cual el cliente en las listas y en el txt, existe un metodo llamado "toTxt" para cliente, sería casi como un toString
        Cliente cliente = new Cliente();

            cliente.setNumCliente(Cliente.obtenerNumCliente());
            System.out.println("Numero de cliente designado: " + cliente.getNumCliente());

            System.out.println("Ingrese el nombre del cliente: ");
            cliente.setNombreCliente(scanner.nextLine());
            
            System.out.println("Ingrese Fecha de alta");
            cliente.setFechaAlta(scanner.nextLine());

        clientes.add(cliente); //Agregamos al ArrayList
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file,true))) {
            if(!file.exists()) { //Validación rápida si existe el archivo, si no existe se guarda primero el encabezado
                bw.write("Numero de cliente,Cliente,Fecha de alta");
                bw.newLine();  
            }

            bw.write(cliente.toTxtLine());//Cliente aplicado con toTxt
            bw.newLine();

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage() + " al guardar el archivo");
        }

        System.out.println("------------------- Cliente ---------------------"); //Aqui imprimimos los datos que ingresamos para mostrar como quedo
        System.out.println("Número: " + cliente.getNumCliente() + 
        "\nNombre: " + cliente.getNombreCliente() + 
        "\nFecha de registro: " + cliente.getFechaAlta());
        System.out.println("Cliente registrado preciona ENTER para continuar");
        scanner.nextLine();
    }

    //Cargado de datos de cliente esto realmente solo carga los datos y los devuelve como una lista,
    //En la clase cliente esta para listar
    public static ArrayList<Cliente> cargarDatos() {
        ArrayList<Cliente> clientes = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_CLIENTE))) {
            String line = br.readLine();

            while((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if(parts.length == 3) {
                    int id = Integer.parseInt(parts[0]);
                    String nombre = parts[1];
                    String fechaAlta = parts[2];

                    clientes.add(new Cliente(id, nombre, fechaAlta));
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return clientes;
    }

    // Gestion de datos de factura
    
    //Guardado de facturas
    public static void guardarFactura(Factura factura) {
        File file = new File(ARCHIVO_FACTURAS);


        try {
            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);

            if (!file.exists()) { //Si no existe se crea primero el encabezado
                bw.write("numFactura,numCliente,fechaEmision,descripcion,importe,iva,total");
                bw.newLine();
            }
            //Equivalencia a toTxtLine
            bw.write(factura.getNumFactura() + "," +
                     factura.getCliente().getNumCliente() + "," +
                     factura.getFechaEmision() + "," +
                     factura.descripcion + "," + 
                     factura.importe + "," +     
                     factura.getIva() + "," +
                     factura.getTotal());
            bw.newLine();
            bw.close(); // metodo para cerrar el flujo de datos y liberar memoria

            System.out.println("----------------------------------------");
            System.out.println("  ***********  FACTURA     ************  ");
            System.out.println("----------------------------------------");
            System.out.println("Numero de factura: " + factura.getNumFactura());
            System.out.println("Cliente: " + factura.getCliente().getNombreCliente());
            System.out.println("Fecha de Emision: " + factura.getFechaEmision());
            System.out.println("Descripcion: " + factura.descripcion);
            System.out.println("Importe: $" + factura.importe);
            System.out.println("IVA: $" + factura.getIva());
            System.out.println("Total: $" + factura.getTotal());
            System.out.println("----------------------------------------\n");

        } catch (IOException e) {
            System.out.println("Error al escribir el archivo: " + e.getMessage());
        }
    }

    public static ArrayList<Factura> cargarFacturas() {
        ArrayList<Factura> listaFacturas = new ArrayList<>(); //Creamos la lista
        File file = new File(ARCHIVO_FACTURAS);
        
        if (!file.exists()) {
            return listaFacturas;
        }

        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            
            String linea;
            br.readLine(); 

            while ((linea = br.readLine()) != null) {
                Factura f = new Factura(null,null,0.0,0,0.0,0.0); // Instanciamos una factura dentro del while para crear
                                                                                                                 // una nueva cada ciclo

                if (!linea.isBlank()) { //isBlank es la forma mas efectiva de comprobar si hay caracteres en un texto
                    String[] datos = linea.split(",");
                    
                    f.setNumFactura(Integer.parseInt(datos[0].trim()));
                    int numCli = Integer.parseInt(datos[1].trim());
                    f.setFechaEmision(datos[2].trim());
                    f.setDescripcion(datos[3].trim());
                    f.setImporte(Double.parseDouble(datos[4].trim()));
                    f.setIva(Double.parseDouble(datos[5].trim()));
                    f.setTotal(Double.parseDouble(datos[6].trim()));

                    Cliente clienteAsociado = null;
                    for (Cliente c : ListCliente) { //Recorremos con for each, por cada Cliente de la lista cliente se crea uno nuevo
                        if (c.getNumCliente() == numCli) {
                            clienteAsociado = c;
                            break;
                        }
                    }

                    if (clienteAsociado == null) {
                        clienteAsociado = new Cliente(numCli, "Desconocido", "N/A"); //Hacemos que si no existe se cree con valores predeterminados
                    }
                    f.setCliente(clienteAsociado);
                    
                    listaFacturas.add(f);
                }
            }
            br.close(); 
            
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }

        return listaFacturas;
    }

    public static void exportarCSV(Scanner scanner) {
        int opcionExportar = 0;
        
        while(true) {
            System.out.println("1) Exportar Clientes \n2) Exportar facturas \n3) Salir");
            
            try {
            opcionExportar = scanner.nextInt();
            scanner.nextLine();
            } catch(InputMismatchException e) { //Manejo de errores para el nextInt
                System.err.println("Error: " + e.getMessage());
                break;
            }

            switch (opcionExportar) {
                case 1:
                    try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_CLIENTE)); // creamos un lector para el txt y un escritor para el csv
                    BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_CLIENTE_CSV))) {
                        String linea;

                        while((linea = br.readLine()) != null) {
                            bw.write(linea);    // Escribimos lo que leyo "linea" del txt al csv
                            bw.newLine();
                        }
                    } catch (IOException e) {
                            System.out.println("Error: " + e.getMessage() + " Error al guardar el archivo");
                        }
                System.out.println("Archivo exportado");
                break;

                case 2 :
                    try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_FACTURAS));
                    BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_FACTURAS_CSV))) {  //creamos un lectos para el txt y un escritor para el csv
                        String linea;

                        while((linea = br.readLine()) != null) {
                            bw.write(linea);    // Escribimos lo que leyo "linea" del txt al csv
                            bw.newLine();
                        }
                        System.out.println("Archivo exportado");
                    } catch (IOException e) {
                            System.out.println("Error: " + e.getMessage() + " Error al guardar el archivo");
                        }
                break;

                case 3 :
                    return;
            
                default:
                    System.out.println("ingresa una opcion valida");
            }

        }

    }
    
}
