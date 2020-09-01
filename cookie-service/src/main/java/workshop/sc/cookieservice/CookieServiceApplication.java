package workshop.sc.cookieservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CookieServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CookieServiceApplication.class, args);
    }

}
