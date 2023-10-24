#include "checksum.h"

void calculate_checksum(const char *data, size_t len, unsigned char *output) {
    MD5_CTX md5;
    MD5_Init(&md5);
    MD5_Update(&md5, data, len);
    MD5_Final(output, &md5);
}
