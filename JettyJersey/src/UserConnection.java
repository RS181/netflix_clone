// package com.mkyong;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

//✅ -> indica que funciona como esperado

@Path("/connect")
public class UserConnection {

    // ✅
    @GET
    @Path("/all/usr")
    @Produces(MediaType.APPLICATION_JSON)
    public static List<User> getUsers() {
        List<User> list = new ArrayList<>();
        System.out.println("dentro de getusers");

        try {
            // DB.OpenDatabase();
            Statement statement = MainApp.con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM user");
            while (rs.next()) {
                User user = new User();
                user.setId(Integer.valueOf(rs.getString(1)));
                user.setlogin(rs.getString(2));
                user.setpassword(rs.getString(3));
                // System.out.println(rs.getString(1) + " " +rs.getString(2) + " " +
                // rs.getString(3));
                list.add(user);
            }

            rs.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // DB.CloseDatabase();
        return list;
    }

    // same as above but for admins ✅
    @GET
    @Path("/all/admin")
    @Produces(MediaType.APPLICATION_JSON)
    public static List<User> getAdmins() {
        List<User> list = new ArrayList<>();

        try {
            // DB.OpenDatabase();
            Statement statement = MainApp.con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM admin");
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt(1));
                user.setlogin(rs.getString(2));
                user.setpassword("SECRET");
                // System.out.println(rs.getString(1) + " " +rs.getString(2) + " " +
                // rs.getString(3));
                list.add(user);
            }

            rs.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // DB.CloseDatabase();
        return list;
    }

    // ✅ (User registration)
    @Path("/signup/usr")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public static RespUser SignUp(User new_user) {
        RespUser ans = new RespUser();

        String login = new_user.getlogin();
        String Password = new_user.getpassword();

        if (CheckloginExists(login) != true) {
            // creates the user on DB
            CreateUser(login, Password);
            // Configures custom responce
            ans.setStatus("OK");
            ans.setError("none");
            System.out.println("Successfully created the use with login : " + login);
            return ans;
        }

        ans.setStatus("NOT OK");
        ans.setError("User already exists in DataBase");
        System.out.println("Error: " + login + " already exists in Database");

        return ans;
    }

    // ✅(Admin registration)
    @Path("/signup/admin")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public static RespUser SignUpA(User new_user) {
        RespUser ans = new RespUser();

        String login = new_user.getlogin();
        String Password = new_user.getpassword();
        if (CheckAdminloginExists(login) != true) {
            // creates the user on DB
            CreateAdmin(login, Password);
            // Configures custom responce
            ans.setStatus("OK");
            ans.setError("none");
            System.out.println("Successfully created the use with login : " + login);
            return ans;
        }

        ans.setStatus("NOT OK");
        ans.setError("User already exists in DataBase");
        System.out.println("Error: " + login + " already exists in Database");

        return ans;
    }

    // ✅
    @Path("/remove/usr")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    // since login is unique we can remove by login
    public static RespUser RemoveUsr(User usr) {
        RespUser ans = new RespUser();

        String login = usr.getlogin();
        int usr_id = GetUserid(login);
        if (usr_id == -1) {
            System.out.println("Error , there is no user with login = " + login);
            ans.setStatus("NOT OK");
            ans.setError("There is no user with login " + login);
            return ans;
        }

        try {
            String query = "DELETE FROM user WHERE idUser = ?";

            PreparedStatement preparedStmt = MainApp.con.prepareStatement(query);
            preparedStmt.setInt(1, usr_id);
            preparedStmt.execute();

            // updates the id of all users
            UpdateUserId();

            ans.setStatus("OK");
            ans.setError("none");
            System.out.println("Successfully removed the user with login : " + login);
            return ans;

        } catch (SQLException e) {
            System.out.println("Error ocurred while trying to remove user with login = " + login);
            throw new RuntimeException(e);
        }

    }

    // ✅
    @Path("/remove/admin")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public static RespUser RemoveAdmin(User usr) {
        RespUser ans = new RespUser();

        String login = usr.getlogin();
        int usr_id = GetAdminid(login);
        if (usr_id == -1) {
            System.out.println("Error , there is no admin with login = " + login);
            ans.setStatus("NOT OK");
            ans.setError("There is no admin with login " + login);
            return ans;
        }

        try {
            String query = "DELETE FROM admin WHERE idAdmin = ?";

            PreparedStatement preparedStmt = MainApp.con.prepareStatement(query);
            preparedStmt.setInt(1, usr_id);
            preparedStmt.execute();

            // atualizar os id's de todos os utilizadores automaticamente
            UpdateAdminId();

            ans.setStatus("OK");
            ans.setError("none");
            System.out.println("Successfully removed the admin with login : " + login);
            return ans;

        } catch (SQLException e) {
            System.out.println("Error ocurred while trying to remove user with login = " + login);
            throw new RuntimeException(e);
        }

    }

