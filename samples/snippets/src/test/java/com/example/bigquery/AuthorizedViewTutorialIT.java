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

public class AuthorizedViewTutorialIT {

  private String sourceDatasetId;
  private String sourceTableId;
  private String sharedDatasetId;
  private String sharedViewId;
  private ByteArrayOutputStream bout;
  private PrintStream out;

  private static final String PROJECT_ID = requireEnvVar("GOOGLE_CLOUD_PROJECT");

  private static String requireEnvVar(String varName) {
    String value = System.getenv(varName);
    assertNotNull(
        "Environment variable " + varName + " is required to perform these tests.",
        System.getenv(varName));
    return value;
  }

  @BeforeClass
  public static void checkRequirements() {
    requireEnvVar("GOOGLE_CLOUD_PROJECT");
  }

  @Before
  public void setUp() {
    sourceDatasetId = "SOURCE_DATASET_TEST_" + UUID.randomUUID().toString().substring(0, 8);
    sourceTableId = "SOURCE_TABLE_TEST_" + UUID.randomUUID().toString().substring(0, 8);
    sharedDatasetId = "SHARED_DATASET_TEST_" + UUID.randomUUID().toString().substring(0, 8);
    sharedViewId = "SHARED_VIEW_TEST_" + UUID.randomUUID().toString().substring(0, 8);

    bout = new ByteArrayOutputStream();
    out = new PrintStream(bout);
    System.setOut(out);
  }

  @After
  public void tearDown() {
    // Clean up
    DeleteDataset.deleteDataset(PROJECT_ID, sourceDatasetId);
    DeleteDataset.deleteDataset(PROJECT_ID, sharedDatasetId);
    System.setOut(null);
  }

  @Test
  public void testAuthorizedViewTutorial() {
    AuthorizedViewTutorial.authorizedViewTutorial(
        PROJECT_ID, sourceDatasetId, sourceTableId, sharedDatasetId, sharedViewId);
    assertThat(bout.toString()).contains("Authorized view tutorial successfully");
  }
}
