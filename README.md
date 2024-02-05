# TALLER 2: DISEÑO Y ESTRUCTURACIÓN DE APLICACIONES DISTRIBUIDAS EN INTERNET

En este taller, se exploro la arquitectura de las aplicaciones distribuidas y el cómo construir un servidor web que soporte múltiples solicitudes seguidas (no concurrentes). Para ello, utilizaremos Java y las librerías para manejo de la red.

El servidor web construido será capaz de leer archivos del disco local y retornar todos los archivos solicitados, incluyendo páginas html, archivos java script, css e imágenes. Para probar el servidor, se construyo una aplicación web. Además, incluiremos en la aplicación la comunicación asíncrona con unos servicios REST en el backend.

## Empezando

- El proyecto contiene una solo clase que sera utilizada para probar lo pedido en este taller  que es : [HttpServer](https://github.com/MPulidoM/Taller2_AREP/blob/main/Taller2_AREP/src/main/java/edu/escuelaing/arem/ASE/app/HttpServer.java).
  
- En el tema de pruebas lo encontramos en la carpeta destinada a los Test, la clase es la siguiente :  [HttpServerTest](https://github.com/MPulidoM/Taller2_AREP/blob/main/Taller2_AREP/src/test/java/edu/escuelaing/arem/ASE/app/HttpServerTest.java).
  
- También se cuenta con la carpeta de recursos donde estaran los archivos que se probraran en la aplicación web : [resource](https://github.com/MPulidoM/Taller2_AREP/tree/main/Taller2_AREP/src/main/resource)
  
- Por últmo en el tema de la docuemntación a detalle de los metodos usados se pueden encontrar [doc](https://github.com/MPulidoM/Taller2_AREP/tree/main/Taller2_AREP/doc).

   
### Requisitos previos

[Maven](https://maven.apache.org/) : Con esta herramienta se creo la estructura del proyecto y se manejan las dependencias que se necesitan

[Git](https://git-scm.com/) : Se basa en un sistema de control de versiones distribuido, donde cada desarrollador tiene una copia completa del historial del proyecto.

Para asegurar una correcta instalación de Maven, es crucial confirmar que la versión del JDK de Java sea compatible. Si el JDK no está actualizado, la instalación de las versiones actuales de Maven podría fallar, generando problemas durante el uso de la herramienta.
```
java -version 
```

### Instalando

Para poder ver el funcionamiento de este taller , en si instalar este programa, debe clonar el repositorio en su maquina local. Para esto utilice el siguiente comando y ejecutelo.

```
$ git clone https://github.com/MPulidoM/Taller2_AREP.git
```
Para poder ver la aplicación web , se debe inicar en el IDE Utilizado el [HttpServerTest](https://github.com/MPulidoM/Taller2_AREP/blob/main/Taller2_AREP/src/test/java/edu/escuelaing/arem/ASE/app/HttpServerTest.java). Lo siguiente es abrir el navegador , en el caso de este taller se trabajo más que todo en Google, y poner en el siguiente 
```
localhost:35000
```

## Ejecutando las pruebas

Ya teniendo el proyecto compilado sin ningún problemas utilizando 
```
mvn package
```
Utilice el siguiente comando para ver las pruebas hechas
```
mvn test
```

## Arquitectura

Este servidor HTTP simple en Java maneja cargas de archivos con el método POST y sirve una página HTML predeterminada con un formulario de carga de archivos. Cuando un archivo es cargado, el servidor crea un archivo con el mismo nombre en el directorio especificado y devuelve el contenido del archivo en el formato adecuado al cliente. Si el URI solicitado no está relacionado con la carga de archivos, se devuelve la página HTML predeterminada.

## Pruebas Realizadas

- Como se ve la pagina:
- Prueba con Imágenes:
- Prueba HTML:
- Prueba Java Script:
- Prueba TXT:
- Prueba Css:
  


## Construido con

* [Maven](https://maven.apache.org/) - Gestión de dependencias
* [Java](https://www.java.com/es/) - Lenguaje Utilizado
* [GitHub](https://git-scm.com/) - Control de Versiones



## Autores

* **Mariana Pulido Moreno** - *Arep 101* - [MPulidoM](https://github.com/MPulidoM)
