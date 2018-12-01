# Genonbeta Android Framework
This Android library provides a bunch of helpful abstraction layers for your
Android application where Google's Android Support Library (android.support.\*/androidx.\x)   
falls behind.

# Layers you might consider checking
* ListFragment: *Apart from Support Library's ListFragment, this does not explicitly define a ListView so that you use RecyclerView or any view that derives ViewGroup class (Check also: RecyclerViewFragment, ListViewFragment)*
* DbSharedPreferences: *Using our GDatabase library, this will help you update your setting
even when you are working with multi-process applications (Check also: PreferenceUtils)*
* SweetImageLoader: *Load bitmaps (images) in the background*
* PowerfulActionMode: *Support Action Mode works only with ListView, but this works with everything*
* DocumentFile: *If you needed work with one provided by support library, you possibly have encountered
performance backdrops. So this does not do that*
* StreamInfo: *Opens files just like Android Framework tells you*
* UpdateWithGitHub: *Only releasing new versions on GitHub? This will deliver a notification whenever you release one*

# How to include in your projects?
```xml
...
repositories {
    ...
    jcenter()
    ...
}
...
dependencies {
    ...
    implementation 'com.genonbeta.android:framework:1.0.2.4'
    ...
}
...
```
