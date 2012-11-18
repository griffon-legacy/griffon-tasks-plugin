/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author Andres Almiray
 */
class TasksGriffonPlugin {
    // the plugin version
    String version = '0.1'
    // the version or versions of Griffon the plugin is designed for
    String griffonVersion = '1.2.0 > *'
    // the other plugins this plugin depends on
    Map dependsOn = [:]
    // resources that are included in plugin packaging
    List pluginIncludes = []
    // the plugin license
    String license = 'Apache Software License 2.0'
    // Toolkit compatibility. No value means compatible with all
    // Valid values are: swing, javafx, swt, pivot, qt
    List toolkits = []
    // Platform compatibility. No value means compatible with all
    // Valid values are:
    // linux, linux64, windows, windows64, macosx, macosx64, solaris
    List platforms = []
    // URL where documentation can be found
    String documentation = ''
    // URL where source can be found
    String source = 'https://github.com/griffon/griffon-tasks-plugin'

    List authors = [
        [
            name: 'Andres Almiray',
            email: 'aalmiray@yahoo.com'
        ]
    ]
    String title = 'Task management support'
    // accepts Markdown syntax. See http://daringfireball.net/projects/markdown/ for details
    String description = '''
The tasks plugin provides the means to schedule and manage tasks. Tasks will always be executed off
the UI thread. In many respects task execution follows the same rules as JDK's [SwingWorker][1].

This plugin contains code written by Eike Kettner ([swing-tasks][2]).

Usage
-----

#### Creating Tasks

Tasks must implement the `Task` interface. There is a helper class (`AbstractTask`) which can be subclassed,
for which the `execute()` method must be implemented as a minimum. This method should contain the long running code.

The following example depicts a Task used to find the first N prime numbers

     import griffon.plugins.tasks.Tracker;
     import org.codehaus.griffon.runtime.tasks.AbstractTask;
     
     public class PrimeNumbersTask extends 
             AbstractTask<List<Integer>, Integer> {
         public PrimeNumbersTask(int numbersToFind) {
             //initialize
         }
     
         @Override
         public Integer execute(Tracker<Integer> tracker) throws Exception {
             while (!enough && !isCancelled()) {
                     int number = nextPrimeNumber();
                     tracker.publish(number);
                     tracker.setProgress(100 * numbers.size() / numbersToFind);
                 }
             }
             return 0;
         }
     }

#### TaskManager

The `TaskManager` is the entry point for scheduling tasks. It is used to create `TaskControl` instances, register
listeners and find currently running tasks.

You can create one by instantiating the provided default implementation

    TaskManager taskmanager = new NonBlockingTaskManager();
#### Executing and controlling Tasks

Use the `TaskManager` to create a `TaskControl` object and invoke its execute() method.

    TaskControl taskControl = taskManager.create(new PrimeNumbersTask(10));
    taskControl.execute();

The `execute()` method will return immediately and submit the task for execution. Now the `TaskControl` can be used
to cancel the execution of the task. The method `waitFor()` can be invoked to wait for the task to finish.

#### Listeners

You can register TaskListeners to receive notifications on tasks. There are 3 different ways to register listeners
concerning the scope of events received:

 * register on TaskManager (use `taskManager.getTaskListenerSupport()`)to receive events for all tasks
 * register on TaskManager using a _taskId_ to receive events for tasks of the given _taskId_
 * register on a TaskControl (use `taskControl.getTaskContext()`) to receive events solely for one execution


[1]: http://docs.oracle.com/javase/6/docs/api/javax/swing/SwingWorker.html
[2]: https://github.com/eikek/swing-tasks
'''
}
