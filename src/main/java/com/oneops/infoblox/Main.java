/*******************************************************************************
 *
 *   Copyright 2017 Walmart, Inc.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *******************************************************************************/
package com.oneops.infoblox;

import com.oneops.infoblox.model.SearchType;

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

    //client.getAuthZones().forEach(System.out::println);
    client.getAuthZones("walmart.com",SearchType.NONE).forEach(System.out::println);
    //SearchType.CASE_INSENSITIVE.
  }
}
