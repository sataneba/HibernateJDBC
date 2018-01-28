import java.util.HashSet;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import Configuration.SpringConfig;
import Configuration.HibernateConfig;
import Hibernate.House;
import Hibernate.HouseService;
import JDBC.User;
import JDBC.UserDAO;
import JDBC.UserService;

public class Main {
    public static void main(String[] args) {
        ApplicationContext applicationContext = createApplicationContext();
        HouseService houseService = applicationContext.getBean(HouseService.class);
        UserService userService = applicationContext.getBean(UserService.class);
        UserDAO userDAO = applicationContext.getBean(UserDAO.class);

        //-------------------------------------------------------------------------------
        //-------------------------------test-user---------------------------------------
        //-------------------------------------------------------------------------------
		Set<User> users = new HashSet<>();
		User user = User.create("Franz", "Kafka", 1000);
		users.add(user);
		user = User.create("Friedrich","Nietzsche",2000);
		users.add(user);
        for(User user_iter:users) {
            userDAO.insert(user_iter);
        }
		
		// save/get
		user = users.iterator().next();
		if (user.equals(userDAO.get(user.getId()).get())) {
			System.out.println("save/get - ok");
		} else {
            System.out.println("save/get - fail");
        }
		
		// getall
		Set<User> usersGet = userDAO.getAll();
        for(User usr: usersGet) {
            System.out.println(usr.toString());
        }
        if (usersGet.equals(users)) {
        	System.out.println("getall - ok");
        } else {
            System.out.println("getall - fail");
        }
		
		// update
        user = users.iterator().next();
        user.setMoney(500);
        userDAO.update(user);
        if (user.equals(userDAO.get(user.getId()).get())) {
        	System.out.println("update - ok");
        } else {
            System.out.println("update - fail");
        }
		
		// delete
        user = users.iterator().next();
        userDAO.delete(user.getId());
        try {
        	userDAO.get(user.getId()).get(); 
            System.out.println("delete - fail");
            // kostyl v1.0
        } catch(Exception e) {
        	System.out.println("delete - ok");
        }
		
        // draw money
        user = User.create("Arnold","Schwarzenegger",1000);
        userService.insert(user);
		userService.drawMoney(user.getId(), 100);
		if (900 == userService.get(user.getId()).get().getMoney()) {
			System.out.println("draw money - ok");
		} else {
            System.out.println("draw money - fail");
        }
        //-----------------------------end-test-user-------------------------------------
		
		
		//-------------------------------------------------------------------------------
        //-------------------------------test-house--------------------------------------
        //-------------------------------------------------------------------------------
		Set<House> houses = new HashSet<>();
		House house = new House("Moscow", 0, 100);
		houses.add(house);
		house = new House("London", 0, 150);
        houses.add(house);
        for(House house_iter:houses) {
            houseService.save(house_iter);
        }
        
        // save/get
        house = houses.iterator().next();
        System.out.println(house.toString());
        System.out.println(houseService.get(house.id()).get());
        if (house.equals(houseService.get(house.id()).get())) {
        	System.out.println("save/get - ok");
        } else {
            System.out.println("save/get - fail");
        }
        
        // getall
        Set<House> setGet;
        setGet = houseService.getAll();
        if (setGet.equals(houses)) {
        	System.out.println("getall - ok");
        } else {
            System.out.println("getall - fail");
        }
        
        // update
        house = houses.iterator().next();
        houseService.setCost(house.id(), 500);
        houseService.update(house);
        System.out.println(houseService.get(house.id()).get());
        if (house.equals(houseService.get(house.id()).get())) {
        	System.out.println("update - ok");
        } else {
            System.out.println("update - fail");
        }
        
        // delete
        house = houses.iterator().next();
        houseService.delete(house.id());
        try {
        	houseService.get(house.id()).get();
            System.out.println("delete - fail");
            // kostyl v1.0
        } catch(Exception e) {
        	System.out.println("delete - ok");
        }
        
        
        //-----------------------------end-test-hous-------------------------------------

        //-------------------------------------------------------------------------------
        //-------------------------------transaction-------------------------------------
        //-------------------------------------------------------------------------------
        // prepare
		user = User.create("Jason", "Statham", 1000);
		houseService.getUserService().insert(user);

        house = new House("Usa", 0, 100);
        houses.add(house);
        houseService.save(house);

        // transaction
        try {
        	houseService.buyHouse(user.getId(), house.id());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // test
        user.setMoney(900);
        house.setOwner(user.getId());
        System.out.println(houseService.get(house.id()).get());
        System.out.println(houseService.getUserService().get(user.getId()).get());

        if (house.equals(houseService.get(house.id()).get())
            && user.equals(houseService.getUserService().get(user.getId()).get())) {
        	System.out.println("transaction - ok");
        } else {
            System.out.println("transaction - fail");
        }
        //-----------------------------end-transaction-----------------------------------
        
		userDAO.clearDatabase();
		houseService.cleanTables();
	}
	
	public static ApplicationContext createApplicationContext() {
		return new AnnotationConfigApplicationContext(SpringConfig.class, HibernateConfig.class);
	}
}