package script.jsoup.fangtianxia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @version: v1.0
 * @date: 2020/10/19
 * @author: dgj
 */
@SpringBootApplication
@EnableAsync
@EnableConfigurationProperties
public class AppMain {

    public static void main(String[] args) {
        SpringApplication.run(AppMain.class, args);
    }

}

