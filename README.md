# Java Design Patterns Studies

This code is only for reference for show use cases
where design patterns are useful to solve problems.
All code is from the course: 
[Java Design Patterns & SOLID Design Principles](https://www.udemy.com/course/design-patterns-in-java-concepts-hands-on-projects/)

Summary:
- [SOLID](#SOLID)
    - [Single Responsibility Principle](#SINGLE-RESPONSIBILITY-PRINCIPLE)
    - 

## SOLID

SOLID is an acronym for the following principles:

- Single Responsibility Principle
- Open Closed Principle
- Liskov substitution Principle
- Interface Segregation Principle
- Dependency Inversion Principle

### Single Responsibility Principle

As the same suggest, a class should only be focused on handling
a specific task. In other words:

<i>A Class should have only one reason to change.</i>

Example:
- A class handling a request should not be changed if the validation changes.
- A class handling a request should not be  changed if the persistence logic changes.

Code 
```java
public String createUser(String userJson) throws IOException {
        /* Parsing Logic */`
        ObjectMapper mapper = new ObjectMapper();
        User user = mapper.readValue(userJson, User.class);

        /* Breaking Single Responsibility Principle
        * Should be handled by another class*/
        if(!isValidUser(user)) {
            return "Error";
        }
        

//      Another violation of the SRP - class is now handling persistence logic
        store.store(user)        
        
        return "SUCCESS";
    } 
```

Problems addressed by this class:
- parsing
- object validation
- persistence logic

A more SOLID code would be: 

```java
//Handles incoming JSON requests that work on User
public class UserController {

    private Store store = new Store();

    private UserPersistenceService persistenceService = new UserPersistenceService();

    //Create a new user - Controller method should only receive a request
    public String createUser(String userJson) throws IOException {
        /* Parsing Logic */
        ObjectMapper mapper = new ObjectMapper();
        User user = mapper.readValue(userJson, User.class);

        /* Validation Logic */
        UserValidator validator = new UserValidator();
        boolean valid = validator.validateUser(user);

        if(!valid) {
            return "ERROR";
        }

        persistenceService.saveUser(user);

        return "SUCCESS";
    }

}
```

Problems Addressed:
- Validation logic handled outside of the controller
- Persistence logic handled outside of the controller

Another thing that could be done would be to have a separate class to do parsing.