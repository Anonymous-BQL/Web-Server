package io.github.bianql.test;

import java.util.Arrays;

public class ClientTest {
    public static void main(String[] args) {
        String a = "a b   s";
        Arrays.asList(a.split("\\s+")).forEach(System.out::println);
    }
}
