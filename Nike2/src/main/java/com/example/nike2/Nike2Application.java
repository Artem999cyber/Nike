package com.example.nike2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Nike2Application {public class Demo {
    private String name;
    private int number;

    public Demo(String name, int number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
    @RestController
    @RequestMapping("/")
    public class DemoController {

        @GetMapping("/hello")
        public String sayHello(@RequestParam String str){
            return str;
        }

        @PostMapping
        public Demo getDemo(@RequestBody Demo demo){
            return demo;
        }
    }
    public static void main(String[] args) {
        SpringApplication.run(Nike2Application.class, args);
    }

}
