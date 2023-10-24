#!/bin/bash

#gcc -w client.c -lm -o client
#gcc -w server.c -lm -o server
gcc -w client.c checksum.c -lm -o client -lssl -lcrypto
gcc -w server.c checksum.c -lm -o server -lssl -lcrypto

for exp in 3 4 5 6; do
    size=$((10**exp))
    echo "Buffer size $size bytes"
    
    ./server 4000 $size &
    SERVER_PID=$!
    # echo $SERVER_PID
    sleep 2
    ./client localhost 4000 $size
    #kill -9 $SERVER_PID
    sleep 2
    echo ""
done
