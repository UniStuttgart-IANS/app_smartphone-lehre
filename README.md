# Innovative Smartphone Lehre / Smartphone-Apps im Mathematikstudium

## Download project and import to Android Studio 
This project can be easily imported to Android Studio by following the steps below:
1. Download or clone this repository
```
git clone https://github.com/UniStuttgart-IANS/app_smartphone-lehre.git
cd app_smartphone-lehre
``` 
2. Download required third-party modules 
```
svn co https://github.com/PhilJay/MPAndroidChart/trunk/MPChartLib MPChartLib
svn co https://github.com/Nishant-Pathak/MathView/trunk/mathview-lib mathview-lib
```
3. Open Android studio and import this project
4. Adapt all build.gradle to your local gradle version, i.e., change *compileSdkVersion*, *buildToolsVersion* and *targetSdkVersion*.

## Project structure
The java source files can be found in the dirctory `./app/src/main/java/com/uni_stuttgart/isl`. Each sub-app, e.g. Intergration, has his own subdirectory. The correspondig *\*.xml* files, that describe the layout of each app can be found in the directory `./res/layout`.

## Edit or add new apps
A new sub-app can be easily added using the Android-Studio functions. Do a right-click on the `isl` directory and select `new package`. Then do a right-click on the directory of the new package and select `new activity -> basic activity`. Your new app will automatically be added to the `Manifest.xml` file.
Additionally, you should add the sub-app to the navigation and the intro. Therefore, do a smart copy and paste of the existing apps.
Please contact us if you need further information.

### Prüfungsmodus
The "Prüfungsmodus" is exemplarily integrated in the *Riemann Integral* app.
The mode is intended to work on exercises specially designed for the app.
In this mode, an identification of the student can be filled in and a timestamp is displayed.
Please contact us if you need further information.

## License
This software is licensed under a Creative Commons Attribution-ShareAlike 4.0 International License. For further information see *license.txt*.
