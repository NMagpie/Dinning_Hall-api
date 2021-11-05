package com.dinninghallapi;

import com.dinninghallapi.foods.Foods;
import com.dinninghallapi.http.RequestController;
import com.dinninghallapi.order.OrderGeneration;
import com.dinninghallapi.order.service.OrderService;
import com.dinninghallapi.tables.Table;
import com.dinninghallapi.waiter.Waiter;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class DinningHallApiApplication {

    private static final SpringApplication app = new SpringApplication(DinningHallApiApplication.class);
    private static final ArrayList<OrderService> orderServices = new ArrayList<>();
    private static Restaurant restaurant;
    private static TimeUnit timeUnit;
    private static TimeUnit restTime;
    private static String URLKitchen;
    private static String URLFoodOrder;
    private static int tablesNumber;

    public static void main(String[] args) throws InterruptedException {

        initialization();

        System.out.println(restaurant.getId() + ". " + restaurant.getName());

        System.out.println(restaurant.getAddress());

        Table[] tables = new Table[tablesNumber];

        for (int i = 0; i < tablesNumber; i++) {
            tables[i] = new Table();
        }

        ArrayList<Waiter> waiters = new ArrayList<>();

        for (int i = 0; i < tablesNumber / 2; i++) {
            waiters.add(new Waiter(tables));
            new Thread(waiters.get(i)).start();
        }

        RequestController.setUrl(URLKitchen);

        RequestController.setOrderServices(orderServices);

        RequestController.setWaiters(waiters);

        if (timeUnit.ordinal() < TimeUnit.SECONDS.ordinal())
            sendTestRequest();

        OrderGeneration orderGeneration = new OrderGeneration(tables);

        new Thread(orderGeneration).start();

        registerRestaurant();

    }

    private static void initialization() throws InterruptedException {

        File config = new File("configDH.txt");

        try {

            String str;

            Scanner scanner = new Scanner(config);

            str = scanner.nextLine();

            if (!str.matches("^\\d+ [\\w']+$"))
                parsingError(1);

            String[] id_name = str.split(" ", 2);

            int id = Integer.parseInt(id_name[0]);

            String name = id_name[1];

            str = scanner.nextLine();

            timeUnit = TimeUnit.valueOf(str);

            restTime = TimeUnit.values()[timeUnit.ordinal() - 1];

            String port = scanner.nextLine();

            if (!port.matches("^\\d{4,5}$"))
                parsingError(3);

            app.setDefaultProperties(Collections.singletonMap("server.port", port));

            app.run();

            URLKitchen = scanner.nextLine();

            if (!URLKitchen.matches("((https?://[\\w-]+)|(((https?://)?\\d{1,3}\\.){3}(\\d{1,3})(/\\d+)?)):\\d{4,5}"))
                parsingError(4);

            URLFoodOrder = scanner.nextLine();

            if (!URLFoodOrder.matches("((https?://[\\w-]+)|(((https?://)?\\d{1,3}\\.){3}(\\d{1,3})(/\\d+)?)):\\d{4,5}"))
                parsingError(5);

            URLFoodOrder += "/register";

            tablesNumber = scanner.nextInt();

            if (tablesNumber < 1) parsingError(6);

            scanner.close();

            parseMenu();

            ArrayList<Foods> menu = Foods.getMenu();

            String address = "localhost:" + port;

            //OR address = Inet4Address.getLocalHost().getHostAddress() + ":" + port;
            //OR address = "http://dinning-hall:" + port;

            restaurant = new Restaurant(id, name, address, menu.size(), menu);

        } catch (InputMismatchException e) {
            parsingError(6);
        } catch (NoSuchElementException e) {
            parsingError(0);
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            parsingError(2);
        } catch (FileNotFoundException e) {
            parsingError(-1);
        }

        if (timeUnit.ordinal() < 3) {
            System.out.println("!WARNING! The app supports timeUnits less, than seconds," +
                    " but some POST requests can be sent for more, than 200 ms, so the" +
                    " rating of the restaurant can be lowered to 0*!");
            TimeUnit.SECONDS.sleep(1);
        }
    }

    private static void parseMenu() throws InterruptedException {
        try {

            ObjectMapper mapper = new ObjectMapper();

            mapper.readValue(Paths.get("menu.json").toFile(), Foods[].class);

        } catch (JsonMappingException | JsonParseException e) {
            parsingError(-3);
        } catch (IOException e) {
            parsingError(-2);
        }
    }

    private static void parsingError(int intCase) throws InterruptedException {
        if (intCase > -1)
            System.out.println("Wrong data in config-file! Config file has to contain by lines:" +
                    "\n1. Id and the name of the restaurant (e.g. 1 McDonald's)" +
                    "\n2. Time units by capslock (e.g. MILLISECONDS, SECONDS, MICROSECONDS)" +
                    "\n3. Free port to be reserved for this server" +
                    "\n4. IPv4 address or URL of Kitchen and its port (e.g. http://localhost:8081)" +
                    "\n5. IPv4 address or URL of Food Ordering service and its port (e.g. http://localhost:8081)" +
                    "\n6. number of Tables in DinningHall (integer)");

        switch (intCase) {
            case -3:
                System.out.println("ERROR: PARSING MENU");
                break;
            case -2:
                System.out.println("\"menu.json\" file have to be in the same directory as jar file or project");
                break;
            case -1:
                System.out.println("\"configDH.txt\" file have to be in the same directory as jar file or project");
                break;
            case 0:
                System.out.println("ERROR: WRONG NUMBER OF LINES");
                break;
            case 1:
                System.out.println("ERROR IN LINE 1: Id and name of restaurant");
                break;
            case 2:
                System.out.println("ERROR IN LINE 2: TIMEUNITS");
                break;
            case 3:
                System.out.println("ERROR IN LINE 3: Port of this server");
                break;
            case 4:
            case 5:
                System.out.println("ERROR IN LINE " + intCase + ": ADDRESS OR IP");
                break;
            case 6:
                System.out.println("ERROR IN LINE 6: NUMBER OF TABLES");
                break;
        }

        TimeUnit.SECONDS.sleep(10);
        System.exit(1);
    }

    private static void sendTestRequest() throws InterruptedException {

        final String body = "{\n" +
                "\"order_id\": -1,\n" +
                "\"table_id\": 0,\n" +
                "\"waiter_id\": 1,\n" +
                "\"items\": [ 2 ],\n" +
                "\"priority\": 3,\n" +
                "\"max_wait\": 45,\n" +
                "\"pick_up_time\": 3\n" +
                "}\n";

        RestTemplate restTemplate = new RestTemplateBuilder().build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForObject(URLKitchen + "/order", request, String.class);
        } catch (RestClientException e) {
            noResponse();
        }

        TimeUnit.SECONDS.sleep(3);
    }

    private static void registerRestaurant() {

/*        int ordersNumber = 1;

        if (timeUnit.ordinal() < TimeUnit.SECONDS.ordinal()) ordersNumber = 501;
        while (Order.getCount() < ordersNumber) {
            restTime.sleep(10000);
        }*/

        RestTemplate restTemplate = new RestTemplateBuilder().build();

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            ObjectMapper mapper = new ObjectMapper();

            String json = mapper.writeValueAsString(restaurant);

            HttpEntity<String> request = new HttpEntity<>(json, headers);

            restTemplate.postForObject(URLFoodOrder, request, String.class);

        } catch (JsonProcessingException e) {
            System.out.println("Error processing the JSON file!");
        } catch (HttpClientErrorException | ResourceAccessException e) {
            System.out.println("There is no Food Ordering Service!");
        }

    }

    private static void noResponse() {
        System.out.println("No connection; exiting program...");
        System.exit(1);
    }

    public static TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public static TimeUnit getRestTime() {
        return restTime;
    }

    public static String getURLKitchen() {
        return URLKitchen;
    }

    public static Restaurant getRestaurant() {
        return restaurant;
    }
}