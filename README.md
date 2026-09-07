
## Running
```
gradlew run --args="filepath"
```

# Criteria
## a) General code quality
There are no errors or warnings from the compiler and I have not suppressed any warnings.


## b) Use of map or dictionary
I have used a map in several different places throughout the program.

The App class/object (responsible for coordinating all the main logic and user input) has a map of String, TaskStructure for storing all the base tasks (tasks without a set parent). These tasks may be a TaskLeaf or TaskParent (which has its own HashMap of the same type that stores all of its subtasks), which cumulatively stores the whole WBS program from the file.

Additionally, App has a map of Integer, EffortEstimate to store all of the estimate options in a strategy pattern.

## c) Clear and distinct class/interface/method responsibilities

### App
The App class/object handles all of the user input and is the "hub" for all of the main logic. It will take input from the user (using methods such as enterString, enterN) and then decide what to do with this input. To do so, it stores a map of TaskStructures to hold the WBS structure. It also has a few methods that simply print the options for maximum re-usability.

The only method which may not fit is `performEstimates()`. The other class that could hold this would be TaskStructure (as a concrete method), which would make more sense responsibility wise.

However, to move this method to TaskStructure, it would require either creating an App object to access the scanner, estimations, generalParent, etc, which moves some responsibilities (such as user input) into TaskStructure where it shouldn't be, or passing all this data through the function which becomes convoluted and messy.

As such, I decided to keep performEstimates in the App class as this function mainly requires user input, which is a part of the App's responsibilities.

### TaskStructure
TaskStructure is an abstract class that serves as a Parent class to TaskParent and TaskLeaf (in the form of the Composite Pattern).

In addition to initialising some vital (abstract) methods for the children class to override, I have also added some concrete static methods which are used in combination with the HashMap of base tasks to make it easier to search through the whole map with maximum code re-usability and to keep the task logic and responsibilities in a place that makes the most sense.

### Effort Estimate
Effort Estimate is a very simple Strategy pattern. EffortEstimate itself is an interface which is extended by DiscussionEstimate, HighestEstimate and MedianEstimate which each have their own way of performing `getEffortEstimate()` depending on the configuration chosen by the user.

The number of estimators and the estimator configuration itself is stored within App and used when calling the EffortEstimate as these are configurable variables, dependent on user input, and it wouldn't make sense to store it in a EffortEstimate object itself as it would require much more interactions with the EffortEstimate classes and reduces the efficiency of the Strategy pattern.

### WBSFileIO
WBSFileIO has very clear responsibility. It has two simple methods, reading and writing to a file that is in the WBS structure. App calls these methods on the instantisation of the program and the end of it, passing the file name (given by the args of running the program), and either passing or receiving the base tasks map to/from `writeToWBS` and `readWBS` respectively.

### WBSException
WBSException is the superclass of the custom WBS exceptions used in this program. It extends Exception.

WBSException is thrown when the user tries to interact with the WBS in a way that doesn't make sense or is not supported (and is not covered by WBSFormatException or WBSLogicException), for example trying to perform estimates for a task that doesn't exist. WBSFormatException extends this for the case where the file is not formatted correctly, and WBSLogicException extends it where the WBS itself is incorrect.

The WBSFormatException subclass of WBSException is thrown when the file given does not have the correct WBS structure, such as a TaskParent without any subtasks, a task having a parent that doesn't exist, or duplicate task IDs. Typically, if there is a WBSFormatException, the program is ended as this means that the file isn't structured correctly and can cause more issues through further usage.

The WBSLogicException is thrown mostly within fileIO. It means that the file is in the correct format but it doesn't logically comply with the WBS, for example, a parent task with no subtasks (line can be formatted correctly, but it shouldn't be a parent task) or a leaf task with subtasks (a leaf task cannot have subtasks). It is also possibly thrown when a non-positive integer is attempted to be set to task leaf (however guards in performEstimates will stop this beforehand).

## d) Appropriate error handling and logging
As mentioned above, the WBS program uses 3 custom exception types (WBSException, WBSLogicException, WBSFormatException) that are thrown when the program is given bad input in the form of a badly formatted or illogical WBS file or when trying to perform estimates.

In addition to this, there are other exceptions thrown such as IllegalArgumentException and there are also exceptions caught such as IOExceptions in file IO.

There are also many guards used that can sometimes prevent these custom exceptions from being thrown before they happen, such as checking that all TaskParents have subtasks, checking that certain objects are not null, and containers are not empty, etc. There are also many guards in place for bad user input (such as searching for a task ID, trying to enter estimation configuration/menu choice or entering estimated time in performEstimates) which will result in the prompt for input repeating until the user inputs sufficient data.

Logs are also used covering the levels of finest, finer, fine, info, warning and severe. They are used to log user input, the creation of objects, and certain functions that have background workings that won't immediately show the reason for fail without logging. I have also modified the logging properties so that the logs will output to a file within the WBS_assignment folder for easier viewing of the logs pertaining to this project.

