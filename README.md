# Java Design Patterns Studies

This code is only for reference for show use cases
where design patterns are useful to solve problems.
All code is from the course: 
[Java Design Patterns & SOLID Design Principles](https://www.udemy.com/course/design-patterns-in-java-concepts-hands-on-projects/)

Summary:
- [SOLID](#SOLID)
    - [Single Responsibility Principle](#SINGLE-RESPONSIBILITY-PRINCIPLE)
    - [Open-Closed Principle](#open-closed-principle)
    - [Liskov Substitution Principle](#liskov-substitution-principle)
    - [Interface Segregation Principle](#interface-segregation-principle)

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
            /* Parsing Logic */
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

### Liskov Substitution Principle

_We should be to substitute base class objects with child class objects 
and this should not alter behavior/characteristics of program_


If the base class specifies a behavior A and if that class is substituted by
a child class, that behavior should not alter.

For example:

````java
package Solid.LiskovSubstitution;

public class Square extends Rectangle {
	
	public Square(int side) {
		super(side, side);
	}
	
	@Override
	public void setWidth(int width) {
		setSide(width);
	}

	@Override
	public void setHeight(int height) {
		setSide(height);
	}

	public void setSide(int side) {
		super.setWidth(side);
		super.setHeight(side);
	}

}
````

````java
package Solid.LiskovSubstitution;

public class Rectangle {

	private int width;
	
	private int height;

	public Rectangle(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	public int computeArea() {
		return width * height;
	}
}
````

In our main class, our tests will fail in the square use case.
It happens because the Square class, as shown before, overrides the setHeight
and setWidth method which in the end modifies the original behavior of the class.

````java
package Solid.LiskovSubstitution;

public class Main {

	public static void main(String[] args) {
		
		Rectangle rectangle = new Rectangle(10, 20);
		System.out.println(rectangle.computeArea());
		
		Square square = new Square(10);
		System.out.println(square.computeArea());
		
		useRectangle(rectangle);
		
		useRectangle(square);

	}

	private static void useRectangle(Rectangle rectangle) {
		/* Square class will fail:
		* - even a valid square class will in the end fail this tests
		* - modified the behavior of the base class
		* */
		rectangle.setHeight(20);
		rectangle.setWidth(30);
		assert rectangle.getHeight() == 20 : "Height Not equal to 20";
		assert rectangle.getWidth() == 30 : "Width Not equal to 30";
	}
}
````

This problem can be solved by creating a contract which our classes will implement:

````java
package Solid.LiskovSubstitution;

public interface Shape {
    public int computeArea();
}
````

By replacing the Rectangle extension from the Square class, and implementing
the Shape class we will ensure the Liskov Principle doesn't get broken as it was before.
Now a derived class is not changing the behavior of the parent class anymore and a new 
test case for Square shall be written:

````java
package Solid.LiskovSubstitution;

public class Square implements Shape {

	public int side;
	
	public Square(int side) {
		this.side = side;
	}

	public void setSide(int side) {
		this.side = side;
	}

	@Override
	public int computeArea() {
		return this.side * this.side;
	}
}
````

### Interface Segregation Principle

_Clients should not be forced to depend upon interfaces that they do not use_

In other words, clients should not depend on methods that they don't use..

**Interface Polution**

The practice to gather unrelated methods into a single interface, 
making it larger and obscure.

Signs of Interface Polution:
- Classes have empty method implementations
- Method implementations throw UnsupportedOperationException or similar exception
- Method implementations return null or default/dummy values

Interface Segregation makes sure our interfaces/contracts are clean and only have methods related to each other.

Example:

Let's say we have 3 Service implementations and only a single
interface to declare the methods they should implement.

````java
package Solid.InterfaceSegregation.service;

import Solid.InterfaceSegregation.entity.Entity;

import java.util.List;

//common interface to be implemented by all persistence services.
public interface PersistenceService<T extends Entity> {

	public void save(T entity);
	
	public void delete(T entity);
	
	public T findById(Long id);

	public List<T> findByName(String name);
	
}
````

And our two service classes implementing this PersistenceService

````java
package Solid.InterfaceSegregation.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import Solid.InterfaceSegregation.entity.User;

//Stores User entities
public class UserPersistenceService implements PersistenceService<User>{
	
	private static final Map<Long, User> USERS = new HashMap<>();
	
	@Override
	public void save(User entity) {
		synchronized (USERS) {
			USERS.put(entity.getId(), entity);
		}
	}

	@Override
	public void delete(User entity) {
		synchronized (USERS) {
			USERS.remove(entity.getId());
		}
	}

	@Override
	public User findById(Long id) {
		synchronized (USERS) {
			return USERS.get(id);
		}
	}

	@Override
	public List<User> findByName(String name) {
		synchronized (USERS) {
			return USERS.values().stream().filter(u->u.getName().equalsIgnoreCase(name)).collect(Collectors.toList());
		}
	}

}
````

And now our OrderService implementation 

````java
package Solid.InterfaceSegregation.service;

import Solid.InterfaceSegregation.entity.Order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderPersistenceService implements PersistenceService<Order>{
	
	private static final Map<Long, Order> ORDERS = new HashMap<>();
	
	@Override
	public void save(Order entity) {
		synchronized (ORDERS) {
			ORDERS.put(entity.getId(), entity);
		}
	}

	@Override
	public void delete(Order entity) {
		synchronized (ORDERS) {
			ORDERS.remove(entity.getId());
		}
	}

	@Override
	public Order findById(Long id) {
		synchronized (ORDERS) {
			return ORDERS.get(id);
		}
	}

    @Override
	public List<Order> findByName(String name) {
		throw new UnsupportedOperationException("Find by name is not supported");
	}
}
````

**Problem**

- Unrelated methods in a common Interface
- Returning exceptions that show class do not support it 

What happens here is that if the Order Entity doesn't have a name property, the method findByName
will be useless. This method is also returning an unsupported operation exception, which is just one
of signs that it is breaking the interface segregation as well.

**Solution**

- A common way to solve it would be to remove the findByName method from PersistenceService interface in addition
to removing the override from the UserPersistence class.

- A more interesting way to solve this problem would be to break the PersistenceService into separate interfaces for each Service. This approach
will create more methods, but folder and file structure will be clearer to developers in general.