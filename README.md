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

**1. Login and Sync:** In order to use our system, users need to login using their username and password on all their devices. In the GIF below, we can see that we're logging in with the same username on both devices - the Android phone (using the app) and the Desktop device (using the webapp). Once logged in, the user can see the shared clipboard on all their devices. The clipboards are split into two tabs - _Mobile_ and _Desktop_. Clips in the Mobile tab show all the clips that have been copied from Android devices using our app, while the clips in the Desktop tab show all the clips that have been copied from desktop/tablet/mobile devices using our webapp. We are also showing to the user which specific device the copied clip originated from. Thus, we are also able to support multiple devices on the same platform, i.e. for instance, the user can log in to our app on two different Android devices, copy clips that will show up under the _Mobile_ tab, and our app will tell the user which specific device (eg. Oneplus A5010) the copied clip originated from.

![](https://github.com/OOAD-Semester-Project/android-app/blob/master/media/Login-and-sync.gif)

**2. Copy from a Desktop device (using Web Browser) - Paste it anywhere:** Any piece of text or URL that is copied using the web app is instantantaneously available to paste on all other devices. In the GIF below, we can see that a piece of text copied on the desktop device (_desktop1_) is not only updated in the clips list under the _Desktop_ tab, but is also copied on the Android device, ready to be pasted! 

![](https://github.com/OOAD-Semester-Project/android-app/blob/master/media/Desktop-to-Android-copy-final.gif)

**3. Delete a clip from a Desktop device (using Web Browser):** On deleting any clip from the Webapp, the clipboard is re-syncronized across all logged in devices. In the GIF below, we can see that deleting a clip on the webapp from either the _Mobile_ or the _Desktop_ tab deletes that particular clip from the Android device.

![](https://github.com/OOAD-Semester-Project/android-app/blob/master/media/Desktop-delete-final.gif)

**4. Copy from a Android device (using Android app) - Paste it anywhere:** Any piece of text or URL that is copied using the Android app is instantantaneously available to paste on all other devices. In the GIF below, we can see that a piece of text copied on the Android device (_Oneplus A5010_) is not only updated in the clips list under the _Android_ tab, but is also copied on the Desktop device, ready to be pasted!

![](https://github.com/OOAD-Semester-Project/android-app/blob/master/media/Android-to-Desktop-Copy-Final.gif)

**5. Delete a clip from an Android device (using Android app):** On deleting any clip from the Android app, the clipboard is re-syncronized across all logged in devices. In the GIF below, we can see that deleting a clip on the Android app deletes that particular clip from the Desktop's webapp.

![](https://github.com/OOAD-Semester-Project/android-app/blob/master/media/Android-delete-final.gif)

**6. Different users don't share clipboards:** Clipboards are not shared across users. In other words, any changes made in the clipboard of UserA will not be seen the clipboard of UserB. In the GIF below, the current logged in user is by the username: _rajchandak_. Now, we open up a new browser window and sign in to the webapp using the username: _madhu_. We can observe that changes made in _rajchandak_'s clipboard are not reflected in _madhu_'s clipboard.

![](https://github.com/OOAD-Semester-Project/android-app/blob/master/media/Multiple-users.gif)
