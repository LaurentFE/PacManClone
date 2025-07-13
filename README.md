# PacMan clone, in Java

## How to run project
Make sure your Java home environment variable is set to the Java version 21, as it is the version used to develop this application.\
Build with command line from `<project folder>/src/main/java` :\
```javac fr/LaurentFE/pacManClone/PacManClone.java```\
When the build is done, run this command to execute :\
```java fr/LaurentFE/pacManClone/PacManClone```

## Controls
Move with ```directional arrows``` or ```Z Q S D```

## Context of the project
### WHAT
A clone of the classic arcade game Pac Man, in Java

### HOW
Developed in Java JDK 21

IDE : https://www.jetbrains.com/idea/ (Community Edition)

### WHY
The goals are : 
- To create a video game purely in Java

### SCOPE
The target result is :
- Have a fully navigable maze
- PacMan can be moved around the maze, respecting constraints
- 4 Ghosts chase PacMan with different behaviours to reach him
- Ghosts can chase, disperse, be frightened, and go resurrect at their base
- PacMan can eat pellets to increase score
- PacMan can eat a power pellet to make them frightened
- PacMan can eat a frightened ghost, increasing score and forcing the ghost to go resurrect at their base
- Level is reset when all pellets have been eaten

Increasing difficulty, and fruits may be added to the list. We'll see.
