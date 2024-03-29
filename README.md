# Hangboard
![Logo](././app/src/main/res/mipmap-xxhdpi/ic_launcher.png)

## Describe

Application dedicated for climbers to training on hangboard.

## Future

The application will be completely rebuilt before release on Google Play. I plan to add:
- tests
- dependency injection
- repository pattern
- shared pref.
- more viewModels than one
- foreground service for timer
- translation to Polish language

## Features

- Timer with the ability to set the time of hanging, rest, number of series and repetitions and pause time.
- Possibility to save any number of configurations
- Ability to save training history with details about: grip type, crimp type, additional weight and more.
- Sound alarm when exercise step ends.
- Notification in the notification bar with the current status of the exercise.

## Installation

- Use build option in Android Studio
- [APK](https://1drv.ms/u/s!ArukgIiDJtcOkjvV4spuIkwKQKB3?e=4Yb42q)

## Supported devices

The application works on devices with Android 5.0 or higher.

## Technologies

- MVVM
- RoomDb
- Coroutines
- Navigation Component
- Fragments
- RecyclerView
- Broadcast Receiver and Foreground Service

## Architecture

Application uses `MVVM` as its architecture pattern. `HangboardActivity` is
the main activity of the app. It is composed of three main fragments: TimerFragment, 
SavedConfigurationsFragment and HistoryFragment with `Navigation Component`. The `ViewModel`
of this app is located in HangboardViewModel. `Exercise` class is responsible for calculating
the time and running the exercise. `RoomDb` support is located in the db folder. Notification uses
`Foreground Service` for updates time when app is in the background and `Broadcast Receiver`
for start and stop command. 

## Project Status

Project is in progress. 


## Contact


<a href="https://www.linkedin.com/in/bartosz-filipow/"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white" />
<a href="mailto:filipow.bartosz@gmail.com"><img src="https://img.shields.io/badge/Gmail-D14836?style=for-the-badge&logo=gmail&logoColor=white" /></a> 
</br>Created by Bartosz Filipów. Feel free to contact me.

## License

[MIT](./LICENSE.md)

## Screenshots

<img src="./Screenshots/Main.png" alt="Main screen" width="50%" height="50%">
<img src="./Screenshots/SavedConfigs.png" alt="Configurations" width="50%" height="50%">
<img src="./Screenshots/SloperDetails.png" alt="Slopers details" width="50%" height="50%">
<img src="./Screenshots/EdgeDetails.png" alt="Edge details" width="50%" height="50%">
<img src="./Screenshots/History.png" alt="History" width="50%" height="50%">
