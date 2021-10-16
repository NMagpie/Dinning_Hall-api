package com.example.dinninghallapi;

import com.example.dinninghallapi.http.RequestController;
import com.example.dinninghallapi.order.OrderGeneration;
import com.example.dinninghallapi.tables.Table;
import com.example.dinninghallapi.waiter.Waiter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@SpringBootApplication
public class DinningHallApiApplication {

	private static TimeUnit timeUnit;

	private static TimeUnit restTime;

	public static float rating = 0;

	public static int rates = 0;

	private static String URL;

	private static int tablesNumber;

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(DinningHallApiApplication.class, args);

		initialization();

		ReentrantLock[] locks = new ReentrantLock[tablesNumber];

		Table[] tables = new Table[tablesNumber];

		for (int i = 0; i < tables.length; i++)
		{
			locks[i] = new ReentrantLock();
			tables[i] = new Table();
		}

		OrderGeneration orderGeneration = new OrderGeneration(tables);

		new Thread(orderGeneration).start();

		ArrayList<Waiter> waiters = new ArrayList<>();

		for (int i = 0; i < tablesNumber / 1.5; i++)
		{
			waiters.add(new Waiter(tables, locks));
			new Thread(waiters.get(i)).start();
		}

		RequestController.setWaiters(waiters);

	}

	public static TimeUnit getTimeUnit() {
		return timeUnit;
	}

	private static void initialization() throws InterruptedException {
		File config = new File("configDH.txt");

		Scanner scanner = null;

		try {
			scanner = new Scanner(config);
		} catch (FileNotFoundException e) {
			System.out.println("\"configDH.txt\" file have to be in the same directory as jar file or project");
			TimeUnit.SECONDS.sleep(10);
			System.exit(1);
		}

		String tUnit;

		if (scanner.hasNextLine()) {tUnit = scanner.nextLine(); try {
			timeUnit = TimeUnit.valueOf(tUnit);
		} catch (IllegalArgumentException e) { parsingError(); }
		} else { parsingError(); }

		if (scanner.hasNextLine()) URL = scanner.nextLine(); else { parsingError(); }
		if (!URL.matches("(https?\\:\\/\\/\\w+\\:\\d{4})|((\\d{1,3}\\.){3}(\\d{1,3})(\\/\\d+)?)")) parsingError();

		if (scanner.hasNextLine()) try { tablesNumber= scanner.nextInt(); }
		catch (InputMismatchException e) { parsingError(); }
		else { parsingError(); }
		if (tablesNumber < 1) parsingError();

		scanner.close();

		restTime = TimeUnit.values()[timeUnit.ordinal()-1];


		if (timeUnit.ordinal() == 0) {
			System.out.println("Wrong timeUnit input. Exiting program...");
			TimeUnit.SECONDS.sleep(10);
			System.exit(1);
		}

		if (timeUnit.ordinal() < 3) {
			System.out.println("!WARNING! The app supports timeUnits less, than seconds," +
					" but some POST requests can be sent for more, than 200 ms, so the" +
					" rating of the restaurant can be lowered to 0*!");
			TimeUnit.SECONDS.sleep(10);
		}
	}

	private static void parsingError() {
		System.out.println("Wrong data in config-file! Config file has to contain by lines:" +
				"1. Time units by capslock (e.g. MILLISECONDS, SECONDS, MICROSECONDS)" +
				"2. IPv4 address or URL of Kitchen and its port (e.g. http://localhost:8081)" +
				"3. number of Tables in DinningHall (integer)");
		try {
			TimeUnit.SECONDS.sleep(20);
		} catch (InterruptedException e) {
		}
		System.exit(1);
	}

	public static TimeUnit getRestTime() {
		return restTime;
	}

	public static String getURL() {
		return URL;
	}
}
