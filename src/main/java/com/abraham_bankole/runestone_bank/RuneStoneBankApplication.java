package com.abraham_bankole.runestone_bank;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import jdk.jfr.Category;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "RuneStone Bank App",
                description = "Backend REST APIs made with SpringBoot",
                version = "v1.0",
                contact = @Contact(
                        name = "Abraham Bankole",
                        email = "abraham.o.bankole@gmail.com",
                        url = "https://github.com/Semilore317/RuneStone-Bank"
                ),
                license = @License(
                        name = "RuneStone Bank",
                        url = "https://github.com/Semilore317/RuneStone-Bank"
                )
        ),
        externalDocs = @ExternalDocumentation(
                description = "RuneStone Bank",
                url = "https://github.com/Semilore317/RuneStone-Bank"
        )
)
public class RuneStoneBankApplication {

    public static void main(String[] args) {
        SpringApplication.run(RuneStoneBankApplication.class, args);
    }

}
