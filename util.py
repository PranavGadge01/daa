from scapy.all import *
from socket import gethostbyname

print("\n========== PROGRAM 1 : ICMP PING ==========\n")

icmp_pkt = IP(dst="8.8.8.8")/ICMP()

icmp_pkt.show()

icmp_res = sr1(icmp_pkt, timeout=2)

if icmp_res:
    print("\nICMP Reply Received\n")
    icmp_res.show()
else:
    print("No ICMP reply")


print("\n========== PROGRAM 2 : UDP PACKET ==========\n")

udp_pkt = IP(dst="8.8.8.8")/UDP(dport=53)/"Hello UDP"

udp_pkt.show()

udp_res = sr1(udp_pkt, timeout=2)

if udp_res:
    print("\nUDP Reply Received\n")
    udp_res.show()
else:
    print("No UDP reply")


print("\n========== PROGRAM 3 : DNS QUERY ==========\n")

dns_pkt = IP(dst="8.8.8.8")/UDP(dport=53)/DNS(
    rd=1,
    qd=DNSQR(qname="google.com")
)

dns_pkt.show()

dns_res = sr1(dns_pkt, timeout=2)

if dns_res:
    print("\nDNS Reply Received\n")
    dns_res.show()
else:
    print("No DNS reply")


print("\n========== PROGRAM 4 : HTTP GET REQUEST ==========\n")

ip = gethostbyname("example.com")

syn = IP(dst=ip)/TCP(dport=80, flags="S")

syn_ack = sr1(syn, timeout=2)

if syn_ack:

    ack = IP(dst=ip)/TCP(
        dport=80,
        flags="A",
        ack=syn_ack.seq + 1
    )

    send(ack)

    http_pkt = IP(dst=ip)/TCP(
        dport=80,
        flags="PA"
    )/"GET / HTTP/1.1\r\nHost: example.com\r\n\r\n"

    send(http_pkt)

    print("HTTP GET request sent")

else:
    print("No SYN-ACK received")


print("\n========== PROGRAM 5 : TRACEROUTE ==========\n")

for ttl in range(1, 20):

    trace_pkt = IP(
        dst="8.8.8.8",
        ttl=ttl
    )/UDP(dport=33434)

    trace_res = sr1(trace_pkt, timeout=2, verbose=0)

    if trace_res:

        print(ttl, "->", trace_res.src)

        if (
            trace_res.haslayer(ICMP)
            and trace_res.getlayer(ICMP).type == 3
        ):
            print("Destination reached")
            break









#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <sys/wait.h>

#define BUFFER 1024
#define MAX_CLIENTS 10

/* =========================================================
   PROGRAM 1
   Simple Client Server
========================================================= */

#define PORT1 5000

void p1_server()
{
    int server_fd, new_socket;
    struct sockaddr_in address;
    int addrlen = sizeof(address);
    char buffer[BUFFER];

    server_fd = socket(AF_INET, SOCK_STREAM, 0);

    address.sin_family = AF_INET;
    address.sin_addr.s_addr = INADDR_ANY;
    address.sin_port = htons(PORT1);

    bind(server_fd, (struct sockaddr *)&address, sizeof(address));

    listen(server_fd, 3);

    printf("Program1 Server waiting...\n");

    new_socket = accept(server_fd,
                        (struct sockaddr *)&address,
                        (socklen_t *)&addrlen);

    read(new_socket, buffer, BUFFER);

    int characters = strlen(buffer);
    int words = 0, sentences = 0;

    for(int i = 0; i < characters; i++)
    {
        if(buffer[i] == ' ' || buffer[i] == '\n')
            words++;

        if(buffer[i] == '.')
            sentences++;
    }

    words++;

    char result[BUFFER];

    sprintf(result,
            "Characters: %d\nWords: %d\nSentences: %d\n",
            characters, words, sentences);

    send(new_socket, result, strlen(result), 0);

    close(new_socket);
    close(server_fd);
}

