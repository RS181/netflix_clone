// package com.mkyong;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.List;

@Path("/hello")
public class MyResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Jersey Jetty example.";
    }

    @Path("/{name}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public User hello(@PathParam("name") String name) {

        User obj = new User();
        obj.setId(0);
        obj.setlogin(name);
        obj.setpassword("batatas");
        return obj;

    }

    @Path("/all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> helloList() {

        List<User> list = new ArrayList<>();

        User obj1 = new User();
        obj1.setId(1);
        obj1.setlogin("Rui");
        obj1.setpassword("1234");
        list.add(obj1);

        User obj2 = new User();
        obj2.setId(2);
        obj2.setlogin("kenny");
        obj2.setpassword("4321");
        list.add(obj2);

        return list;

    }

}