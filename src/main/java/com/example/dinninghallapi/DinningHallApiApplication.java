package com.example.dinninghallapi;

import Tables.OrderGeneration;
import Tables.Table;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class DinningHallApiApplication {

	public static final TimeUnit timeUnit = TimeUnit.SECONDS;

	public static void main(String[] args) {
		//SpringApplication.run(DinningHallApiApplication.class, args);

		//Scanner scanner = new Scanner(System.in);

		//System.out.println("Input number of tables");

		//Table[] tables = new Table[scanner.nextInt()];

		Table[] tables = new Table[10];
		for (int i=0; i< tables.length; i++)
			tables[i] = new Table();

		OrderGeneration orderGeneration = new OrderGeneration(tables);

		new Thread(orderGeneration).start();

		//scanner.close();

		int waiters = tables.length-2;

	}

}
