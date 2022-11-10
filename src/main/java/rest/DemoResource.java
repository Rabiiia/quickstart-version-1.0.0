package rest;

import com.google.gson.Gson;
import dtos.ChuckDTO;
import dtos.DadJokeDTO;
import entities.User;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import security.UserPrincipal;

import externalAPIHandling.JokeFetcher;
import utils.EMF_Creator;

/**
 * @author lam@cphbusiness.dk
 */
@Path("info")
public class DemoResource {
    
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private final Gson gson = new Gson();
    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getInfoForAll() {
        return "{\"msg\":\"Hello anonymous\"}";
    }

    //Just to verify if the database is setup
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("all")
    public String allUsers() {

        EntityManager em = EMF.createEntityManager();
        try {
            TypedQuery<User> query = em.createQuery ("select u from User u",entities.User.class);
            List<User> users = query.getResultList();
            return "[" + users.size() + "]";
        } finally {
            em.close();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("user")
    @RolesAllowed("user")
    public String getFromUser() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to User: " + thisuser + "\"}";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("admin")
    @RolesAllowed("admin")
    public String getFromAdmin() {
        String thisuser = securityContext.getUserPrincipal().getName();
        String thisrole = "admin";
        //if (securityContext.isUserInRole("admin")) thisrole = "admin";
        //else if (securityContext.isUserInRole("user")) thisrole = "user";

        return "{\"username\": \"" + thisuser + "\",\"role\":\"" + thisrole + "\"}";
    }

    @GET
    @Path("externalAPI/jokes")
    @Produces(MediaType.APPLICATION_JSON)
    public String getJokes() throws ExecutionException, InterruptedException {

        List<Object> futureList= new ArrayList();
        JokeFetcher jokeFetcher = new JokeFetcher();

        futureList.add(jokeFetcher.getFutureJoke("https://api.chucknorris.io/jokes/random", ChuckDTO.class).get());
        futureList.add(jokeFetcher.getFutureJoke("https://icanhazdadjoke.com", DadJokeDTO.class).get());

        return gson.toJson(futureList);
    }

}