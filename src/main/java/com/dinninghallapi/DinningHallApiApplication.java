package com.dinninghallapi;

import com.dinninghallapi.http.RequestController;
import com.dinninghallapi.order.OrderGeneration;
import com.dinninghallapi.tables.Table;
import com.dinninghallapi.waiter.Waiter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class DinningHallApiApplication {

    private static float rating = 0;
    private static int rates = 0;
    private static TimeUnit timeUnit;
    private static TimeUnit restTime;
    private static String URL;

    private static int tablesNumber;

    public static void main(String[] args) throws InterruptedException {

        SpringApplication.run(DinningHallApiApplication.class, args);

        initialization();

        Table[] tables = new Table[tablesNumber];

        for (int i = 0; i < tables.length; i++) {
            tables[i] = new Table();
        }

        ArrayList<Waiter> waiters = new ArrayList<>();

        for (int i = 0; i < tablesNumber / 2; i++) {
            waiters.add(new Waiter(tables));
            new Thread(waiters.get(i)).start();
        }

        RequestController.setWaiters(waiters);

        OrderGeneration orderGeneration = new OrderGeneration(tables);

        new Thread(orderGeneration).start();

    }

    public static synchronized float addRating(int mark) {
        rates++;
        rating += mark;
        return rating / rates;
    }

    public static TimeUnit getTimeUnit() {
        return timeUnit;
    }

    private static void initialization() throws InterruptedException {
        File config = new File("configDH.txt");

        try {

            String str;

            Scanner scanner = new Scanner(config);

            str = scanner.nextLine();

            timeUnit = TimeUnit.valueOf(str);

            URL = scanner.nextLine();

            if (!URL.matches("((https?://[\\w-]+)|(((https?://)?\\d{1,3}\\.){3}(\\d{1,3})(/\\d+)?)):\\d{4}"))
                parsingError(2);

            tablesNumber = scanner.nextInt();

            if (tablesNumber < 1) parsingError(3);

            restTime = TimeUnit.values()[timeUnit.ordinal() - 1];

            scanner.close();

        } catch (InputMismatchException e) {
            parsingError(3);
        } catch (NoSuchElementException e) {
            parsingError(0);
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            parsingError(1);
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

    private static void parsingError(int intCase) throws InterruptedException {
        System.out.println("Wrong data in config-file! Config file has to contain by lines:" +
                "\n1. Time units by capslock (e.g. MILLISECONDS, SECONDS, MICROSECONDS)" +
                "\n2. IPv4 address or URL of Kitchen and its port (e.g. http://localhost:8081)" +
                "\n3. number of Tables in DinningHall (integer)");
        switch (intCase) {
            case -1:
                System.out.println("\"configDH.txt\" file have to be in the same directory as jar file or project");
                TimeUnit.SECONDS.sleep(10);
                System.exit(1);
            case 0:
                System.out.println("ERROR: WRONG NUMBER OF LINES");
                break;
            case 1:
                System.out.println("ERROR IN LINE 1: TIMEUNITS");
                break;
            case 2:
                System.out.println("ERROR IN LINE 2: ADDRESS OR IP");
                break;
            case 3:
                System.out.println("ERROR IN LINE 3: NUMBER OF TABLES");
                break;
        }

        TimeUnit.SECONDS.sleep(20);
        System.exit(1);
    }

    public static TimeUnit getRestTime() {
        return restTime;
    }

    public static String getURL() {
        return URL;
    }

}