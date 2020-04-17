/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.jbpm.test.performance.taskassigning;

import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.kie.server.api.model.instance.TaskEventInstance;

public class TaskStatisticsUtilTest {

    @Test
    public void delaysBetweenCompleteAndStartEvents_worksAsExpected_forTwoEvents() {
        Instant now = Instant.now();
        TaskEventInstance completed = new TaskEventInstance();
        completed.setType("COMPLETED");
        completed.setLogTime(Date.from(now));

        TaskEventInstance started = new TaskEventInstance();
        started.setType("STARTED");
        started.setLogTime(Date.from(now.plus(5, ChronoUnit.SECONDS)));

        SortedSet<TaskEventInstance> events =
                new TreeSet<>(Comparator.comparing(taskEventInstance -> taskEventInstance.getLogTime().getTime()));
        events.add(completed);
        events.add(started);
        List<Long> delays = TaskStatisticsUtil.delaysBetweenCompleteAndStartEvents(events);

        Assertions.assertThat(delays).containsExactly(5_000L);
    }

    @Test
    public void median_single() {
        List<Long> numbers = Arrays.asList(1L);
        Assertions.assertThat(TaskStatisticsUtil.median(numbers)).isEqualTo(1L);
    }

    @Test
    public void median_odd() {
        List<Long> numbers = Arrays.asList(2L, 3L, 4L);
        Assertions.assertThat(TaskStatisticsUtil.median(numbers)).isEqualTo(3L);
    }

    @Test
    public void median_even() {
        List<Long> numbers = Arrays.asList(2L, 4L, 6L, 8L);
        Assertions.assertThat(TaskStatisticsUtil.median(numbers)).isEqualTo(5L);
    }
}