    // ✅ (User login)
    @Path("/login/usr")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public static RespUser login(User user) {
        RespUser ans = new RespUser();
        String login = user.getlogin();
        String Password = user.getpassword();
        if (CheckUserExists(login, Password) == true) {
            // configures custom responce
            ans.setStatus("OK");
            ans.setError("none");
            System.out.println("login Sucessful");
            return ans;
        }

        ans.setStatus("NOT OK");
        ans.setError("login Unsucessful");
        return ans;
    }

    // ✅(Admin login)
    @Path("/login/admin")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public static RespUser loginA(User user) {
        RespUser ans = new RespUser();
        String login = user.getlogin();
        String Password = user.getpassword();
        if (CheckAdminExists(login, Password) == true) {
            // configures custom responce
            ans.setStatus("OK");
            ans.setError("none");
            System.out.println("login Sucessful");
            return ans;
        }

        ans.setStatus("NOT OK");
        ans.setError("login Unsucessful");
        return ans;
    }

    /* helper methods */

    // check if exists a user in the db with given login✅
    // return true -> there is already a user with this login
    public static boolean CheckloginExists(String login) {
        try {
            // DB.OpenDatabase();
            // LIKE BINARY makes it case sensitive
            String query = "SELECT COUNT(*) FROM user WHERE login LIKE BINARY ?";

            PreparedStatement preparedStatement = MainApp.con.prepareStatement(query);
            preparedStatement.setString(1, login);
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            int aux = rs.getInt(1);
            // System.out.println(aux);

            // DB.CloseDatabase();
            return aux == 1;
        } catch (SQLException e) {
            System.out.println(
                    "Error ocurred while trying to check if a user with the login: " + login + " exists in Database");
            throw new RuntimeException(e);

        }
    }

    // same as above but for admins ✅
    public static boolean CheckAdminloginExists(String login) {
        try {
            // DB.OpenDatabase();
            // LIKE BINARY makes it case sensitive
            String query = "SELECT COUNT(*) FROM admin WHERE login LIKE BINARY ?";

            PreparedStatement preparedStatement = MainApp.con.prepareStatement(query);
            preparedStatement.setString(1, login);
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            int aux = rs.getInt(1);
            // System.out.println(aux);

            // DB.CloseDatabase();
            return aux == 1;
        } catch (SQLException e) {
            System.out.println(
                    "Error ocurred while trying to check if a user with the login: " + login + " exists in Database");
            throw new RuntimeException(e);

        }
    }

    // checks if there is a user in the db that has given ✅
    // login and password (its case sensitive)
    // true -> when there is one user on db with that login and password
    public static boolean CheckUserExists(String login, String password) {

        try {
            String query = "SELECT Count(*) FROM user WHERE login LIKE BINARY ? AND Password LIKE md5(?);";
            PreparedStatement preparedStmt = MainApp.con.prepareStatement(query);
            preparedStmt.setString(1, login);
            preparedStmt.setString(2, password);

            ResultSet rs = preparedStmt.executeQuery();
            rs.next();
            int aux = rs.getInt(1);

            System.out.println("Number of user with pair login/password = " + aux);
            return aux == 1;
        } catch (SQLException e) {
            System.out.println("Error ocurred while trying to check if there is a user with a pair login and password");
            throw new RuntimeException(e);
        }
    }

    // same as above but for admins ✅
    public static boolean CheckAdminExists(String login, String password) {
        try {
            String query = "SELECT Count(*) FROM admin WHERE login LIKE BINARY ? AND Password LIKE md5(?);";
            PreparedStatement preparedStmt = MainApp.con.prepareStatement(query);
            preparedStmt.setString(1, login);
            preparedStmt.setString(2, password);

            ResultSet rs = preparedStmt.executeQuery();
            rs.next();
            int aux = rs.getInt(1);

            System.out.println("Number of admin with pair login/password = " + aux);
            return aux == 1;
        } catch (SQLException e) {
            System.out.println("Error ocurred while trying to check if there is a user with a pair login and password");
            throw new RuntimeException(e);
        }
    }

