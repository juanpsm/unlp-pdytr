#!/bin/bash

gcc -w sha512/server.c sha512/checksum.c -lm -o server -lssl -lcrypto

echo -e "10³\t10⁴\t10⁵\t10⁶\t10⁷"
for i in {1..10}; do
for exp in 3 4 5 6 7; do
    size=$((10**exp))

    ./server 4003 $size
done
echo ""
done
