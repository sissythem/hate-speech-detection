#!/usr/bin/env bash

jarsfile="$(pwd)/jars.txt"
cd target/classes
check_kill.sh &
java -Xmx12g -XX:-UseGCOverheadLimit -Xms2g -cp "$(cat $jarsfile)"  gr.di.hatespeech.main.HateSpeechDetection
