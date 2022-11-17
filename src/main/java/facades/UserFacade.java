package facades;

import com.google.gson.JsonElement;
import entities.Role;
import entities.User;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

import errorhandling.API_Exception;
import org.mindrot.jbcrypt.BCrypt;
import security.errorhandling.AuthenticationException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lam@cphbusiness.dk
 */
public class UserFacade {

    private static EntityManagerFactory emf;
    private static UserFacade instance;

    private UserFacade() {
    }

    /**
     *
     * @param _emf
     * @return the instance of this facade.
     */
    public static UserFacade getUserFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new UserFacade();
        }
        return instance;
    }

    public User getVeryfiedUser(String username, String password) throws AuthenticationException {
        EntityManager em = emf.createEntityManager();
        User user;
        try {
            user = em.find(User.class, username);
            System.out.println(user);
            //System.out.println("Here in getVeryfiedUser " + user.getUserName() + " " + user.getUserPass());
            //System.out.println("Here password in getVeryfiedUser " + password + " " + user.verifyPassword(password));
            if (user == null || !user.verifyPassword(password)) {
                throw new AuthenticationException("Invalid user name or password");
            }
        } finally {
            em.close();
        }
        return user;
    }

    // added this method because so that we can get new token everytime reloading the page in front end
    // we want this because we want to be stayed logged when we are active
    // however you will be logged out after 30 minutes if you are not active
    public User getUser(String username) throws AuthenticationException {
        EntityManager em = emf.createEntityManager();
        User user;
        try {
            user = em.find(User.class, username);
            if (user == null /*|| !user.verifyPassword(password)*/) {
                throw new AuthenticationException("Faulty token");
            }
        } finally {
            em.close();
        }
        return user;
    }


    // Det er rart at returnere et User objekt i stedet for UserDTO, fordi vi i fremtiden kan det være at vi laver
    // addRole metode. så kalder jeg createUser metoden så kan jeg lige efter bruge addRole metoden som også
    // retunere et User entitiy objekt.
    // når man så er færdig med at samlet User objekt, så kan jeg lave det om til UserDTO.
    // LIGE NU bruger vi createUser metoden som returnerer et entity objekt, men det bliver lavet om til userDTO i Userresource i rest
    public User createUser(String username, String password) throws API_Exception {


        // Construct user:
        User user = new User(username, password);

        // Persist user to database:
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(user);
            Role role = em.find(Role.class, "user");
            if (role == null) {
                role = new Role("user");
                em.persist(role);
            }
            user.addRole(role);
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            throw new API_Exception("Could not create user", 500, e);
        } finally {
            em.close();
        }

        return user;
    }

    public static void main(String[] args) throws API_Exception, AuthenticationException {
        System.out.println(BCrypt.checkpw("test123", "$2a$10$QeEwAmgZAh2ALPWobjqsVeMlGCsPIRUFgW8BvLoDAwjqYNFZarh2C"));


        //User user = new User("test_user2", "1234");
        //user.verifyPassword("123");

//        EntityManagerFactory emf = utils.EMF_Creator.createEntityManagerFactory();
//        UserFacade userFacade = UserFacade.getUserFacade(emf);
//        System.out.println(  userFacade.getVeryfiedUser("NyUser", "test123"));

//        List<String> roles = new ArrayList<>();
//        roles.add("admin");
//        roles.add("user");
//        System.out.println(userFacade.createUser("test_user2", "1234", roles));
    }
}
