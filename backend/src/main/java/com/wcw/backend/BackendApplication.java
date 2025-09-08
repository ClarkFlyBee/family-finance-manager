package com.wcw.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {
    public static void main(String[] args) {
        // 🔥 临时添加：打印 JVM 编码信息
        System.out.println("✅ JVM Encoding Check:");
        System.out.println("Default Charset: " + java.nio.charset.Charset.defaultCharset());
        System.out.println("file.encoding: " + System.getProperty("file.encoding"));
        System.out.println("console.encoding: " + System.getProperty("console.encoding"));
        System.out.println("LANG: " + System.getenv("LANG"));
        System.out.println("LC_ALL: " + System.getenv("LC_ALL"));

        SpringApplication.run(BackendApplication.class, args);
    }
}