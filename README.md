# Copy Once Paste Anywhere - Android App Repo

## Introduction:

We have built a solution for synchronizing the clipboard content (any copied text, or URL) across all the devices a user has logged in. There are two types of applications for users - Android app and [Web app](https://clipboard-sync-angular-app.appspot.com/). The user has to simply login to these devices in order to use our system. Whenever a user copies some text in any of their logged-in devices, it is copied on all of their other devices, ready to be pasted. This makes it much more convenient for users to transfer content from one device to another.

The is the GitHub repository of the Android app of our project. You can install the [APK](https://github.com/OOAD-Semester-Project/android-app/raw/master/base.apk) on your Android smartphone and try it out! (*Requires Android 9 or lower*)

## Features:

**1. Login and Sync:** In order to use our system, users need to login using their username and password on all their devices. In the GIF below, we can see that we're logging in with the same username on both devices - the Android phone (using the app) and the Desktop device (using the webapp). Once logged in, the user can see the shared clipboard on all their devices. The clipboards are split into two tabs - _Mobile_ and _Desktop_. Clips in the Mobile tab show all the clips that have been copied from Android devices using our app, while the clips in the Desktop tab show all the clips that have been copied from desktop/tablet/mobile devices using our webapp.

![](https://github.com/OOAD-Semester-Project/android-app/blob/master/media/Login-and-sync.gif)

**2. Copy from a Desktop device (using Web Browser) - Paste it in Android device:**

![](https://github.com/OOAD-Semester-Project/android-app/blob/master/media/Desktop-to-Android-copy-final.gif)
