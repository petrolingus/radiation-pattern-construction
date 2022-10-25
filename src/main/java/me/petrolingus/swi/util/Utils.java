package me.petrolingus.swi.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {

    public static String loadShader(String shader) {
        try (Stream<String> stringStream = Files.lines(Paths.get("src/main/resources/shaders/" + shader))) {
            return stringStream.collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
