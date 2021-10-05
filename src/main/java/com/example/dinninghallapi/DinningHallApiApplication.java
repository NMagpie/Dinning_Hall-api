package com.example.dinninghallapi;

import Order.OrderGeneration;
import Tables.Table;
import Waiter.Waiter;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@SpringBootApplication
public class DinningHallApiApplication {

	public static final TimeUnit timeUnit = TimeUnit.SECONDS;

	public static void main(String[] args) throws InterruptedException {
		//SpringApplication.run(DinningHallApiApplication.class, args);

		//Scanner scanner = new Scanner(System.in);

		//System.out.println("Input number of tables");

		//Table[] tables = new Table[scanner.nextInt()];

		ReentrantLock[] locks = new ReentrantLock[10];

		Table[] tables = new Table[10];

		for (int i = 0; i < tables.length; i++)
		{
			locks[i] = new ReentrantLock();
			tables[i] = new Table();
		}

		OrderGeneration orderGeneration = new OrderGeneration(tables);

		new Thread(orderGeneration).start();

		//scanner.close();

		ArrayList<Waiter> waiters = new ArrayList<>();
		for (int i = 0; i < tables.length-2; i++)
		{
			waiters.add(new Waiter(tables, locks));
			new Thread(waiters.get(i)).start();
		}

		while (true)
			timeUnit.sleep((long)0.5);

	}

}
