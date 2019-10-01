package com.chriniko.likecqrs.sample.infra.resource;

import com.chriniko.likecqrs.sample.error.ProcessingException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class Resources {

    public static String getResourceFileAsString(String fileName) {
        try (InputStream is = Resources.class.getClassLoader().getResourceAsStream(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            return reader.lines().collect(Collectors.joining(System.lineSeparator()));

        } catch (IOException e) {
            throw new ProcessingException(e);
        }
    }
}
