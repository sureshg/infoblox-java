package com.oneops.infoblox;

/**
 * Main class
 *
 * @author Suresh G
 */
public class Main {

  public static void main(String[] args) throws Exception {

    InfobloxClient client = InfobloxClient.builder()
        .endPoint("localhost:8888"/*"infoblox-api.walmart.com"*/)
        .userName("")
        .password("")
        .timeout(5)
        .build();

    client.getAuthZones().forEach(System.out::println);
    System.out.println("--------------");
    client.getAuthZones("xxx.com").forEach(System.out::println);
  }
}
