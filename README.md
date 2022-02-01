
[![GPLv3 License](https://img.shields.io/badge/License-GPL%20v3-yellow.svg)](https://opensource.org/licenses/GPL-3.0)


# Mahao Android

Mahao is an Android application that recommends houses for rent to users.
            This application uses a recommender system that allows users to conduct a
            preference-based search for rental homes. The recommendations will be based on
            a collaborative recommendation model. This means that the user-user relationship,
            as well as the user-item relationship, plays a role in the recommendation. The
            application was the fourth-year project submitted in partial fulfilment of the
            requirements for a BSc. Computer Science.


## Project Setup

1. Clone the repo using 
    ```git clone https://github.com/tonyawino/Mahao-Android.git```
1. Open the project in Android Studio
1. Generate a [Google Maps API Key](https://developers.google.com/maps/documentation/android-sdk/get-api-key) 
1. Replace the API Key in the [google_maps_api](app/src/debug/res/values/google_maps_api.xml) file with the generated API key
    ```<string name="google_maps_key" templateMergeStrategy="preserve" translatable="false">YOUR_KEY_HERE</string>```
1. Hit "Run". Done!
## Architecture Overview

Mahao uses MVVM architecture for separation of concerns and state maintenance on configuration changes.

The application uses Jetpack's Navigation component and has a single Activity with 
multiple fragments.

All operations are performed via accessing API endpoints over the network, 
making the app effectively a stateless client.

The single activity and fragments are solely there to help with presentation logic. 
Each activity and the majority of the fragments have their own ViewModels, which 
contain the business logic. The ViewModel uses LiveData to react to data changes 
and instructs the fragment/activity how to update itself.



## Dependencies

These are the libraries the project depends on:


- [Jetpack](https://developer.android.com/jetpack)
  - [Viewmodel](https://developer.android.com/topic/libraries/architecture/viewmodel) - Manage UI related data in a lifecycle conscious way and act as a channel between business logic and ui
  - [View Binding](https://developer.android.com/topic/libraries/view-binding) - View binding is a feature that allows you to more easily write code that interacts with views
  - [Room](https://developer.android.com/training/data-storage/room) - Provides abstraction layer over SQLite
  - [Navigation](https://developer.android.com/guide/navigation/navigation-getting-started#Set-up) - The Navigation component provides fragment transactions
  - [Paging](https://developer.android.com/guide/navigation/navigation-getting-started#Set-up) - Load and display small chunks of data at a time
- [Google Maps](https://developers.google.com/maps/documentation/android-sdk/start) - Displays a map by using the Google Maps 
- [Places](https://developers.google.com/maps/documentation/places/android-sdk/overview) - Allows you to build location-aware apps that respond contextually to places near the user's device.
- [Retrofit](https://square.github.io/retrofit/) - Type safe http client and supports coroutines out of the box
- [Gson](https://github.com/google/gson) - Library that can be used to convert Java Objects into their JSON representation.
- [okhttp-logging-interceptor](https://github.com/square/okhttp/blob/master/okhttp-logging-interceptor/README.md) - Logs HTTP request and response data
- [Glide](https://github.com/bumptech/glide) - Media management and image loading framework for Android
- [Material Design](https://material.io/develop/android/docs/getting-started/) - Build awesome beautiful UIs
- [Dagger](https://developer.android.com/training/dependency-injection/dagger-android) - Dependency injection on Android
- [Espresso](https://developer.android.com/training/testing/espresso) - Test framework to write UI Tests
- [Ronnie-Image-Picker](https://github.com/ronnieotieno/Ronnie-Image-Picker) - Asks for Camera and storage permission and return uri of the images taken or picked from the gallery
- [Lottie for Android](https://github.com/airbnb/lottie-android) - Parses Adobe After Effects animations exported as json with Bodymovin and renders them natively on mobile
- [oss-licenses](https://developers.google.com/android/guides/opensource) - Displaying the notices for the open source libraries that your app uses
- [Smart App Rate](https://github.com/codemybrainsout/smart-app-rate) - Smart app rate dialog for Android which takes user rating into consideration
## Demo

![Create Property](./screenshots/create_property.gif =700x1500) ![View Property](./screenshots/view_property.gif =700x1500)
![View Properties list and filter](./screenshots/view_list.gif =700x1500) ![Search and sort](./screenshots/search_property.gif =700x1500)
![View Properties on map](./screenshots/map_property.gif =700x1500) ![View and edit profile](./screenshots/profile.gif =700x1500)


## Authors

- [@tonyawino](https://www.github.com/tonyawino)


## License

[GPL v3](https://opensource.org/licenses/GPL-3.0)

    Mahao is an Android application that recommends houses for rent to users. 
    Copyright (C) 2022  Tony Awino

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

