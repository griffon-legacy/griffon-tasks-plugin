/*
 * Copyright 2011 Eike Kettner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.griffon.runtime.tasks;


import griffon.core.UIThreadManager;
import griffon.plugins.tasks.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 20.07.11 19:16
 */
public class TaskExecutionTest {
    private final static Logger log = LoggerFactory.getLogger(TaskExecutionTest.class);
    private final TaskManager manager = new NonBlockingTaskManager();

    @Test
    public void testExceptionTask() throws Exception {
        UIThreadManager.getInstance().setUIThreadHandler(new SwingUIThreadHandler());

        Task<Long, Long> task = new LongTask();
        final TaskControl<Long> control = manager.create(task);
        control.getContext().addListener(new TaskListener() {
            public void stateChanged(ChangeEvent<State> event) {
                log.info(">>> State: " + event.getOldValue() + " => " + event.getNewValue());
                if (event.getNewValue() == State.STARTED) {
                    log.info("Started: " + event.getSource().getStartedTimestamp());
                }
            }

            public void progressChanged(ChangeEvent<Integer> event) {
                log.info(">>> Progress: " + event.getOldValue() + " => " + event.getNewValue());
            }

            public void phaseChanged(ChangeEvent<String> event) {
                log.info(">>> Phase: " + event.getOldValue() + " => " + event.getNewValue());
            }
        });
        Long value = control.waitFor();
        assertNotNull(value);
        assertEquals(value.intValue(), 40L);
        log.info("Waited for task: " + value);
    }
}
