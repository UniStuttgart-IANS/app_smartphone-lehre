# Innovative Smartphone Lehre / Smartphone-Apps im Mathematikstudium

## Download project and import to Android Studio 
This project can be easily imported to Android Studio by following the steps below:
1. Download or clone this repository
```
git clone https://github.com/UniStuttgart-IANS/app_smartphone-lehre.git
cd app_smartphone-lere
``` 
2. Download needed third-party modules 
```
svn co https://github.com/PhilJay/MPAndroidChart/trunk/MPChartLib MPChartLib
svn co https://github.com/Nishant-Pathak/MathView/trunk/mathview-lib mathview-lib
```
3. Open Android studio and import this project
4. Adapt all build.gradle to your local gradle version, i.e., change *compileSdkVersion*, *buildToolsVersion* and *targetSdkVersion*.

## Project structure
The java source files can be found in the dirctory `./app/src/main/java/com/uni_stuttgart/isl`. Each sub-app, e.g. Intergration, has his own subdirectory. The correspondig *\*.xml* files, that describes the layout of each app can be found in the directory `./res/layout`.

## License
This software is licensed under a Creative Commons Attribution-ShareAlike 4.0 International License. For further information see *license.txt*.
