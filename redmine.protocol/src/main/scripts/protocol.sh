#!/bin/bash

#######################################################
#
# Start, stop, restart application and show it's status
#
# author: Markus Meier
# date:   2017-06-29
# helpful alias:
#     alias protocol=/path/to/this/script.sh
#######################################################

# global variables
path="/home/markus"
app="redmine.protocol"
version="1.1.1"
profile="remote"

# global functions
function status {
        local stat=`ps aux|grep protocol|grep redmine`
        echo "$stat"
}

function stop {
	status|awk -F'[[:space:]]+' '{print $2}'|xargs kill -15
}

function start {
	java -jar -Dspring.profiles.active=$profile -Dlogging.file=$path/$app.log $path/$app-$version.jar &
}

# show usage if started without args
if [ "$#" -ne 1 ] ; then
	echo "Usage: protocol [option]"
	echo "Options:"
	echo "  ps      show app status"
	echo "  start   start or restart app"
	echo "  stop    shutdown app"
	exit 3
fi

result=$(status)

if [ $1 = "ps" ] ; then
	if [[ -z "${result// }" ]] ; then
		echo "protocol is not running!"
	else
		echo $result
	fi

elif [ $1 == "start" ] ; then
	if [[ -z "${result// }" ]] ; then
		echo -e "\e[31mStarting protocol..."
		start
	else
		echo -e "\e[31mRe-starting protocol..."
		stop
		start
	fi

elif [ $1 == "stop" ] ; then
	if [[ -z "${result// }" ]] ; then
		echo "protocol is not running!"
	else	
		echo -e "\e[31mStopping protocol..."
		stop
	fi

else
	echo "Unknown option: '$1'. Run script without args to see help."
fi