void p1_client()
{
    int sock;
    struct sockaddr_in serv_addr;

    char buffer[BUFFER];
    char paragraph[BUFFER];

    sock = socket(AF_INET, SOCK_STREAM, 0);

    serv_addr.sin_family = AF_INET;
    serv_addr.sin_port = htons(PORT1);

    inet_pton(AF_INET, "127.0.0.1", &serv_addr.sin_addr);

    connect(sock,
            (struct sockaddr *)&serv_addr,
            sizeof(serv_addr));

    printf("Enter paragraph:\n");

    fgets(paragraph, BUFFER, stdin);

    send(sock, paragraph, strlen(paragraph), 0);

    read(sock, buffer, BUFFER);

    printf("\nServer Response:\n%s\n", buffer);

    close(sock);
}

/* =========================================================
   PROGRAM 2
   Concurrent Server
========================================================= */

#define PORT2 5001

void p2_handle_client(int client_socket)
{
    char buffer[BUFFER];

    memset(buffer, 0, BUFFER);

    read(client_socket, buffer, BUFFER);

    int characters = strlen(buffer);
    int words = 0;
    int sentences = 0;

    for(int i = 0; i < characters; i++)
    {
        if(buffer[i] == ' ')
            words++;

        if(buffer[i] == '.')
            sentences++;
    }

    if(characters > 0)
        words++;

    char result[BUFFER];

    sprintf(result,
            "Characters: %d\nWords: %d\nSentences: %d\n",
            characters, words, sentences);

    send(client_socket, result, strlen(result), 0);

    close(client_socket);

    exit(0);
}

void p2_server()
{
    int server_fd, client_socket;
    struct sockaddr_in address;

    int addrlen = sizeof(address);

    server_fd = socket(AF_INET, SOCK_STREAM, 0);

    address.sin_family = AF_INET;
    address.sin_addr.s_addr = INADDR_ANY;
    address.sin_port = htons(PORT2);

    bind(server_fd,
         (struct sockaddr *)&address,
         sizeof(address));

    listen(server_fd, 5);

    printf("Concurrent Server running on port %d...\n", PORT2);

    while(1)
    {
        client_socket = accept(server_fd,
                               (struct sockaddr *)&address,
                               (socklen_t *)&addrlen);

        if(fork() == 0)
        {
            close(server_fd);

            p2_handle_client(client_socket);
        }

        close(client_socket);
    }
}

void p2_client()
{
    int sock;
    struct sockaddr_in serv_addr;

    char buffer[BUFFER];
    char paragraph[BUFFER];

    sock = socket(AF_INET, SOCK_STREAM, 0);

    serv_addr.sin_family = AF_INET;
    serv_addr.sin_port = htons(PORT2);

    inet_pton(AF_INET, "127.0.0.1", &serv_addr.sin_addr);

    connect(sock,
            (struct sockaddr *)&serv_addr,
            sizeof(serv_addr));

    printf("Enter paragraph:\n");

    fgets(paragraph, BUFFER, stdin);

    send(sock, paragraph, strlen(paragraph), 0);

    read(sock, buffer, BUFFER);

    printf("\nServer Response:\n%s\n", buffer);

    close(sock);
}

/* =========================================================
   PROGRAM 3
   Two Client Chat
========================================================= */

#define PORT3 5002

void p3_relay(int sender, int receiver)
{
    char buffer[BUFFER];
    int bytes;

    while(1)
    {
        memset(buffer, 0, BUFFER);

        bytes = recv(sender, buffer, BUFFER, 0);

        if(bytes <= 0)
            break;

        if(strncmp(buffer, "exit", 4) == 0)
        {
            send(receiver,
                 "Other user exited.\n",
                 19,
                 0);

            break;
        }

        send(receiver, buffer, strlen(buffer), 0);
    }

    close(sender);
    close(receiver);

    exit(0);
}

