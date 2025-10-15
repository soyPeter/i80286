/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:bitnomio-backend@bitnomio.io
 *
 * fraud-detection-engine - Created by pedro.almendro@bitnomio
 * Date: 9/8/25 Time: 16:53
 *
 */
package io.bitnomio.fde.infra.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executors;

/**
 * Configuration for asynchronous task execution, specifically for the fraud rule engine.
 */
@Configuration
@EnableAsync
public class AsyncTaskConfig {

  /**
   * Creates a dedicated TaskExecutor for running fraud rules.
   * <p>
   * This implementation uses Java 21's Virtual Threads, which are ideal for I/O-bound tasks
   * (like rules that might call external services).
   *
   * @return An AsyncTaskExecutor backed by a virtual-thread-per-task executor.
   */
  @Bean(name = "fraudRuleTaskExecutor")
  public AsyncTaskExecutor fraudRuleTaskExecutor() {
    // Using Java 21's new virtual thread per task executor
    return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
  }

}
