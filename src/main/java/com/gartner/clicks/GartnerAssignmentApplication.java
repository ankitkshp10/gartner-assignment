package com.gartner.clicks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.gartner.clicks.solution.ClicksSolution;

@SpringBootApplication
public class GartnerAssignmentApplication {
	public static void main(String[] args) {
		SpringApplication.run(GartnerAssignmentApplication.class, args);
		ClicksSolution clicksSolution = new ClicksSolution();
		clicksSolution.solution();
	}

}
