package com.ngeneration;

import com.ngeneration.graphic.engine.drawers.Console2DDrawer;
import com.ngeneration.graphic.engine.drawers.Drawer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.Scanner;

@SpringBootApplication
public class SpringStarterExample {

    @Autowired
    private String secret;

    public static void main(String[] args) {
//        SpringApplication.run(SpringStarterExample.class, args);
        new Simulation().start();
    }

    @Bean
    public String secret() {
        return "you are beautiful!";
    }


    //    @Bean
    public CommandLineRunner allBeans(ApplicationContext ctx) {
        return args -> {

            System.out.println("Let's inspect the beans provided by Spring Boot:");

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                System.out.println("\tbean: " + beanName);
            }

        };
    }

    //    @Bean
    public CommandLineRunner findBeans(ApplicationContext ctx) {
        return args -> {
            System.out.println("Start finding bean. . .");
            if (args.length == 0) {
                System.out.print("\tPlease, type bean name regex: ");
                Scanner scanner = new Scanner(System.in);
                String beanNameRegex = scanner.nextLine();
                args = new String[]{beanNameRegex};
            }
            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            boolean anyBeanFound = false;
            for (String beanName : beanNames) {
                if (isMatch(beanName, args)) {
                    anyBeanFound = true;
                    System.out.println("\tbean: " + beanName);
                }
            }
            if (!anyBeanFound) {
                System.err.println("\tSorry, no bean with such name was found :(");
            }

        };
    }

    private boolean isMatch(String sample, String... regex) {
        if (sample == null || sample.isEmpty()) {
            return false;
        }
        for (String s : regex) {
            if (sample.matches("\\w*" + s + "\\w*")) {
                return true;
            }
        }
        return false;
    }
}
