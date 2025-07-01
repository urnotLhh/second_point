package org.graduate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    @Autowired
    public CuckooFilterManager cuckooFilterManager;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        CuckooFilterManager cuckooFilterManager1 = new CuckooFilterManager();
        try {
            cuckooFilterManager1.createFilterIfNotExists("filter name", 10);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
