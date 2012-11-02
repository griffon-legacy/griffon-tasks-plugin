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

import griffon.plugins.tasks.*;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 20.07.11 18:36
 */
public class DefaultTaskManager implements TaskManager {
    private final DefaultTaskListenerSupport taskListenerSupport = new DefaultTaskListenerSupport();
    private final Map<String, TaskControl> tasks = new ConcurrentHashMap<String, TaskControl>();

    private final ExecutorService executorService = new ThreadPoolExecutor(0, 20,
        60L, TimeUnit.SECONDS,
        new SynchronousQueue<Runnable>());

    private AtomicInteger blockingCounter = new AtomicInteger(0);

    public DefaultTaskManager(final Blocker blocker) {
        taskListenerSupport.addListener(new TaskListenerAdapter() {

            public void stateChanged(ChangeEvent<State> event) {
                final State newState = event.getNewValue();
                final String contextId = event.getSource().getContextId();
                final Task task = event.getSource().getTask();
                if (newState != null) {
                    if (newState.isFinalState()) {
                        tasks.remove(contextId);
                        if (task.getMode() == Mode.BLOCKING) {
                            if (blockingCounter.decrementAndGet() == 0) {
                                blocker.unblock(task);
                            }
                        }
                    }
                    if (newState == State.STARTED) {
                        if (task.getMode() == Mode.BLOCKING) {
                            blockingCounter.incrementAndGet();
                            blocker.block(task);
                        }
                    }
                }
            }
        });
    }

    public <V, C> TaskControl<V> create(Task<V, C> task) {
        DefaultTaskContext context = new DefaultTaskContext(new TaskWorker<V, C>(task), taskListenerSupport);
        DefaultTaskControl<V> control = new DefaultTaskControl<V>(context);
        tasks.put(control.getContext().getContextId(), control);
        context.fireStateChangeEvent(null, State.PENDING); //must be after the task-control has been added to the map; so listeners can access it
        return control;
    }

    public TaskListenerSupport getTaskListenerSupport() {
        return taskListenerSupport;
    }

    public Iterable<TaskControl> getTasks(TaskPredicate predicate) {
        return TaskIterable.filter(tasks.values(), predicate);
    }

    public TaskControl findTask(TaskPredicate predicate) {
        return TaskIterable.find(tasks.values(), predicate);
    }

    public Future<?> submit(final Runnable task) {
        return executorService.submit(task);
    }

    public <T> Future<T> submit(Callable<T> task) {
        return executorService.submit(task);
    }

    public TaskControl getTask(String contextId) {
        return tasks.get(contextId);
    }
}
