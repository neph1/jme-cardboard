# jme-cardboard
Google Cardboard integration for jMonkeyEngine 3.1

How do I use this thing!?

This has only been tested against jMonkeyEngine 3.1 (trunk). It most likely won't work with 3.0 without modifications.
Currently, there is an issue where only one viewport is shown with Google Cardboard version 0.5.4. It's recommended to use this with version 0.5.3

Before using it, you need to add Android support to your project through the SDK:

http://wiki.jmonkeyengine.org/doku.php/jme3:android?s[]=android

You also need to follow the instructions for how to develop for Google Cardboard:

https://developers.google.com/cardboard/android/download

Then, the project specific things:
New way using CardboardHarnessFragment:
1. Add jme-cardboard.jar to your project.
2. Turn on Android deployment for your project (Properties/Application/Android
3. Follow these instructions to run jme in a fragment: http://www.softwarepioneering.com/2015/07/running-jmonkeyengine-3-on-android.html
4. But instead of extending AndroidHarnessFragment, extend CardboardHarnessFragment instead.

Old way using CardboardHarness:
1. Add jme-cardboard.jar to your project.
2. Turn on Android deployment for your project (Properties/Application/Android
3. In the generated MainActivity.java (Important Files/Android Main Activity) have it extend CardboardHarness instead of AndroidHarness.
4. Change the appClass in the same to your project's application file.
5. Enjoy?

For an example, look at test/CardboardStarTravel

If you wish to build the sources yourself, it needs access to an android.jar file.

Disclaimer:
This has been developed against jMonkeyEngine 3.1. Support in 3.0 is not verified.

