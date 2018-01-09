package com.oneops.infoblox;

/**
 * Main class
 *
 * @author Suresh G
 */
public class Main {

  public static void main(String[] args) throws Exception {

    InfobloxClient client = InfobloxClient.builder()
        .endPoint("localhost:8888")
        .userName("")
        .password("")
        .build();

    client.getAuthZones().forEach(System.out::println);
    System.out.println("--------------");
    client.getAuthZones("domain.com").forEach(System.out::println);
  }
}
