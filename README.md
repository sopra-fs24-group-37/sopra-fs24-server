# SwissQuiz Application

## 📖 Table of Contents

1. [Introduction](#introduction)
2. [Technologies](#technologies)
3. [High Level Components](#high-level-components)
4. [Launch & Development](#launch--development)
5. [Roadmap](#roadmap)
6. [Authors](#authors)
7. [Acknowledgments](#acknowledgements)
8. [License](#license)

## Introduction <a name="introduction"></a>

SwissQuiz is an interactive online quiz about the geography of Switzerland for up to four players. In each round, players will be shown a random image taken somewhere in Switzerland. Your task is to guess where the image was taken by clicking on a map of Switzerland. The application thus allows you to discover and explore the beauty of Switzerland in a playful manner.

## Technologies <a id="technologies"></a>

The following technologies were used for backend development:

* [Java](https://www.java.com/en/) - Programming language
* [Google Cloud Platform](https://cloud.google.com/) - Cloud Deployment and Data Storage
* [Stomp](https://stomp-js.github.io/stomp-websocket/) - Messaging protocol used for websocket communication with the server
* [Unsplash API](https://unsplash.com/documentation) - API used for importing pictures into the game

## High Level Components <a id="high-level-components"></a>

SwissQuiz consists of several key components that work together to provide a seamless user experience for managing games and rounds. Below is a brief summary of each main component, their roles, and how they are correlated.

### 1. User Component
The User component is responsible for managing user-related operations, including user registration, user login, and profile management. It includes classes such as `UserService`, which handles business logic mainly to support the API endpoints in the `UserController`, and `UserRepository`, which interacts with the database.

- **Main Class:** [`UserService`](https://github.com/sopra-fs24-group-37/sopra-fs24-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/service/UserService.java)
- **Key Functions:** `createUser`, `loginUser`, `updateUser`
- **Related Files:** [`UserRepository.java`](https://github.com/sopra-fs24-group-37/sopra-fs24-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/repository/UserRepository.java), [`UserController.java`](https://github.com/sopra-fs24-group-37/sopra-fs24-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/controller/UserController.java)

### 2. Game Component
The Game component manages the lifecycle of games, including creation, updating, and termination of games. It ensures the proper setup and management of game states and rules.

- **Main Class:** [`GameService`](https://github.com/sopra-fs24-group-37/sopra-fs24-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/service/GameService.java)
- **Key Functions:** `createGame`,`startGame`, `endGame`, `joinGame`
- **Related Files:** [`GameController.java`](https://github.com/sopra-fs24-group-37/sopra-fs24-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/controller/GameController.java), [`GameRepository.java`](https://github.com/sopra-fs24-group-37/sopra-fs24-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/repository/GameRepository.java)

### 3. GamePlayer Component
The GamePlayer component handles the association between users and games. It tracks the participation of users in different games and maintains their progress and states within each game. While it does not have an extensive list of service functions, it's role is essential to separate the tracking of the same user across multiple games.

- **Main Class:** [`GamePlayerEntity`](https://github.com/sopra-fs24-group-37/sopra-fs24-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/entity/GamePlayer.java)
- **Key Functions:** `setPlayerId`, `setScore`
- **Related Files:** [`GamePlayerService.java`]((https://github.com/sopra-fs24-group-37/sopra-fs24-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/service/GamePlayerService.java)), [`GamePlayerRepository.java`](https://github.com/sopra-fs24-group-37/sopra-fs24-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/repository/GamePlayerRepository.java)

### 4. Round Component
The Round component is responsible for managing the individual rounds within a game. This includes creating new rounds, updating round data, and handling round-specific logic such as scoring based on the guessed locations for each user and location tracking of the fetched image.

- **Main Class:** [`RoundService`](https://github.com/sopra-fs24-group-37/sopra-fs24-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/service/RoundService.java)
- **Key Functions:** `createRound`, `updatePlayerGuess`, `fetchPictureFromApi`
- **Related Files:** [`RoundStompController.java`](https://github.com/sopra-fs24-group-37/sopra-fs24-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/controller/RoundStompController.java), [`RoundStatsRepository.java`](https://github.com/sopra-fs24-group-37/sopra-fs24-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/repository/RoundStatsRepository.java)

### Correlation Between Components
These components are closely interlinked to ensure the smooth functioning of the application:

- **User Component**: Provides users who participate in games managed by the Game Component.
- **Game Component**: Oversees the overall game structure and utilizes the GamePlayer Component to manage user participation.
- **GamePlayer Component**: Acts as the bridge between users and games, ensuring users are correctly associated with their respective games.
- **Round Component**: Operates within the context of a game, managing the detailed progression and actions for each game player and within each game round.


## Launch & Development <a id="launch--development"></a>
### Getting started with Spring Boot
-   Documentation: https://docs.spring.io/spring-boot/docs/current/reference/html/index.html
-   Guides: http://spring.io/guides
    -   Building a RESTful Web Service: http://spring.io/guides/gs/rest-service/
    -   Building REST services with Spring: https://spring.io/guides/tutorials/rest/

### Setup this Template with your IDE of choice
Download your IDE of choice (e.g., [IntelliJ](https://www.jetbrains.com/idea/download/), [Visual Studio Code](https://code.visualstudio.com/), or [Eclipse](http://www.eclipse.org/downloads/)). Make sure Java 17 is installed on your system (for Windows, please make sure your `JAVA_HOME` environment variable is set to the correct version of Java).

#### IntelliJ
If you consider to use IntelliJ as your IDE of choice, you can make use of your free educational license [here](https://www.jetbrains.com/community/education/#students).
1. File -> Open... -> SoPra server template
2. Accept to import the project as a `gradle project`
3. To build right click the `build.gradle` file and choose `Run Build`

#### VS Code
The following extensions can help you get started more easily:
-   `vmware.vscode-spring-boot`
-   `vscjava.vscode-spring-initializr`
-   `vscjava.vscode-spring-boot-dashboard`
-   `vscjava.vscode-java-pack`

**Note:** You'll need to build the project first with Gradle, just click on the `build` command in the _Gradle Tasks_ extension. Then check the _Spring Boot Dashboard_ extension if it already shows `soprafs24` and hit the play button to start the server. If it doesn't show up, restart VS Code and check again.

### Building with Gradle
You can use the local Gradle Wrapper to build the application.
-   macOS: `./gradlew`
-   Linux: `./gradlew`
-   Windows: `./gradlew.bat`

More Information about [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) and [Gradle](https://gradle.org/docs/).

#### Build

```bash
./gradlew build
```

#### Run

```bash
./gradlew bootRun
```

You can verify that the server is running by visiting `localhost:8080` in your browser.

#### Test

```bash
./gradlew test
```

#### Development Mode
You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed.

Start two terminal windows and run:

`./gradlew build --continuous`

and in the other one:

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest`

### API Endpoint Testing with Postman
We recommend using [Postman](https://www.getpostman.com) to test your API Endpoints.

### Debugging
If something is not working and/or you don't know what is going on. We recommend using a debugger and step-through the process step-by-step.

To configure a debugger for SpringBoot's Tomcat servlet (i.e. the process you start with `./gradlew bootRun` command), do the following:

1. Open Tab: **Run**/Edit Configurations
2. Add a new Remote Configuration and name it properly
3. Start the Server in Debug mode: `./gradlew bootRun --debug-jvm`
4. Press `Shift + F9` or the use **Run**/Debug "Name of your task"
5. Set breakpoints in the application where you need it
6. Step through the process one step at a time

### Testing
Have a look here: https://www.baeldung.com/spring-boot-testing

## Roadmap <a id="roadmap"></a>

# Roadmap

Our team has thought of 2 engaging and "informative" features that new developers who want to contribute to this project could add:

### 1. Heatmap for Players Familiarity of Switzerland
Add a heatmap feature to analyze and display which areas of Switzerland are the least familiar to players. This will help identify regions where players struggled to guess correctly the most, providing valuable insights and potentially educating users around new places to discover in Switzerland.

- **Key Components to Modify:**
  - `RoundService` to collect and analyze location data from rounds.
  - `GameService` to aggregate player performance data.
  - A new `HeatmapController` to generate and display the heatmap.
  - Frontend components to visualize the heatmap.

### 2. Like Images and Recommend Trips
Develop a feature that allows players to like images and receive trip recommendations for those corresponding locations. This feature would enhance player engagement by connecting the game with real-world travel interests and encourage exploring Switzerland.

- **Key Components to Modify:**
  - `UserService` to track liked locations.
  - `RoundService` to integrate the liking functionality.
  - A new `RecommendationService` to generate trip suggestions.
  - Frontend components for liking images and displaying recommended trips.

These features will enhance the user experience and provide more gamified functionalities that can make the application more engaging and educative.


## Authors <a id="authors"></a>

* [Gian-Luca Führer](https://github.com/gf237) - client
* [Manuel Widmer](https://github.com/manuel-widmer) - client
* [Diana Hidvégi](https://github.com/DiaHidvegi) - server
* [Andri Spescha](https://github.com/Skyl3ss) - server

## Acknowledgments <a id="acknowledgements"></a>

We would like to thank [Fengjiao Ji](https://github.com/feji08) for guiding us through the course in her capacity as teaching assistant. Also, we would like to acknowledge the following providers of opensource data that were crucial in creating our application:

- **[Unsplash](https://unsplash.com/de)**: provider of high quality images of Switzerland (and beyond)

- **[swisstopo](https://www.swisstopo.admin.ch/de)**: provider of geojson data for the national boundaries and canton boundaries of Switzerland

- **[Esri](https://www.esri.com/en-us/home)**: provider of the World Ocean Base tile used for rendering the geographical surface of Switzerland


## License <a id="license"></a>

This project is licensed under the GNU GPLv3 License. 

</div>
