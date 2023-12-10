// package com.mkyong;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.*;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


//todo fazer um file.json para cada qualidade de video !!

//Class that takes care of Uploading a movie via CMS app
@Path("/upload")
public class UploadFile {

    @GET
    public String ping() {
        System.out.println("Received a Get request for upload");
        return "Test connection of /upload";
    }

    //
    //we store the original mp4 file 
    private static final String UPLOAD_FOLDER = "/home/rui/netflix_clone/mkyong/jax-rs/jersey/jersey-jetty/Files-Upload";

    //✅
    @POST
    @Path("/movie")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
            @FormDataParam("moviename") String moviename,
            @FormDataParam("duration") String duration,
            @FormDataParam("year") String year) {
        if (uploadedInputStream == null || fileDetail == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Dados de upload de arquivo inválidos").build();
        }

        try {
            String fileName = fileDetail.getFileName();
            String uploadedFileLocation = UPLOAD_FOLDER + File.separator + fileName;

            // (VERSÃO ANTERIOR) String Output = fileName.replace(".mp4", "");
	    String Output = moviename;
            // Save file received from input
            saveToFile(uploadedInputStream, uploadedFileLocation);

            /* Generates the Low(360p) and high(1080p) versions */

            // Location where the .mp4 are going to be stored are going to be stored
            String output360pFilePath = "/home/rui/Desktop/Videos/" + Output + "_360p.mp4";
            String output1080pFilePath = "/home/rui/Desktop/Videos/" + Output + "_1080p.mp4";

            // ffmpeg command for 360p conversion (Ultrafast version)


            // ffmpeg -i input.mp4 -codec copy  -s 480x360 
            /*
             todo : (ALTERAR FFMPEG ABAIXO)  ffmpeg -i input.mp4 -codec copy  -s 480x360 output.mp4
                (verificar)
                String command360p = "ffmpeg -i " + uploadedFileLocation
                    + " -vcodec libx264 -preset ultrafast -s 480x360 -acodec copy " + output360pFilePath;
                String command1080p = "ffmpeg -i " + uploadedFileLocation
                    + " -vcodec libx264 -preset ultrafast -s 1920x1080 -acodec copy " + output1080pFilePath;

             */
            // input_resp.mp4

            String command360p = "ffmpeg -i " + uploadedFileLocation
                    + " -codec copy -s 480x360 " + output360pFilePath;
            String command1080p = "ffmpeg -i " + uploadedFileLocation
                    + " -codec copy -s 1920x1080 " + output1080pFilePath;

            // executes the FFmpeg command
            boolean teste1 = executeFFmpegCommand(command360p);
            
            boolean teste2 = executeFFmpegCommand(command1080p);

            System.out.println("Result 360p : " + teste1);
            System.out.println("Result 1080p : " + teste2);

            // TODO add the date to the current json file in case multiple files
            // TODO with the same name get uploaded


            // Creates the .json configuration and saves it to nginx conf file
            
            //todo criar dois ficheiros json (..._360p e ..._1080p)
            //360p json file 
            String OutputJsonPath_360 = "/home/rui/Desktop/json/" + Output + "_360p.json";
            System.out.println("Creating and Saving " + Output + "_360p.json to " + OutputJsonPath_360);
            String json = createJSON(Output,"360p");
            
            //1080p jsonfile 
            String OutputJsonPath_1080 = "/home/rui/Desktop/json/" + Output + "_1080p.json";
            System.out.println("Creating and Saving " + Output + "_1080p.json to " + OutputJsonPath_1080);
            String json2 = createJSON(Output, "1080p");

            //saves both configurations to respective nginx defined folder
            saveJSONToFile(json, OutputJsonPath_360);
            saveJSONToFile(json2, OutputJsonPath_1080);

            String output = "Arquive saved to : " + uploadedFileLocation;
  
            //saves the Movie to Db
            try {
                String query = "Insert into movie(MovieId,MovieName,Duration,Year,Link_Low,Link_High) value(?,?,?,?,?,?)";
                System.out.println("Adicionar o filme a BD");
                PreparedStatement preparedStmt = MainApp.con.prepareStatement(query);
                int next_id = MovieRsrc.getMovies().size() + 1;
                System.out.println("Debug =>" + next_id);
                preparedStmt.setInt(1, next_id);
                preparedStmt.setString(2, moviename);
                preparedStmt.setString(3, duration);
                preparedStmt.setString(4, year);

                String Link_Low =
                    "https://35.195.14.2/video/"+ Output+"_360p.json" + "/master.m3u8";

                String Link_High = 
                    "https://35.195.14.2/video/"+ Output+"_1080p.json" + "/master.m3u8";


                //Link is the same for both versions
                preparedStmt.setString(5, Link_Low);
                preparedStmt.setString(6, Link_High);

                preparedStmt.execute();
                System.out.println("Sucessfuly added the movie to database");

            } catch (SQLException e) {
                System.out.println("Error ocurred while trying to add a movie to the database");
                throw new RuntimeException(e);
            }



            return Response.status(Response.Status.OK).entity(output).build();
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao salvar o arquivo: " + e.getMessage()).build();
        }
    }

    /* Auxiliar methods */

    //
    private void saveToFile(InputStream uploadedInputStream, String uploadedFileLocation) throws IOException {
        try (OutputStream out = new FileOutputStream(new File(uploadedFileLocation))) {
            int read;
            byte[] bytes = new byte[1024];
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
        }
    }

    // Executes the ffmpeg command (for 360p and 1080p conversions)
    private boolean executeFFmpegCommand(String command) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            /* Debug message */
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int exitcode = process.waitFor();

            System.out.println("In executeFFmpegCommand");
            if (exitcode == 0) {
                System.out.println("Sucessfuly executed command : " + command);
                return true;
            } else {
                System.out.println("Unsucessfuly executed command : " + command);
                return false;
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.err.println("Error during execution of FFmpeg command");
            return false;
        }
    }

    // Creates a Json object (for putting the json file in nginx)
    //todo passar o 360p , e 1080p como argumento
    public static String createJSON(String inputFileName,String quality) {

        JSONObject jsonRoot = new JSONObject();

        JSONArray sequences = new JSONArray();

        // Creates the first json object (refering to qualityp)
        JSONObject sequence = new JSONObject();
        JSONArray clips = new JSONArray();
        JSONObject clip = new JSONObject();

        clip.put("type", "source");
        clip.put("path", "/home/rui/Desktop/Videos/" + inputFileName + "_"+ quality +".mp4");
        clips.add(clip);
        sequence.put("clips", clips);

        sequences.add(sequence);
        jsonRoot.put("sequences", sequences);
        

        return jsonRoot.toJSONString();
    }

    public static void saveJSONToFile(String json, String outputPath) {

        try {
            FileWriter fileWriter = new FileWriter(outputPath);
            fileWriter.write(json);
            fileWriter.close();
            System.out.println("JSON salvo em " + outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // /(...)/input
        String inputFileName = "input";
        // String outputPath = "/home/rui/Desktop/json/batatas.json";
        String json = createJSON(inputFileName,"1080p");
        // saveJSONToFile(json, outputPath);
        System.out.println(json);
    }

}
