#!/usr/bin/env bash
avail_mem="python3 get_mem.py"
thresh=0.1
while [ 1 ]; do
	if [ $($avail_mem) -lt $thresh ]; then
		echo "Memory: $avail_mem"
		echo "Killing!"
		#kill -9 "pgrep java"
		break;
	fi
	sleep 5s
done
