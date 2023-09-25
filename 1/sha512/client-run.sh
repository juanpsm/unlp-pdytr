#!/bin/bash

gcc -w client.c checksum.c -lm -o client -lssl -lcrypto
gcc -w server.c checksum.c -lm -o server -lssl -lcrypto

echo -e "10³\t\t10⁴\t\t10⁵\t\t10⁶\t\t10⁷"
for i in {1..100}; do
for exp in 3 4 5 6 7; do
    size=$((10**exp))

    #./server 4001 $size &
    #sleep 1

    ./client 163.10.54.131 4003 $size
done
echo ""
done
