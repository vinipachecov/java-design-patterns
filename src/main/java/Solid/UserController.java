package Solid;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

//Handles incoming JSON requests that work on User
public class UserController {

    private Store store = new Store();

    private UserPersistenceService persistenceService = new UserPersistenceService();

    //Create a new user - Controller method should only receive a request
    public String createUser(String userJson) throws IOException {
        /* Parsing Logic */
        ObjectMapper mapper = new ObjectMapper();
        User user = mapper.readValue(userJson, User.class);

        /* Breaking Single Responsibility Principle
        * Should be handled by another class*/
/*        if(!isValidUser(user)) {
            return "Error";
        }*/

        /* Validation Logic */
        UserValidator validator = new UserValidator();
        boolean valid = validator.validateUser(user);

        if(!valid) {
            return "ERROR";
        }

//      Another violation of the SRP - class is now handling persistence logic
        /* store.store(user) */

        persistenceService.saveUser(user);

        return "SUCCESS";
    }

}