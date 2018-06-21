#!/usr/bin/env bash

jarsfile="$(pwd)/jars.txt"
cd target/classes
java -cp "$(cat $jarsfile)"  gr.di.hatespeech.main.NgramGenerator
