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

// [START bigquery_load_table_gcs_parquet]
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryException;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.FormatOptions;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobInfo;
import com.google.cloud.bigquery.LoadJobConfiguration;
import com.google.cloud.bigquery.TableId;
import java.math.BigInteger;

public class LoadParquet {

  public static void main(String[] args) {
    // TODO(developer): Replace these variables before running the sample.
    String datasetName = "MY_DATASET_NAME";
    String sourceUri = "gs://cloud-samples-data/bigquery/us-states/us-states.parquet";
    String tableName = "us_states";
    loadParquet(datasetName, tableName, sourceUri);
  }

  public static void loadParquet(String datasetName, String tableName, String sourceUri) {
    try {
      // Initialize client that will be used to send requests. This client only needs to be created
      // once, and can be reused for multiple requests.
      BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();

      TableId tableId = TableId.of(datasetName, tableName);

      LoadJobConfiguration configuration =
          LoadJobConfiguration.builder(tableId, sourceUri)
              .setFormatOptions(FormatOptions.parquet())
              .build();

      // For more information on Job see:
      // https://googleapis.dev/java/google-cloud-clients/latest/index.html?com/google/cloud/bigquery/package-summary.html
      // Load the table
      Job job = bigquery.create(JobInfo.of(configuration));

      // Blocks until this load table job completes its execution, either failing or succeeding.
      Job completedJob = job.waitFor();
      if (completedJob == null) {
        System.out.println("Job not executed since it no longer exists.");
        return;
      } else if (completedJob.getStatus().getError() != null) {
        System.out.println(
            "BigQuery was unable to load the table due to an error: \n"
                + job.getStatus().getError());
        return;
      }

      // Check number of rows loaded into the table
      BigInteger numRows = bigquery.getTable(tableId).getNumRows();
      System.out.printf("Loaded %d rows. \n", numRows);

      System.out.println("GCS parquet loaded successfully.");
    } catch (BigQueryException | InterruptedException e) {
      System.out.println("GCS Parquet was not loaded. \n" + e.toString());
    }
  }
}
// [END bigquery_load_table_gcs_parquet]
