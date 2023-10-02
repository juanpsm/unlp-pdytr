#!/bin/bash

gcc -w sha512/client.c sha512/checksum.c -lm -o client -lssl -lcrypto

echo -e "10³\t\t10⁴\t\t10⁵\t\t10⁶\t\t10⁷"
for i in {1..10}; do
for exp in 3 4 5 6 7; do
    size=$((10**exp))
    ./client 192.168.56.11 4003 $size
done
echo ""
done
