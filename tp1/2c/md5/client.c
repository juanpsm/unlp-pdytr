#include <stdio.h>
#include <sys/types.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include "checksum.h"

void error(char *msg)
{
    perror(msg);
    exit(0);
}

int main(int argc, char *argv[])
{
    int sockfd, portno, nr, nw;
    struct sockaddr_in serv_addr;
    struct hostent *server;
    clock_t start, end;
    double cpu_time_used;
    unsigned char checksum[MD5_DIGEST_LENGTH]; 
    memset(checksum, 0, MD5_DIGEST_LENGTH);
    
    // Agregado: TOMA BUFFER SIZE COMO ARGUMENTO
    if (argc < 4) {
       fprintf(stderr,"usage: ./client hostname port buffer_size\n", argv[0]);
       exit(0);
    }

    // Agregado: ALOCA MEMORIA PARA EL BUFFER 
    int buffer_size = atoi(argv[3]); 
    char *buffer = malloc(buffer_size * sizeof(char));
    if (buffer == NULL) {
        error("ERROR allocating memory for buffer");
    }

	//TOMA EL NUMERO DE PUERTO DE LOS ARGUMENTOS
    portno = atoi(argv[2]);
	
	//CREA EL FILE DESCRIPTOR DEL SOCKET PARA LA CONEXION
    sockfd = socket(AF_INET, SOCK_STREAM, 0);
	//AF_INET - FAMILIA DEL PROTOCOLO - IPV4 PROTOCOLS INTERNET
	//SOCK_STREAM - TIPO DE SOCKET 
	
    if (sockfd < 0) 
        error("ERROR opening socket");
	
	//TOMA LA DIRECCION DEL SERVER DE LOS ARGUMENTOS
    server = gethostbyname(argv[1]);
    if (server == NULL) {
        fprintf(stderr,"ERROR, no such host\n");
        exit(0);
    }
    bzero((char *) &serv_addr, sizeof(serv_addr));
    serv_addr.sin_family = AF_INET;
	
	//COPIA LA DIRECCION IP Y EL PUERTO DEL SERVIDOR A LA ESTRUCTURA DEL SOCKET
    bcopy((char *)server->h_addr, 
         (char *)&serv_addr.sin_addr.s_addr,
         server->h_length);
     serv_addr.sin_port = htons(portno);
	
	//DESCRIPTOR - DIRECCION - TAMAÑO DIRECCION
    if (connect(sockfd,&serv_addr,sizeof(serv_addr)) < 0) 
        error("ERROR connecting");
    bzero(buffer,buffer_size);

    // Agregado: LLENAR EL BUFFER CON PATRON REPETITVO 
    memset(buffer,'A',buffer_size);

    // Agregado: CALCULAR CHECKSUM
    calculate_checksum(buffer, buffer_size, checksum);
    
    // Agregado: CHECKSUM PRINT
    printf("Checksum before sending: ");
    for(int i = 0; i < MD5_DIGEST_LENGTH; i++) {
      printf("%02x", checksum[i]);
    }
    printf("\n");

    //ENVIA UN MENSAJE AL SOCKET
    printf("Sending %d bytes \n", buffer_size);
    if (write(sockfd,buffer, buffer_size) < 0) 
         error("ERROR writing to socket");

    bzero(buffer,buffer_size);

    //Agregado: CERRAR SOCKET Y LIBERAR BUFFER
    close(sockfd);
    free(buffer);
    return 0;
}
