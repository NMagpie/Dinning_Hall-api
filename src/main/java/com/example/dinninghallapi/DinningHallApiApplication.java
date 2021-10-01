package com.example.dinninghallapi;

import Order.Order;
import Tables.Table;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class DinningHallApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(DinningHallApiApplication.class, args);

		Scanner scanner = new Scanner(System.in);

		System.out.println("Input number of tables");
		
		Table[] tables = new Table[scanner.nextInt()];

		scanner.close();

		int waiters = tables.length-2;



	}

}
