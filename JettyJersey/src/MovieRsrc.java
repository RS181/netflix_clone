// package com.mkyong;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mkyong.DB.*;

@Path("/movie")
public class MovieRsrc {

    // ✅
    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public static List<Movie> getMovies() {
        List<Movie> list = new ArrayList<>();

        try {
            // DB.OpenDatabase();
            Statement statement = MainApp.con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM movie");
            while (rs.next()) {
                Movie movie = new Movie();
                movie.setmovieid(rs.getInt(1));
                movie.setmoviename(rs.getString(2));
                movie.setduration(rs.getString(3));
                movie.setyear(rs.getString(4));
                movie.setlink_low(rs.getString(5));
                movie.setlink_high(rs.getString(6));
                // System.out.println(rs.getString(1) + " " +rs.getString(2) + " " +
                // rs.getString(3));
                list.add(movie);
            }

            rs.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // DB.CloseDatabase();
        return list;
    }

    // ✅
    // Gets all te information about all the movies
    // that "kinda" matches the given moviename
    @Path("/search/{name}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public static List<Movie> SearchForMovie(@PathParam("name") String name) {

        List<Movie> ans = GetMovieByName(name);

        return ans;
    }

    // removes a movie given the MovieId associated with it (including .json and
    // .mp4 associated with nginx)
    @Path("/remove")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public static RespUser RemoveMovie(Movie movie) {
        // todo Nao esquecer que temos de remover os
        // todo respetivos ficheiros do nginx tambem
        RespUser ans = new RespUser();
        
        System.out.println("Trying to remove (" +movie.getmovieid() + ")");
        

        int MovieId = movie.getmovieid();
        if (MovieIdExists(MovieId) == false) {
            System.out.println("Error the Movie id " + MovieId + " isnt associated with any movie");
            ans.setStatus("NOT OK");
            ans.setError("There is no movie with id " + MovieId);
            return ans;
        }

        try {
            //gets the link associated with the movie that is going to be removed
            String link = GetLink(MovieId);
            System.out.println("link = " + link);

            String query = "DELETE FROM movie WHERE MovieId = ?";

            PreparedStatement preparedStmt = MainApp.con.prepareStatement(query);
            preparedStmt.setInt(1, MovieId);
            preparedStmt.execute();

            // updates the id of all movies
            UpdateMovieId();

            /*  deletes the .mp4 and .json files associated with this movie*/

            //gets the .json file 
            String filename = extractFileNameFromURL(link);
            System.out.println("Json filename = " + filename);

            String JsonfilePath = "/home/rui/Desktop/json/" + filename;
            String JsonfilePath1080 = "/home/rui/Desktop/json/" + filename.replace("_360p","_1080p");

            //saves the content of .json as a string 
            String jsonContent = readJSONFileAsString(JsonfilePath);


            if (jsonContent != null){
                //gets the mp4 path of each file that is going to be removed
                String Path_360p = extractPathValue(jsonContent);
                String Path_1080p = extractPathValue(jsonContent).replace("_360", "_1080");

                System.out.println("Removed .360p file = " +removeFile(Path_360p));
                System.out.println("Removed .1080p file = " + removeFile(Path_1080p));
                System.out.println("Removed 360p.json file = " + removeFile(JsonfilePath));
                System.out.println("Removed 1080p.json file = " + removeFile(JsonfilePath1080));

            }
            else {
                System.out.println("Error ocurred while trying to remove files associared with id: " + MovieId);
            }


            ans.setStatus("OK");
            ans.setError("none");
            System.out.println("Successfully removed the movie with id : " + MovieId);
            return ans;

        } catch (SQLException e) {
            System.out.println("Error ocurred while trying to remove movie with id = " + MovieId);
            throw new RuntimeException(e);
        }



    }

    /* helper methods */

    //returns the link associated with a certain Movie id 
    public static String GetLink(int MovieId){
        try{
            String query = "SELECT Link_Low FROM movie WHERE MovieId = ? ";

            PreparedStatement preparedStatement = MainApp.con.prepareStatement(query);

            preparedStatement.setInt(1, MovieId);

            ResultSet rs = preparedStatement.executeQuery();

            while(rs.next()){
                return rs.getString(1);
            }
            return null;

        }catch(SQLException e ){
            System.out.println("Error ocurred while getting link associated with MovieId "+MovieId);
            throw new RuntimeException(e);
        }


    }




    // Return list of movies that the Movie name
    // contains a certain string name
    // (supose to work like a search bar ) ✅
    public static List<Movie> GetMovieByName(String name) {
        List<Movie> list = new ArrayList<>();
        try {
            // DB.OpenDatabase();
            String query = "SELECT * FROM movie WHERE MovieName LIKE ? ORDER BY Moviename,MovieId";
            PreparedStatement preparedStatement = MainApp.con.prepareStatement(query);
            preparedStatement.setString(1, "%" + name + "%");

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Movie movie = new Movie();
                movie.setmovieid(rs.getInt(1));
                movie.setmoviename(rs.getString(2));
                movie.setduration(rs.getString(3));
                movie.setyear(rs.getString(4));
                movie.setlink_low(rs.getString(5));
                movie.setlink_high(rs.getString(6));
                list.add(movie);
            }
            rs.close();
            preparedStatement.close();

        } catch (SQLException e) {
            System.out.println("Error ocurred while trying to get all movies with : " + name
                    + "  in their name from the Database");
            throw new RuntimeException(e);
        }
        // DB.CloseDatabase();
        return list;
    }

