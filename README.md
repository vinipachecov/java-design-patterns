# Java Design Patterns Studies

This code is only for reference for show use cases
where design patterns are useful to solve problems.
All code is from the course: 
[Java Design Patterns & SOLID Design Principles](https://www.udemy.com/course/design-patterns-in-java-concepts-hands-on-projects/)

Summary:
- [SOLID](#SOLID)
    - [Single Responsibility Principle](#SINGLE-RESPONSIBILITY-PRINCIPLE)
    - [Open-Closed Principle](#open-closed-principle)

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
public class UserController {
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
            store.store(user);
            
            return "SUCCESS";
        } 
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

### Open-Closed Principle

"Modules or classes should be open for extension, but closed for modification."

Open for extension: We should be able to extend a existing behavior
Closed for modification: Existing code remains unchanged.

An example of the usage of the open-closed principle would be to identify
a common behavior or set of parameters that are used across multiple classes.

```java
public class PhoneSubscriber {

    private Long subscriberId;

    private String address;

    private Long phoneNumber;

    private int baseRate;

    public double calculateBill() {
        List<CallHistory.Call> sessions = CallHistory.getCurrentCalls(subscriberId);
        long totalDuration = sessions.stream().mapToLong(CallHistory.Call::getDuration).sum();
        return totalDuration*baseRate/100;
    }

    /**
     * @return the subscriberId
     */
    public Long getSubscriberId() {
        return subscriberId;
    }

    /**
     * @param subscriberId the subscriberId to set
     */
    public void setSubscriberId(Long subscriberId) {
        this.subscriberId = subscriberId;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the phoneNumber
     */
    public Long getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @param phoneNumber the phoneNumber to set
     */
    public void setPhoneNumber(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return the baseRate
     */
    public int getBaseRate() {
        return baseRate;
    }

    /**
     * @param baseRate the baseRate to set
     */
    public void setBaseRate(int baseRate) {
        this.baseRate = baseRate;
    }

}
``` 

And

```java
package Solid.OpenClosed;

import java.util.List;

public class PhoneSubscriber {

    private Long subscriberId;

    private String address;

    private Long phoneNumber;

    private int baseRate;

    public double calculateBill() {
        List<CallHistory.Call> sessions = CallHistory.getCurrentCalls(subscriberId);
        long totalDuration = sessions.stream().mapToLong(CallHistory.Call::getDuration).sum();
        return totalDuration*baseRate/100;
    }

    /**
     * @return the subscriberId
     */
    public Long getSubscriberId() {
        return subscriberId;
    }

    /**
     * @param subscriberId the subscriberId to set
     */
    public void setSubscriberId(Long subscriberId) {
        this.subscriberId = subscriberId;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the phoneNumber
     */
    public Long getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @param phoneNumber the phoneNumber to set
     */
    public void setPhoneNumber(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return the baseRate
     */
    public int getBaseRate() {
        return baseRate;
    }

    /**
     * @param baseRate the baseRate to set
     */
    public void setBaseRate(int baseRate) {
        this.baseRate = baseRate;
    }

}
```

Both classes have params and a method in common.
This could be simplified using the Open-Closed principle
moving what is shared among them into a abstract class(closed for modification):

```java
package Solid.OpenClosed;

/* Base class - closed for modification */
public abstract class Subscriber {
    protected Long subscriberId;

    protected String address;

    protected Long phoneNumber;

    protected int baseRate;

    public abstract double calculateBill(); /* open for extension */

    public int getBaseRate() {
        return baseRate;
    }

    public void setBaseRate(int baseRate) {
        this.baseRate = baseRate;
    }

    public Long getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(Long subscriberId) {
        this.subscriberId = subscriberId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
```

Now that we have created a base class, the inheritance will help us 
reducing the amount of code in the other classes while also leaving the behavior
open for any other use-case:


ISP Class
````java
package Solid.OpenClosed;

import java.util.List;

public class ISPSubscriber extends Subscriber {

    private long freeUsage;

    public ISPSubscriber() {

    }

    public double calculateBill() {
        List<InternetSessionHistory.InternetSession> sessions = InternetSessionHistory.getCurrentSessions(subscriberId);
        long totalData = sessions.stream().mapToLong(InternetSessionHistory.InternetSession::getDataUsed).sum();
        long chargeableData = totalData - freeUsage;
        return chargeableData*baseRate/100;
    }

    /**
     * @return the freeUsage
     */
    public long getFreeUsage() {
        return freeUsage;
    }

    /**
     * @param freeUsage the freeUsage to set
     */
    public void setFreeUsage(long freeUsage) {
        this.freeUsage = freeUsage;
    }

    
}
````

```java
package Solid.OpenClosed;

import java.util.List;

public class PhoneSubscriber extends Subscriber{

    public double calculateBill() {
        List<CallHistory.Call> sessions = CallHistory.getCurrentCalls(subscriberId);
        long totalDuration = sessions.stream().mapToLong(CallHistory.Call::getDuration).sum();
        return totalDuration*baseRate/100;
    }

}
```