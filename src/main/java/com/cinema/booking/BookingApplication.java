package com.cinema.booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BookingApplication {

    public static void main(String[] args) {
        // DEBUG TẠM THỜI - sẽ xóa sau khi tìm ra nguyên nhân
        System.out.println("=== DEBUG ENV VARS ===");
        System.out.println("DB_URL = [" + System.getenv("DB_URL") + "]");
        System.out.println("DB_USERNAME = [" + System.getenv("DB_USERNAME") + "]");
        System.out.println("DB_PASSWORD length = " + (System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD").length() : "NULL"));
        System.out.println("REDIS_HOST = [" + System.getenv("REDIS_HOST") + "]");
        System.out.println("=== END DEBUG ===");

        SpringApplication.run(BookingApplication.class, args);
    }

}