void p3_server()
{
    int server_fd, client1, client2;

    struct sockaddr_in address;

    int addrlen = sizeof(address);

    server_fd = socket(AF_INET, SOCK_STREAM, 0);

    address.sin_family = AF_INET;
    address.sin_addr.s_addr = INADDR_ANY;
    address.sin_port = htons(PORT3);

    bind(server_fd,
         (struct sockaddr *)&address,
         sizeof(address));

    listen(server_fd, 2);

    printf("Waiting for two clients...\n");

    client1 = accept(server_fd,
                     (struct sockaddr *)&address,
                     (socklen_t *)&addrlen);

    printf("Client 1 connected.\n");

    client2 = accept(server_fd,
                     (struct sockaddr *)&address,
                     (socklen_t *)&addrlen);

    printf("Client 2 connected.\n");

    printf("Chat started!\n");

    if(fork() == 0)
        p3_relay(client1, client2);

    if(fork() == 0)
        p3_relay(client2, client1);

    wait(NULL);
    wait(NULL);

    close(server_fd);
}

void p3_client()
{
    int sock;

    struct sockaddr_in serv_addr;

    char buffer[BUFFER];

    sock = socket(AF_INET, SOCK_STREAM, 0);

    serv_addr.sin_family = AF_INET;
    serv_addr.sin_port = htons(PORT3);

    inet_pton(AF_INET,
              "127.0.0.1",
              &serv_addr.sin_addr);

    connect(sock,
            (struct sockaddr *)&serv_addr,
            sizeof(serv_addr));

    printf("Connected to chat server.\n");
    printf("Type 'exit' to end chat.\n\n");

    if(fork() == 0)
    {
        while(1)
        {
            memset(buffer, 0, BUFFER);

            int bytes = recv(sock, buffer, BUFFER, 0);

            if(bytes <= 0)
                break;

            printf("\nMessage Received: %s", buffer);

            fflush(stdout);
        }
    }
    else
    {
        while(1)
        {
            fgets(buffer, BUFFER, stdin);

            printf("Message Sent: %s", buffer);

            send(sock, buffer, strlen(buffer), 0);

            if(strncmp(buffer, "exit", 4) == 0)
                break;
        }
    }

    close(sock);
}

/* =========================================================
   PROGRAM 4
   Group + Private Chat
========================================================= */

#define PORT4 5003

int clients[MAX_CLIENTS];
char usernames[MAX_CLIENTS][50];
int client_count = 0;

void broadcast(char *message, int sender_index)
{
    for(int i = 0; i < client_count; i++)
    {
        if(i != sender_index && clients[i] != 0)
        {
            send(clients[i],
                 message,
                 strlen(message),
                 0);
        }
    }
}

void direct_message(char *message,
                    char *target,
                    int sender_index)
{
    for(int i = 0; i < client_count; i++)
    {
        if(strcmp(usernames[i], target) == 0 &&
           clients[i] != 0)
        {
            send(clients[i],
                 message,
                 strlen(message),
                 0);

            return;
        }
    }

    send(clients[sender_index],
         "User not found\n",
         15,
         0);
}

void remove_client(int index)
{
    close(clients[index]);

    clients[index] = 0;
}

void handle_client(int index)
{
    char buffer[BUFFER];

    while(1)
    {
        memset(buffer, 0, BUFFER);

        int bytes = recv(clients[index],
                         buffer,
                         BUFFER,
                         0);

        if(bytes <= 0 ||
           strncmp(buffer, "exit", 4) == 0)
        {
            char msg[BUFFER];

            sprintf(msg,
                    "%s left the chat\n",
                    usernames[index]);

            printf("%s", msg);

            broadcast(msg, index);

            remove_client(index);

            exit(0);
        }

        if(buffer[0] == '@')
        {
            char target[50], msg[BUFFER];

            sscanf(buffer,
                   "@%s %[^\n]",
                   target,
                   msg);

            char formatted[BUFFER];

            sprintf(formatted,
                    "[Private %s]: %s\n",
                    usernames[index],
                    msg);

            direct_message(formatted,
                           target,
                           index);
        }
        else
        {
            char formatted[BUFFER];

            sprintf(formatted,
                    "[Group %s]: %s",
                    usernames[index],
                    buffer);

            broadcast(formatted, index);
        }
    }
}

