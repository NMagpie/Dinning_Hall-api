package com.example.dinninghallapi;

import com.example.dinninghallapi.http.RequestController;
import com.example.dinninghallapi.order.OrderGeneration;
import com.example.dinninghallapi.tables.Table;
import com.example.dinninghallapi.waiter.Waiter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@SpringBootApplication
public class DinningHallApiApplication {

	public static final TimeUnit timeUnit = TimeUnit.SECONDS;

	public static float rating = 0;

	public static int rates = 0;

	public static void main(String[] args) {
		SpringApplication.run(DinningHallApiApplication.class, args);

		//Scanner scanner = new Scanner(System.in);
		//System.out.println("Input number of tables");
		//int number = scanner.nextInt();
		//scanner.close();

		int number = 20;

		ReentrantLock[] locks = new ReentrantLock[number];

		Table[] tables = new Table[number];

		for (int i = 0; i < tables.length; i++)
		{
			locks[i] = new ReentrantLock();
			tables[i] = new Table();
		}

		OrderGeneration orderGeneration = new OrderGeneration(tables);

		new Thread(orderGeneration).start();

		ArrayList<Waiter> waiters = new ArrayList<>();

		for (int i = 0; i < number/1.5; i++)
		{
			waiters.add(new Waiter(tables, locks));
			new Thread(waiters.get(i)).start();
		}

		RequestController.setWaiters(waiters);

		//something from http part causes starvation, and this sucks

	}

}
