package com.pizzaburguer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

// O "(exclude = ...)" desativa a tela de login temporariamente para testes
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class PizzaburguerBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(PizzaburguerBackendApplication.class, args);
    }
}



// package com.pizzaburguer;

// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication
// public class PizzaburguerApplication {
//     public static void main(String[] args) {
//         SpringApplication.run(PizzaburguerApplication.class, args);
//     }
// }