void p4_server()
{
    int server_fd, new_socket;

    struct sockaddr_in address;

    int addrlen = sizeof(address);

    server_fd = socket(AF_INET, SOCK_STREAM, 0);

    int opt = 1;

    setsockopt(server_fd,
               SOL_SOCKET,
               SO_REUSEADDR,
               &opt,
               sizeof(opt));

    address.sin_family = AF_INET;
    address.sin_addr.s_addr = INADDR_ANY;
    address.sin_port = htons(PORT4);

    bind(server_fd,
         (struct sockaddr *)&address,
         sizeof(address));

    listen(server_fd, MAX_CLIENTS);

    printf("Chat Server Running on Port %d...\n", PORT4);

    while(1)
    {
        new_socket = accept(server_fd,
                            (struct sockaddr *)&address,
                            (socklen_t *)&addrlen);

        recv(new_socket,
             usernames[client_count],
             50,
             0);

        clients[client_count] = new_socket;

        printf("%s joined chat\n",
               usernames[client_count]);

        char joinmsg[BUFFER];

        sprintf(joinmsg,
                "%s joined the chat\n",
                usernames[client_count]);

        broadcast(joinmsg, client_count);

        if(fork() == 0)
            handle_client(client_count);

        client_count++;
    }
}

void p4_client()
{
    int sock;

    struct sockaddr_in serv_addr;

    char buffer[BUFFER];
    char username[50];

    sock = socket(AF_INET, SOCK_STREAM, 0);

    serv_addr.sin_family = AF_INET;
    serv_addr.sin_port = htons(PORT4);

    inet_pton(AF_INET,
              "127.0.0.1",
              &serv_addr.sin_addr);

    connect(sock,
            (struct sockaddr *)&serv_addr,
            sizeof(serv_addr));

    printf("Enter username: ");

    fgets(username, 50, stdin);

    username[strcspn(username, "\n")] = 0;

    send(sock, username, strlen(username), 0);

    printf("\nCommands:\n");
    printf("Text -> Group chat\n");
    printf("@username msg -> Private chat\n");
    printf("exit -> Quit\n\n");

    if(fork() == 0)
    {
        while(1)
        {
            memset(buffer, 0, BUFFER);

            int bytes = recv(sock,
                             buffer,
                             BUFFER,
                             0);

            if(bytes <= 0)
                break;

            printf("%s", buffer);
        }
    }
    else
    {
        while(1)
        {
            fgets(buffer, BUFFER, stdin);

            send(sock, buffer, strlen(buffer), 0);

            if(strncmp(buffer, "exit", 4) == 0)
                break;
        }
    }

    close(sock);
}

/* =========================================================
   MAIN MENU
========================================================= */

int main(int argc, char *argv[])
{
    if(argc != 2)
    {
        printf("Usage:\n");

        printf("./socket_all p1server\n");
        printf("./socket_all p1client\n");

        printf("./socket_all p2server\n");
        printf("./socket_all p2client\n");

        printf("./socket_all p3server\n");
        printf("./socket_all p3client\n");

        printf("./socket_all p4server\n");
        printf("./socket_all p4client\n");

        return 0;
    }

    if(strcmp(argv[1], "p1server") == 0)
        p1_server();

    else if(strcmp(argv[1], "p1client") == 0)
        p1_client();

    else if(strcmp(argv[1], "p2server") == 0)
        p2_server();

    else if(strcmp(argv[1], "p2client") == 0)
        p2_client();

    else if(strcmp(argv[1], "p3server") == 0)
        p3_server();

    else if(strcmp(argv[1], "p3client") == 0)
        p3_client();

    else if(strcmp(argv[1], "p4server") == 0)
        p4_server();

    else if(strcmp(argv[1], "p4client") == 0)
        p4_client();

    else
        printf("Invalid option\n");

    return 0;
}
