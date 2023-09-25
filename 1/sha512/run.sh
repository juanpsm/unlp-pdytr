#!/bin/bash

#gcc -w client.c -lm -o client
#gcc -w server.c -lm -o server
gcc -w client.c checksum.c -lm -o client -lssl -lcrypto
gcc -w server.c checksum.c -lm -o server -lssl -lcrypto

echo -e "10³\t\t10⁴\t\t10⁵\t\t10⁶"
for i in {1..100}; do
for exp in 3 4 5 6; do
    size=$((10**exp))

    ./server 4001 $size &
    sleep 1

    ./client localhost 4001 $size
    sleep 1
done
echo ""
done
