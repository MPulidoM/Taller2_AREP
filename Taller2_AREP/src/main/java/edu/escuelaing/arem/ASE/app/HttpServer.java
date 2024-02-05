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
                outputLine = findBoundaries(uriWithFileName);
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

    public static String findBoundaries(String inputString) {
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

    private static String getFileExtension(String filename) {
        int extensionIndex = filename.lastIndexOf(".");
        return extensionIndex != -1 ? filename.substring(extensionIndex + 1) : "";
    }




    public static String toImage(File file, String type) throws IOException{
        byte[] bytes = Files.readAllBytes(file.toPath());
        String base64 = Base64.getEncoder().encodeToString(bytes);
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/"+ type + "\r\n"
                + "\r\n"
                + "<center><img src=\"data:image/" + type + ";base64," + base64 + "\"></center>";
    }

    public static String toHTML(File file) throws IOException {
        StringBuilder body = fromArchiveToString(file);
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<center>" + body + "</center>";
    }

    public static String toCSS(File file) throws IOException{
        StringBuilder body = fromArchiveToString(file);
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/css\r\n"
                + "\r\n"
                + "<center>"+body+"</center>";
    }

    public static String toJs(File file)throws IOException{
        StringBuilder body = fromArchiveToString(file);
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: application/javascript\r\n"
                + "\r\n"
                + "<center>"+body+"</center>";
    }
    /**
     * This method re write the file into a line by line String Builder
     * @param file
     * @return the file components in a StringBuilder
     * @throws IOException
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