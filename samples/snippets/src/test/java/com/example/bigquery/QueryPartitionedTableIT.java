/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.bigquery;

import static com.google.common.truth.Truth.assertThat;
import static junit.framework.TestCase.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class QueryPartitionedTableIT {

  private String tableName;
  private ByteArrayOutputStream bout;
  private PrintStream out;

  private static final String BIGQUERY_DATASET_NAME = requireEnvVar("BIGQUERY_DATASET_NAME");

  private static String requireEnvVar(String varName) {
    String value = System.getenv(varName);
    assertNotNull(
        "Environment variable " + varName + " is required to perform these tests.",
        System.getenv(varName));
    return value;
  }

  @BeforeClass
  public static void checkRequirements() {
    requireEnvVar("BIGQUERY_DATASET_NAME");
  }

  @Before
  public void setUp() throws Exception {
    bout = new ByteArrayOutputStream();
    out = new PrintStream(bout);
    System.setOut(out);

    // Create a test table
    tableName = "LOAD_PARTITIONED_TABLE_TEST_" + UUID.randomUUID().toString().substring(0, 8);
    String sourceUri = "gs://cloud-samples-data/bigquery/us-states/us-states-by-date-no-header.csv";
    LoadPartitionedTable.loadPartitionedTable(BIGQUERY_DATASET_NAME, tableName, sourceUri);

    bout = new ByteArrayOutputStream();
    out = new PrintStream(bout);
    System.setOut(out);
  }

  @After
  public void tearDown() {
    // Clean up
    DeleteTable.deleteTable(BIGQUERY_DATASET_NAME, tableName);
    System.setOut(null);
  }

  @Test
  public void testQueryPartitionedTable() {
    String query =
        String.format(
            "SELECT * FROM `%s.%s` WHERE date BETWEEN @start_date AND @end_date",
            BIGQUERY_DATASET_NAME, tableName);
    QueryPartitionedTable.queryPartitionedTable(query);
    assertThat(bout.toString()).contains("Query partitioned table performed successfully.");
  }
}
