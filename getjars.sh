#!/usr/bin/env bash

dependenciesPath="/home/sissy/Documents/Professional/University/UOA/Graduate/Thesis/hate-speech-detection/target/dependencyJARs"
jars="$(find $dependenciesPath -type f -iname *.jar)"
jarsfile="$(pwd)/jars.txt"
echo $jars | tr ' ' ':' | sed 's/:$//g' > $jarsfile
