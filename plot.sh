#!/bin/sh

LOC=/uw/khaki13
X=plot.x
Y=plot.y
INTERVAL=5

while true
do
    ./client.sh -l $LOC getAttributeValue / num_processes >> $Y
    date +"%s" >> $X
    echo "Value fetched"
    sleep $INTERVAL
done