package sn.setter

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.http.converter.FormHttpMessageConverter

@SpringBootApplication
@EnableJpaAuditing
class SetterApplication {

    static void main(String[] args) {
        SpringApplication.run(SetterApplication, args)
    }
    /*@Bean
    public FormHttpMessageConverter formHttpMessageConverter() {
        return new FormHttpMessageConverter();
    }*/

}
