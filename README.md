# Become A Endless Fighter

A student project made by the members of Info class of Privates Gymnasium Schloss Torgelow
## Project Demo
![img.png](img.png)

## Install the Game

Coming Soon in Release page. We are now in developing phase.
## Setting Up for Contributors (IOS)

Install IntelliJ from Internet.
- Open IntelliJ and click on Clone Repository
- Login with your GitHub account. You should be able to see the name of this project
- ![img_4.png](img_4.png)
- There should be a pop out window during the process, telling you that you should install git.
- DO NOT close this pop out window, click install and wait till git is installed.
- DO NOT try to install git from internet. I have no idea why installing git from internet fails here.
- (maybe there is a way to solve this, but I am no IOS expert)
- ![img_6.png](img_6.png)
- then you need to reopen the IntelliJ completely. There should be no red warnings anymore.
- Clone this project to your local device following the instructions from IntelliJ.
- Opening the project for the first time might take some time, because there are dependencies need to be downloaded.
- ![img_7.png](img_7.png)
- wait till gradle is imported.
- Click on Edit Configuration.
  ![img_1.png](img_1.png)
- Configure as the followings.
  ![img_2.png](img_2.png)
- Congratulations, now you can run and test the game as you want. (click the green triangle on the top right of the screen to run the program)
- We suggest committing your code into a separate branch.



## Setting Up for Contributors (Windows)
If you want to contribute to this project:

- Email us and become a contributor (3318193092@qq.com).
- I suppose a Java JDK is already installed on your device, if not install one.
- Install and open IntelliJ IDE. (You can use your own code editor, as long as you are familiar with Git)
- In tab **Getting Start with VCS**.
![img_3.png](img_3.png)
- Login with your GitHub account. You should be able to see the name of this project
![img_4.png](img_4.png)
- Clone this project to your local device following the instructions from IntelliJ.
- Click on Edit Configuration.
![img_1.png](img_1.png)
- Configure as the followings.
![img_2.png](img_2.png)
- Congratulations, now you can run and test the game as you want. (click the green triangle on the top right of the screen to run the program)
- We suggest committing your code into a separate branch.


## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.

## Gradle
This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:

- `build`: builds sources and archives of every project.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the application. (<- Use this one)