## e) Appropriate use of either the Strategy Pattern or Template Method Pattern
I have used the Strategy Pattern to perform the effort estimates. The interface is EffortEstimate which defines the function of `getEffortEstimate(Scanner sc, TaskLeaf task, List<Integer> estimates)`. The three classes (each pertaining to the 3 options of effort estimate) extend this with HighestEstimate, MedianEstimate and DiscussionEstimate.

The Strategy pattern is then used in the program as App creates a HashMap for these estimate options and simply calls `(estimateOptions.get(estimationConfiguration)).getEffortEstimate()` which runs the EffortEstimate method depending on the user's previous configuration (or DiscussionEstimate by default).

This is very efficient as it replaces the need to do an if-else statement or a switch and will just use one line of code.

The only issue I had with this was the inclusion of Scanner to the parameters. As the scanner is only used with DiscussionEstimate, I was hesitant to put it into the parameters (as it would have to be for all 3), however, as DiscussionEstimate requires 1 more user input (of the decided estimate after getting the estimates for all 3), it needs to access the scanner.

This could technically go into performEstimates, however this removes the logic behind the Strategy pattern as it is just HighestEstimate and MedianEstimate that really benefit from it.

As such, I included the scanner just to be used in DiscussionEstimate so that the DiscussionEstimate could perform guards for bad user input before returning the new estimate back to App. The scanner is also not instantiated within EffortEstimate itself and is simply passed in to ensure that there are not multiple instances of scanners within the program.

---

I could also have done the EffortEstimate with the Template method, although the Strategy pattern is a lot more simple to use here. While this would still definitely work, there are not enough differences in the 3 estimate types to warrant this as it would likely just look like the Strategy pattern except with a template method calling `getEffortEstimate` instead of calling it within App. Template method is better to use when there are multiple algorithms (but it can be used with 0+ hooks technically).

The more common way to do this would just be to use a switch statement and remove the EffortEstimate classes altogether, just performing the logic for each in the switch statement instead.

## f) Appropriate use of either the Decorator Pattern or Composite Pattern
I have used the Composite Pattern in the form of TaskStructure and its children.

TaskStructure is an abstract class that has the protected fields of ID, description and parent (all three of which are also present in TaskParent and TaskLeaf) and it also declares some methods which are shared between the two.

App then has a map of TaskStructure (which are really TaskParent and TaskLeaf objects) to search through. TaskParent also has a map of TaskStructure (following the Composite pattern) to hold all of the subtasks in (which can be TaskParent or TaskLeaf).

This object structure is effective as it allows TaskParent to override the function display() (for example) to use subtask.display() which will display the subtasks that are either TaskLeaf or TaskParent. This applies for all of the abstract classes that TaskStructure declares, such as finding tasks, calculating the cumulative time for all of the subtasks, and printing the WBS.

Seeing as this structure follows the composite pattern, it can technically represent a tree of any size, even empty, however, due to the nature of the WBS, I have programmed exceptions to be thrown if a TaskParent has no subtasks (despite the fact that the Composite pattern allows this). 

Aside from the Composite pattern, TaskStructure is also used for some general logic for the map that App holds, in the form of concrete static methods. This isn't technically relevant to the Composite pattern, however it allows the logic and responsibilities for this map that App holds to lie in the same place.

---

Using the decorator class here would be difficult and unnecessary, same with if you used it on the estimate types as its not really meant to store data but instead used as a way to have different methods for objects that change slightly.

If I didn't use the Composite pattern, I would likely create one class of "Task" which would have the logic of TaskLeaf and TaskParent merged. It would have some extra methods such as hasSubtasks() and hasEstimate() (being mutually exclusive). This would work best if Task Parents can have estimated times and Task Leafs could have subtasks, but this does not work with the standard WBS.

 While it would still work with the standard WBS and likely have similar method calls to what I have, it could get confusing quickly as the Task class would have a map for subtasks and a field for the estimate but only be able to use one of which at a time.

To combat this, I could split it into two classes of TaskParent and TaskLeaf. The map in App would then have to be type Object and would have to check instanceof often. Having TaskStructure (the Composite pattern) makes it a lot more simple as the two classes will share methods and it allows the map to have a specific type that has some wiggle room.

## g) Clear and Correct UML class diagram of your whole application
The UML diagram (in PDF and editable form) within the WBS_assignment folder.

I have included usage dependencies, aggregation, and inheritance. Whilst my program uses libraries that sometimes have other objects that my classes own (such as a scanner), I have decided not to include these in my diagram with a usage line as it can get complicated fast. However, I have included these usages when they are present in the fields, method parameters or method returns.

I have also decided to include when a log is used in the fields of a class to better display what classes have logging capabilities.

Additionally, I have included when classes throw the custom WBS exceptions. As there is no standard way to do this, I have used the `<<create>>` notation as it technically creates an exception instance when an exception is thrown. While other exceptions are thrown in this program, they are not included in this UML Class Diagram as they are not custom.

## h) Penalty marks for code that doesn't work, or doesn't fully address the Problem Description
To my knowledge, all of my code works, with graceful endings or exceptions thrown and guards for invalid input.