    // Indicates if there exists any movie whith a certain MovieId ✅
    public static boolean MovieIdExists(int MovieId) {

        try {
            String query = "SELECT Count(*) FROM movie WHERE MovieId = ?";

            PreparedStatement preparedStmt = MainApp.con.prepareStatement(query);
            preparedStmt.setInt(1, MovieId);

            ResultSet rs = preparedStmt.executeQuery();
            rs.next();
            int aux = rs.getInt(1);
            System.out.println("=>" + aux);
            return aux == 1;

        } catch (SQLException e) {
            System.out.println("Error ocurred while trying to find the movie associated with id " + MovieId);
            throw new RuntimeException(e);
        }
    }

    // ✅
    public static void UpdateMovieId() {
        try {
            String query = "SELECT MovieName FROM movie";

            Statement stmt = MainApp.con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            int cur_id = 1;
            while (rs.next()) {

                System.out.println(rs.getString(1));
                String query_2 = "UPDATE movie SET MovieId = ? WHERE MovieName LIKE BINARY ?";

                PreparedStatement preparedStmt = MainApp.con.prepareStatement(query_2);

                preparedStmt.setInt(1, cur_id);
                preparedStmt.setString(2, rs.getString(1));

                preparedStmt.execute();

                cur_id++;
            }
            System.out.println("Updated Movie id Sucessfuly");
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("Error ocurred while trying to update Movie id");
            throw new RuntimeException(e);
        }
    }

    // removes the file in the given path ✅
    public static boolean removeFile(String filePath) {
        try {
            File file = new File(filePath);

            if (file.delete()) {
                System.out.println("Sucessfuly removed the file: " + filePath);
                return true;
            } else {
                System.out.println("Error while trying to remove the file : " + filePath);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Extracts the path value of .json file
    public static String extractPathValue(String jsonInput) {
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(jsonInput);

            if (obj instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) obj;
                JSONArray sequences = (JSONArray) jsonObject.get("sequences");

                for (Object sequence : sequences) {
                    if (sequence instanceof JSONObject) {
                        JSONObject sequenceObject = (JSONObject) sequence;
                        JSONArray clips = (JSONArray) sequenceObject.get("clips");

                        for (Object clip : clips) {
                            if (clip instanceof JSONObject) {
                                JSONObject clipObject = (JSONObject) clip;
                                String path = (String) clipObject.get("path");

                                if (path != null) {
                                    return path;
                                }
                            }
                        }
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    // reads the content of a json file and returns it as a string
    public static String readJSONFileAsString(String filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }



    public static String extractFileNameFromURL(String url) {
        String[] parts = url.split("/");
        String filename = "";
        for (String str : parts){
            if (str.contains(".json"))
                filename = str;
        }

        return filename;    
    }

    public static void main(String[] args) {

        // List<Movie> movie = GetMovieByName("the");
        // for (Movie m : movie) {
        // System.out.println("=>" + m.getmovieid() + " " + m.getmoviename());
        // }

        // System.out.println(MovieIdExists(1));s
        // String filePath =
        // "/home/rui/Desktop/PDM/netflix_clone/mkyong/jax-rs/jersey/jersey-jetty/Files-Upload/bananas.txt";
        // removeFile(filePath);

        // String jsonInput =
        // "{\"sequences\":[{\"clips\":[{\"path\":\"\\/home\\/rui\\/Desktop\\/Videos\\/testfile_360p.mp4\",\"type\":\"source\"}]},{\"clips\":[{\"path\":\"\\/home\\/rui\\/Desktop\\/Videos\\/testfile_1080p.mp4\",\"type\":\"source\"}]}]}";
        // String pathValue = extractPathValue(jsonInput);
        // System.out.println("Valor do caminho: " + pathValue);

        String Db_link = "http://127.0.0.1/video/testfile.json/master.m3u8 ";
        String filename = extractFileNameFromURL(Db_link);
        System.out.println("filename = " + filename);

        String filePath = "/home/rui/Desktop/json/" + filename;
        String jsonContent = readJSONFileAsString(filePath);
        if (jsonContent != null) {
            System.out.println("Conteúdo do arquivo JSON:");
            System.out.println(extractPathValue(jsonContent));
            System.out.println(extractPathValue(jsonContent).replace("_360", "_1080"));
        }

        System.out.println(GetLink(7));

    }

}