#!/bin/bash

gcc -o server server.c
gcc -o client client.c

for exp in 1 2 3 4 5 6 7 8; do
    size=$((10**exp))
    echo "Buffer size $size bytes"
    
    ./server 4000 $size &
    SERVER_PID=$!
    echo $SERVER_PID
    
    sleep 2
    
    ./client localhost 4000 $size
    
    # kill -9 $SERVER_PID
done


