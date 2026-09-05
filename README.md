
## Running

To run your code (for debugging purposes), invoke the `gradlew` script with a `run` argument:

```
gradlew run --args="filepath"
```


## Linting and Testing

This project has been configured to use [PMD][] to check code quality, and (though you don't need it for OOSE) [JUnit 5][] to perform unit testing. To perform these steps:

[PMD]: https://docs.pmd-code.org/latest/
[JUnit 5]: https://junit.org/junit5/

```
./gradlew check
```

(Alternatively, run "`./gradlew build`".)

Look up and fix any of the [PMD warnings](), or [suppress][] them if/where appropriate.

[PMD warnings]: https://docs.pmd-code.org/latest/pmd_rules_java.html
[suppress]: https://docs.pmd-code.org/latest/pmd_userdocs_suppressing_warnings.html


# Criteria
## a) General code quality


## b) Use of map or dictionary


## c) Clear and distinct class/interface/method responsibilities

### App

### Task Structure

### Effort Estimate

### WBS_FileIO

### WBSException


### Other

The only thing that I was not sure about was moving performEstimates into TaskStructure as a concrete method. This would make more sense responsibility wise, however it would require either creating an App object to access the scanner, estimations, generalParent, etc, which moves some responsibilities (such as user input) into TaskStructure where it shouldn't be, or passing all this data through the function which becomes convoluted and messy.

As such, I decided to keep performEstimates in the App class as this function mainly requires user input, which is apart of the App's responsibilities.

## d) Appropriate error handling and logging


## e) Appropriate use of either the Strategy Pattern or Template Methid Pattern


## f) Appropriate use of either the Decorator Pattern or Composite Pattern


## g) Clear and Correct UML class diagram of your whole application


## h) Penalty marks for code that doesn't work, or doesn't fully address the Problem Description
To my knowledge, all of my code works, with graceful endings or exceptions thrown and guards for invalid input.