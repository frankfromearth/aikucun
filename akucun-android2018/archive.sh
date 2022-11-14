#!bin/bash

./gradlew clean apkRelease -Pchannels=@markets.txt
