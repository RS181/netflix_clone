// package com.mkyong;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mkyong.DB.*;

public class MainApp {

    // public static final String BASE_URI = "http://localhost:8080/";
//*    public static final String BASE_URI = "http://35.195.14.2/";  (versão anterior)//* */

    //Abaixo configuração de proxy
    public static final String BASE_URI = "http://127.0.0.1:4321";

    //public static final String BASE_URI = "http://0.0.0.0:5050/"; //* */
    //Load database to connection (to avoid constant opening and closing of db)
    public static Connection con = DB.OpenDatabase();

    public static Server startServer() {
        //Load all the resource files we want here
        final ResourceConfig config = new ResourceConfig(UserConnection.class,MyResource.class,MovieRsrc.class,UploadFile.class);
        
        // config.register(MultiPartFeature.class);
        // config.register(MultiPartFeature.class);

        final Server server = JettyHttpContainerFactory.createServer(URI.create(BASE_URI), config);

        return server;

    }

    public static void main(String[] args) {

        try {

            final Server server = startServer();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    System.out.println("Shutting down the application...");
                    server.stop();
                    System.out.println("Closing connection to Database");
                    DB.CloseDatabase();
                    System.out.println("Done, exit.");

                } catch (Exception e) {
                    Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, e);
                }
            }));

            System.out.println(String.format("Application started.%nStop the application using CTRL+C"));

            // block and wait shut down signal, like CTRL+C
            Thread.currentThread().join();

        } catch (InterruptedException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }



    }

}
// 
