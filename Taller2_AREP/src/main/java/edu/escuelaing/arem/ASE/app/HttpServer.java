package edu.escuelaing.arem.ASE.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Base64;
import java.awt.*;

public class HttpServer {

    public static File file;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }
        boolean running = true;
        while (running) {

            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));
            String inputLine, outputLine;
            boolean fline = true;
            boolean necessaryFlag = true;
            String uriS = "";
            String uriWithFileName = "";
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received: " + inputLine);
                if (necessaryFlag) {
                    if (fline) {
                        fline = false;
                        uriS = uriS = inputLine.split(" ")[1];
                    }
                    if (inputLine.startsWith("Content-Disposition:")) {
                        necessaryFlag = false;
                        uriWithFileName = inputLine;
                    }
                }
                if (!in.ready()) {
                    break;
                }
            }
            if (uriS.startsWith("/upload")) {
                outputLine = extractFileContentFromInput(uriWithFileName);
            } else {
                outputLine = getHomeIndex();
            }
            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }
    /**
     * Extrae el contenido del archivo de una cadena de entrada, manejando diferentes tipos de archivos y devolviendo respuestas HTTP apropiadas.
     *
     * @param inputString La cadena de entrada que contiene el nombre del archivo y los límites potenciales.
     * @return El contenido del archivo como una respuesta HTTP, o "404" si el archivo no se encuentra o hay un error.
     */

    public static String extractFileContentFromInput(String inputString) {
        String filename = null;

        // Extract the filename from the input string
        for (String part : inputString.split(";")) {
            part = part.trim();
            if (part.startsWith("filename")) {
                String[] filenameParts = part.split("=");
                if (filenameParts.length > 1) {
                    filename = filenameParts[1].trim().replace("\"", "");
                    break;
                }
            }
        }

        if (filename != null) {
            // Get the file content based on its type
            String fileContent = getTheFileContent(filename, "Taller2_AREP\\\\src\\\\main\\\\resource\\\\");
            if (!fileContent.equals("404")) {
                return fileContent;
            }
        }

        // Handle the case where the file is not found or there's an error
        return "404";
    }
    /**
     * Recupera el contenido de un archivo en función de su tipo, convirtiéndolo en una respuesta HTTP apropiada.
     *
     * @param filename El nombre del archivo a recuperar.
     * @param path La ruta al directorio que contiene el archivo.
     * @return El contenido del archivo como una respuesta HTTP, o "404" si el archivo no se encuentra o hay un error.
     */

    public static String getTheFileContent(String filename, String path) {
        String completePath = path + filename;
        File file = new File(completePath);
        String type = getFileExtension(filename);  // Get the file extension

        if (file.exists()) {
            System.out.println("Se encuentra el archivo");
            try {
                // Call the appropriate conversion method based on type
                switch (type) {
                    case "html":
                    case "txt":
                        return toHTML(file);
                    case "js":
                        return toJs(file);
                    case "css":
                        return toCSS(file);
                    case "jpg":
                    case "jpge":
                    case "jpeg":
                    case "png":
                        return toImage(file, type);
                    default:
                        // Handle unsupported file types
                        throw new IOException("Unsupported file type: " + type);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("NO existe");
        }

        return "404";
    }
    /**
     * Obtiene la extensión del archivo a partir de un nombre de archivo.
     *
     * @param filename El nombre del archivo del que se extrae la extensión.
     * @return La extensión del archivo, o una cadena vacía si no se encuentra ninguna extensión.
     */
    private static String getFileExtension(String filename) {
        int extensionIndex = filename.lastIndexOf(".");
        return extensionIndex != -1 ? filename.substring(extensionIndex + 1) : "";
    }

    /**
     * Convierte un archivo de imagen a una respuesta HTTP con codificación Base64.
     *
     * @param file El archivo de imagen a convertir.
     * @param type El tipo de imagen (por ejemplo, "jpg", "png").
     * @return La respuesta HTTP que contiene los datos de la imagen codificados en Base64.
     * @throws IOException Si hay un error al leer el archivo.
     */

    public static String toImage(File file, String type) throws IOException{
        byte[] bytes = Files.readAllBytes(file.toPath());
        String base64 = Base64.getEncoder().encodeToString(bytes);
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/"+ type + "\r\n"
                + "\r\n"
                + "<center><img src=\"data:image/" + type + ";base64," + base64 + "\"></center>";
    }
    /**
     * Convierte un archivo HTML a una respuesta HTTP.
     *
     * @param file El archivo HTML a convertir.
     * @return La respuesta HTTP que contiene el contenido HTML.
     * @throws IOException Si hay un error al leer el archivo.
     */
    public static String toHTML(File file) throws IOException {
        StringBuilder body = fromArchiveToString(file);
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<center>" + body + "</center>";
    }

    /**
     * Convierte un archivo CSS a una respuesta HTTP.
     *
     * @param file El archivo CSS a convertir.
     * @return La respuesta HTTP que contiene el contenido CSS.
     * @throws IOException Si hay un error al leer el archivo.
     */
    public static String toCSS(File file) throws IOException{
        StringBuilder body = fromArchiveToString(file);
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/css\r\n"
                + "\r\n"
                + "<center>"+body+"</center>";
    }

    /**
     * Convierte un archivo JavaScript a una respuesta HTTP.
     *
     * @param file El archivo JavaScript a convertir.
     * @return La respuesta HTTP que contiene el contenido JavaScript.
     * @throws IOException Si hay un error al leer el archivo.
     */
    public static String toJs(File file)throws IOException{
        StringBuilder body = fromArchiveToString(file);
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: application/javascript\r\n"
                + "\r\n"
                + "<center>"+body+"</center>";
    }

    /**
     * Lee un archivo línea por línea y devuelve su contenido como un StringBuilder.
     *
     * @param file El archivo a leer.
     * @return Un StringBuilder que contiene el contenido del archivo.
     * @throws IOException Si hay un error al leer el archivo.
     */
    public static StringBuilder fromArchiveToString(File file) throws IOException{
        StringBuilder body = new StringBuilder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        String line;
        do {
            line = reader.readLine();
            if (line != null) {
                body.append(line).append("\n");
            }
        } while (line != null);
        reader.close();
        return body;
    }

    /**
     * Genera el contenido HTML para la página de inicio, incluyendo un formulario de carga de archivos.
     *
     * @return El contenido HTML para la página de inicio como una respuesta HTTP.
     */
    public static String getHomeIndex() {
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>File Hub</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "             background-image: url(https://i.pinimg.com/736x/fb/1a/7b/fb1a7be0ff2251b4ff0b9e443b74c638.jpg);\n" +
                "            font-family: \"Ubuntu\", Impact;\n" +
                "            display: flex;\n" +
                "            justify-content: center;\n" +
                "            align-items: center;\n" +
                "            height: 100vh;\n" +
                "            margin: 0;\n" +
                "        }\n" +
                "\n" +
                "        form {\n" +
                "            display: inline-block;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "\n" +
                "        label, input[type=\"file\"], input[type=\"button\"] {\n" +
                "            display: block;\n" +
                "            margin: 10px 0;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "\n" +
                "        #uploadMsg {\n" +
                "            margin-top: 20px;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <form action=\"/upload\" method=\"POST\" enctype=\"multipart/form-data\">\n" +
                "        <label for=\"file\">Sube tu archivo aquí.</label><br>\n" +
                "        <input type=\"file\" id=\"file\" name=\"file\"><br><br>\n" +
                "        <input type=\"button\" value=\"Visualiza tu Archivo\" onclick=\"uploadFile()\">\n" +
                "    </form>\n" +
                "\n" +
                "    <div id=\"uploadMsg\"></div>\n" +
                "\n" +
                "    <script>\n" +
                "        function uploadFile() {\n" +
                "            const fileInput = document.getElementById(\"file\");\n" +
                "            const file = fileInput.files[0];\n" +
                "            const formData = new FormData();\n" +
                "            formData.append(\"file\", file);\n" +
                "\n" +
                "            const xhr = new XMLHttpRequest();\n" +
                "            xhr.onload = function () {\n" +
                "                document.getElementById(\"uploadMsg\").innerHTML = this.responseText;\n" +
                "            };\n" +
                "            xhr.open(\"POST\", \"/upload\");\n" +
                "            xhr.send(formData);\n" +
                "        }\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }

}