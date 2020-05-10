# Copy Once Paste Anywhere - Android App Repo

## Introduction:

We have built a solution for synchronizing the clipboard content (any copied text, or URL) across all the devices a user has logged in. There are two types of applications for users - Android app and [Web app](https://clipboard-sync-angular-app.appspot.com/). The user has to simply login to these devices in order to use our system. Whenever a user copies some text in any of their logged-in devices, it is copied on all of their other devices, ready to be pasted. This makes it much more convenient for users to transfer content from one device to another.

The is the GitHub repository of the Android app of our project. You can install the [APK](https://github.com/OOAD-Semester-Project/android-app/raw/master/base.apk) on your Android smartphone and try it out! (*Requires Android 9 or lower*)

## Architecture:

![Architecture Diagram](https://github.com/OOAD-Semester-Project/android-app/blob/master/media/architecture-diagram.png)

Following is a brief description of this final architecture:
* Angular has been used for developing the web application, and Java has been used for developing the Android application. 
* Using the Socket.io library, we have used Websockets to maintain the connection between the user applications and the Node.js server. Whenever there is a new clipboard data, the Android app or desktop app send the data to the server and the corresponding controller in the server receives the message through this WebSocket. 
* We have used WebSockets in our system because the data needs to be pushed to the server frequently and using request-response mechanism (RESTful web service) will have more overhead compared to WebSockets.
* We have used Keycloak as our authentication server that maintains user credentials and is responsible for issuing and invalidating auth tokens to clients (Android or web app). All the auth tokens are validated by this authentication server.
* We have used MongoDB ATLAS as our persistent data store.
* There are multiple controllers in the Node.js microservice that interact with the Keycloak authentication server and MongoDB.
* The node.js server, Keycloak authentication server, MongoDB database, and the Angular web app are containerized and deployed using Docker and Docker swarm. We have used Google Cloud Platform to deploy our infrastructure.


## Features:

**1. Login and Sync:** In order to use our system, users need to login using their username and password on all their devices. In the GIF below, we can see that we're logging in with the same username on both devices - the Android phone (using the app) and the Desktop device (using the webapp). Once logged in, the user can see the shared clipboard on all their devices. The clipboards are split into two tabs - _Mobile_ and _Desktop_. Clips in the Mobile tab show all the clips that have been copied from Android devices using our app, while the clips in the Desktop tab show all the clips that have been copied from desktop/tablet/mobile devices using our webapp.

![](https://github.com/OOAD-Semester-Project/android-app/blob/master/media/Login-and-sync.gif)

**2. Copy from a Desktop device (using Web Browser) - Paste it anywhere:** Any piece of text or URL that is copied using the web app is instantantaneously available to paste on all other devices. In the GIF below, we can see that a piece of text copied on the desktop device is not only updated in the clips list under the desktop tab, but is also copied on the Android device, ready to be pasted! 

![](https://github.com/OOAD-Semester-Project/android-app/blob/master/media/Desktop-to-Android-copy-final.gif)
