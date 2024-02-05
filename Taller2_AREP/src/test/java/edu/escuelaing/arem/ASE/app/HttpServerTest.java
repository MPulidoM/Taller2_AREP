package edu.escuelaing.arem.ASE.app;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class HttpServerTest {
    @Test
    public void testJPGImageConversionToHTML(){
        HttpServer httpServer = new HttpServer();
        File file = new File("Taller2_AREP\\\\src\\\\main\\\\resource\\\\joe.jpg");
        String result;
        try {
            result = httpServer.toImage(file, "jpg");
            assertTrue(result.contains("Content-Type: text/jpg"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void testHTMLFileConversionToHTML(){
        HttpServer httpServer = new HttpServer();
        File file = new File("Taller2_AREP\\\\src\\\\main\\\\resource\\\\.jpg");
        String result;
        try{
            result = httpServer.toHTML(file);
            assertTrue(result.contains("Content-Type: text/html"));
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testJSFileConversionToHTML(){
        HttpServer httpServer = new HttpServer();
        File file = new File("Taller2_AREP\\\\src\\\\main\\\\resource\\\\kitConnor.js");
        String result;
        try{
            result = httpServer.toJs(file);
            assertTrue(result.contains("application/javascript"));
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    @Test
    public void testCSSFileConversionToHTML(){
        HttpServer httpServer = new HttpServer();
        File file = new File("Taller2_AREP\\\\src\\\\main\\\\resource\\\\kitConnor.css");
        String result;
        try{
            result = httpServer.toJs(file);
            assertTrue(result.contains("Content-Type: text/css"));
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    @Test
    public void testTXTFileConversionToHTML(){
        HttpServer httpServer = new HttpServer();
        File file = new File("Taller2_AREP\\\\src\\\\main\\\\resource\\\\Prueba.txt");
        String result;
        try{
            result = httpServer.toHTML(file);
            assertTrue(result.contains("Content-Type: text/css"));
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}

