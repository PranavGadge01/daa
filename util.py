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
