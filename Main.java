import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    //Cargamos los datos de los txt a unas listas
    private static ArrayList<Cliente> clientes = gestionDatos.cargarDatos();
    private static ArrayList<Factura> facturas = gestionDatos.cargarFacturas();
    public static void main(String[] args) {
        Scanner board = new Scanner(System.in);
        int opcion = 0;

        do { // se ejecuta mientras se cumpla el while

            System.out.println("--------------------------------------- \n1) Mostrar clientes registrados \n2) Mostrar todas las facturas ");
            System.out.println("3) Crear factura \n4) Mostrar facturas de un cliente \n5) Agregar cliente \n6) Exportar a CSV (Para hojas de cálculo)\n7) Salir \n---------------------------------------");
            try{
            opcion = board.nextInt();
            board.nextLine();
            } catch(InputMismatchException e) {
                System.err.println("Error " + e.getMessage());
                return;
            }

            switch(opcion) {
                case 1 :
                    Cliente.mostrarClienteRegistrados(clientes); // Ejecuta el metodo para mostrar los clientes de listas
                    break;

                case 2 :
                    Factura.mostrarFacturasRegistradas(facturas); // AL igual que el de arriba muestra la lista
                    break;

                case 3 :
                    Factura.crearFactura(board); // Ejecuta un menú para seleccionar si crear o salir, si se selecciona crear se procede la creación
                    break;

                case 4 :
                    Factura.filtrarFacturas(board); //Ejecuta el metodo de filtrado de factura por numero de cliente
                    break;


                case 5 : 
                    gestionDatos.guardarCliente(board,clientes); //Llama el método para guardar los datos al txt
                    break;

                case 6 :
                    gestionDatos.exportarCSV(board); //Esta fue una funcion extra
                    break;

                case 7 :
                    System.out.println("Saliendo");
                    System.exit(0); //Metodo para salir sin error
                    break;

                default: 
                    System.out.println("Seleccione una opcion valida");
                    break;
            }

        } while (opcion != 7); // se ejecuta el bloque Do mientras no se seleccione 7, dos formas de asegurar la salida correcta
        
        board.close();
    }
} 