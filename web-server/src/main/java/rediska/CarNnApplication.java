package rediska;

import competition.Competition;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.scheduling.annotation.EnableScheduling;
import tensor.HelloTensorFlow;


import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication
@EnableMBeanExport
@EnableScheduling
@ComponentScan(basePackageClasses = {Competition.class, CarNnApplication.class})
public class CarNnApplication {
//
    public static void main(String[] args) {
        try {
            HelloTensorFlow.main(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        run(CarNnApplication.class, args);
    }
}
