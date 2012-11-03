/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package griffon.plugins.tasks;

import griffon.core.Observable;
import griffon.util.UIThreadWorker;

/**
 * @author Andres Almiray
 */
public interface TaskWorker<V, C> extends Observable, UIThreadWorker<V, C> {
    Long getStartedTimestamp();

    Long getFinishTimestamp();

    String getPhase();

    Task<V, C> getTask();

    void setContext(TaskContext context);

    boolean isError();

    void publishProgress(int progress);

    void publishChunks(C... chunks);

    void setPhase(String phase);
}
