package com.example.dinninghallapi;

import Tables.Table;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class DinningHallApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(DinningHallApiApplication.class, args);

		//Scanner scanner = new Scanner(System.in);

		//System.out.println("Input number of tables");

		//Table[] tables = new Table[scanner.nextInt()];

		Table[] tables = new Table[10];

		//scanner.close();

		int waiters = tables.length-2;



	}

}
