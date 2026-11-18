package com.logic.springjwt;

import org.springframework.boot.Banner;
import org.springframework.core.env.Environment;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ProfileBasedBanner implements Banner {

    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
        String[] profiles = environment.getActiveProfiles();
        String profile = profiles.length > 0 ? profiles[0] : "default";
        String bannerFile = "banner-" + profile + ".txt";

        try {
            String banner = Files.readString(Paths.get("src/main/resources/" + bannerFile));
            out.println(banner);
        } catch (Exception e) {
            out.println("Default Spring Boot Application");
        }
    }
}