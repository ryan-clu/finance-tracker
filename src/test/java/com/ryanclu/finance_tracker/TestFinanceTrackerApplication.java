package com.ryanclu.finance_tracker;

import org.springframework.boot.SpringApplication;

public class TestFinanceTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.from(FinanceTrackerApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
