package edu.hust.it4060.bootstrap.udp;

import java.net.DatagramPacket;

@FunctionalInterface
interface UDPPacketHandler {
    void handle(DatagramPacket packet);
}