    // Creates a user (NOTE: does not verify if a user with the same
    // login exists)✅
    public static boolean CreateUser(String login, String password) {
        try {
            int next_available_id = getUsers().size() + 1;

            // Clean way to define the values we want to add to database
            // https://stackoverflow.com/questions/59147960/how-to-insert-into-mysql-database-with-java

            // Note password is being encripted in mysql
            String query = "Insert into user(idUser,login,Password) value(?,?,md5(?))";

            PreparedStatement preparedStmt = MainApp.con.prepareStatement(query);
            preparedStmt.setInt(1, next_available_id);
            preparedStmt.setString(2, login);
            preparedStmt.setString(3, password);

            preparedStmt.execute();

            return true;
        } catch (SQLException e) {
            System.out.println("Error ocurred while trying to add  user : " + login + " to Database");
            // e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    // ✅
    public static boolean CreateAdmin(String login, String password) {
        try {
            int next_available_id = getAdmins().size() + 1;

            // Clean way to define the values we want to add to database
            // https://stackoverflow.com/questions/59147960/how-to-insert-into-mysql-database-with-java

            // Note password is being encripted in mysql
            String query = "Insert into admin(idAdmin,Login,Password) value(?,?,md5(?))";

            PreparedStatement preparedStmt = MainApp.con.prepareStatement(query);
            preparedStmt.setInt(1, next_available_id);
            preparedStmt.setString(2, login);
            preparedStmt.setString(3, password);

            preparedStmt.execute();

            return true;
        } catch (SQLException e) {
            System.out.println("Error ocurred while trying to add  ADMIN : " + login + " to Database");
            // e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    // since login is unique (we can get the userid using it) ✅
    public static int GetUserid(String login) {
        if (!CheckloginExists(login)) {
            System.out.println("Error , there is no user with login = " + login);
            return -1;
        }

        try {
            String query = "SELECT idUser FROM user WHERE login LIKE BINARY ?";

            PreparedStatement preparedStmt = MainApp.con.prepareStatement(query);
            preparedStmt.setString(1, login);

            ResultSet rs = preparedStmt.executeQuery();
            rs.next();

            int ans = rs.getInt(1);

            return ans;

        } catch (SQLException e) {
            System.out.println("Error ocurred while trying to get the user id of " + login);
            throw new RuntimeException(e);
        }
    }

    // same as above but for admins ✅
    public static int GetAdminid(String login) {
        if (!CheckAdminloginExists(login)) {
            System.out.println("Error , there is no admin with login = " + login);
            return -1;
        }

        try {
            String query = "SELECT idAdmin FROM admin WHERE Login LIKE BINARY ?";

            PreparedStatement preparedStmt = MainApp.con.prepareStatement(query);
            preparedStmt.setString(1, login);

            ResultSet rs = preparedStmt.executeQuery();
            rs.next();

            int ans = rs.getInt(1);

            return ans;

        } catch (SQLException e) {
            System.out.println("Error ocurred while trying to get the admin id of " + login);
            throw new RuntimeException(e);
        }
    }

    // ✅
    public static void UpdateUserId() {
        try {
            String query = "SELECT Login FROM user";

            Statement stmt = MainApp.con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            int cur_id = 1;
            while (rs.next()) {

                System.out.println(rs.getString(1));
                String query_2 = "UPDATE user SET idUser = ? WHERE Login LIKE BINARY ?";

                PreparedStatement preparedStmt = MainApp.con.prepareStatement(query_2);

                preparedStmt.setInt(1, cur_id);
                preparedStmt.setString(2, rs.getString(1));

                preparedStmt.execute();

                cur_id++;
            }
            System.out.println("Updated user id Sucessfuly");
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("Error ocurred while trying to update user id");
            throw new RuntimeException(e);
        }
    }

    // ✅
    public static void UpdateAdminId() {
        try {
            String query = "SELECT Login FROM admin";

            Statement stmt = MainApp.con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            int cur_id = 1;
            while (rs.next()) {

                System.out.println(rs.getString(1));
                String query_2 = "UPDATE admin SET idAdmin = ? WHERE Login LIKE BINARY ?";

                PreparedStatement preparedStmt = MainApp.con.prepareStatement(query_2);

                preparedStmt.setInt(1, cur_id);
                preparedStmt.setString(2, rs.getString(1));

                preparedStmt.execute();

                cur_id++;
            }
            System.out.println("Updated admin id Sucessfuly");
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("Error ocurred while trying to update admin id");
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        List<User> users = getUsers();
        System.out.println("== Users ==");
        for (User u : users)
            System.out.println("=>" + u.getId() + " " + u.getlogin() + " " + u.getpassword());

        List<User> admins = getAdmins();
        System.out.println("== Admins ==");
        for (User a : admins)
            System.out.println("=>" + a.getId() + " " + a.getlogin() + " " + a.getpassword());

        System.out.println("tests");
        UpdateUserId();
        // System.out.println(GetUserid("teste_post_2"));
        // System.out.println(GetAdminid("root"));
        // System.out.println(users.size());

        // System.out.println(CreateUser("teste","1234"));
        // System.out.println(CreateUser("guest", "guest"));
        // System.out.println(CheckloginExists("rui"));
        // System.out.println("login :" +CheckUserExists("Kenny", "1234"));
    }

}
