# How to Start Server

## Install Node.js on your local machine
* go to https://nodejs.org/en/download and install node.js. Follows the directions on the website.
* Check: run the following code in your terminal
* `node -v`
* the output should be something like this
* `v22.12.0`
* ![img.png](img/img.png)
## Setup Node.js 
* go to the Terminal of IntelliJ
* run command `cd server` to get into server folder
* run command `npm init`
* follow the steps (you can leave all the options blank)
* run command `npm install express http socket.io` and wait till all dependencies are successfully installed

## Start the Server
1. Method 1: Use Terminal
* go to the Terminal of IntelliJ
* run command `cd server` to get into server folder
* run command `node server_v2.js` to start the server
* press `Ctrl` + `C` to stop the server
2. Method 2: Configure Running Setup
* go to edit configuration
* ![img_1.png](img/img_1.png)
* Configure as the following
* ![img_2.png](img/img_2.png)
* Now you can start and stop the server just like what you do with your client application.

