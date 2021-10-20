package com.example.dinninghallapi;

import com.example.dinninghallapi.http.RequestController;
import com.example.dinninghallapi.order.OrderGeneration;
import com.example.dinninghallapi.tables.Table;
import com.example.dinninghallapi.waiter.Waiter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@SpringBootApplication
public class DinningHallApiApplication {

	private static final String os = System.getProperty("os.name");

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

		ArrayList<Waiter> waiters = new ArrayList<>();

		for (int i = 0; i < tablesNumber / 2; i++)
		{
			waiters.add(new Waiter(tables, locks));
			new Thread(waiters.get(i)).start();
		}

		RequestController.setWaiters(waiters);

		sendTestRequest();

		OrderGeneration orderGeneration = new OrderGeneration(tables);

		new Thread(orderGeneration).start();

/*		Scanner scanner = new Scanner(System.in);

		String kek = "";

		while (!kek.equals("q")) {
			kek = scanner.nextLine();
		}

		scanner.close();

		System.out.println("Exiting program...");

		System.exit(0);*/

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
		} catch (IllegalArgumentException e) { parsingError(1); }
		} else { parsingError(0); }

		if (scanner.hasNextLine()) URL = scanner.nextLine(); else { parsingError(0); }
		if (!URL.matches("((https?\\:\\/\\/[\\w-]+)|(((https?\\:\\/\\/)?\\d{1,3}\\.){3}(\\d{1,3})(\\/\\d+)?))\\:\\d{4}")) parsingError(2);

		if (scanner.hasNextLine()) try { tablesNumber= scanner.nextInt(); }
		catch (InputMismatchException e) { parsingError(3); }
		else { parsingError(0); }
		if (tablesNumber < 1) parsingError(3);

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

	private static void parsingError(int intCase) {
		System.out.println("Wrong data in config-file! Config file has to contain by lines:" +
				"\n1. Time units by capslock (e.g. MILLISECONDS, SECONDS, MICROSECONDS)" +
				"\n2. IPv4 address or URL of Kitchen and its port (e.g. http://localhost:8081)" +
				"\n3. number of Tables in DinningHall (integer)");
		switch (intCase) {
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

	private static void sendTestRequest() throws InterruptedException {

		final String body = "{\n" +
				"\"order_id\": -1,\n" +
				"\"table_id\": 0,\n" +
				"\"waiter_id\": 1,\n" +
				"\"items\": [ 1 ],\n" +
				"\"priority\": 3,\n" +
				"\"max_wait\": 45,\n" +
				"\"pick_up_time\": 3\n" +
				"}\n";

		RestTemplate restTemplate = new RestTemplateBuilder().build();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(body,headers);
		restTemplate.postForObject(getURL()+"/order",request,String.class);

		TimeUnit.SECONDS.sleep(5);
	}
}