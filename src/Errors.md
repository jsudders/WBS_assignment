# Fixed

**Estimate for tasks complete without input**
Error(s):
- [Estimate effort] -> [Parent task] = "Estimates for task completed" without entering any input.
- Estimates for J only creating estimates for L
- Estimates for H only creating estimates for I and L

Fix:
File IO did not classify these tasks as leaves because they did not have a preset estimate and the split function did not include the last section. Fixed this by changing split limit to -1

**Entering a character on menu causing problems**
Errors:
- String on main menu causing infinite loop
- String on discussion estimate crashing

Fix:
Infinite loop - no sc.nextLine() / clearing buffer
Crashing - no exception for non integer

**Estimate effort doesn't specify which leaf tasks the estimates are for until after estimates are entered**
Fix: State leaf before entry

# Critical
- Fix app to run with file name as argument
- Fix intro printing to remove tab from first parents
- Add "Press Enter to Continue" to some functions and make sure it doesn't skip it
    - 

# Syntax and ease of use
- Have text when printing to file
- Remove Entered: because it takes up space