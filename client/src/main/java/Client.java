import com.zeroc.Ice.Util;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Client {
    public static void main(String[] args) {
        java.util.List<String> extraArgs = new ArrayList<>();

        try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "config.client", extraArgs)) {
            Demo.PrinterPrx service = Demo.PrinterPrx
                    .checkedCast(communicator.propertyToProxy("Printer.Proxy"));

            if (service == null) {
                throw new Error("Invalid proxy");
            }

            // Obtener username y hostname
            String username = executeCommand("whoami").trim();
            String hostname = executeCommand("hostname").trim();

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String inputLine;

            System.out.println("Ingrese su mensaje (escriba 'salir' para finalizar):");

            while (true) {
                inputLine = reader.readLine(); // Leer entrada del usuario

                if ("salir".equalsIgnoreCase(inputLine)) {
                    break; // Salir del ciclo si el usuario escribe 'salir'
                }

                // Preparar el mensaje con el formato username:hostname:mensaje
                String message = username + ":" + hostname + ":" + inputLine;

                // Enviar el mensaje al servidor y recibir la respuesta
                Demo.Response response = service.printString(message);

                // Imprimir la respuesta del servidor
                System.out.println("Respuesta del servidor: " + response.value + ", " + response.responseTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String executeCommand(String command) {
        StringBuilder output = new StringBuilder();
        String line;

        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();
    }
}
