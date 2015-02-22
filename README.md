# jme-cardboard
Google Cardboard integration for jMonkeyEngine 3

How do I use this thing!?

1. Add jme-cardboard.jar to your project.
2. Turn on Android deployment for your project (Properties/Application/Android
3. In the generated MainActivity.java (Important Files/Android Main Activity) have it extend CardboardHarness instead of AndroidHarness.
4. Change the appClass in the same to your project's application file.
5. Enjoy?

For an example, look at test/CardboardStarTravel

If you wish to build the sources yourself, it needs access to an android.jar file.

