package org.erachain.network;

import org.erachain.network.message.Message;

import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

/**
 * интерфейс для класса Network - не знаю зачем так сделано - неудобно искать методы
 */
public interface ConnectionCallback {

    void onConnect(Peer peer, boolean asNew);

    void tryDisconnect(Peer peer, int banForMinutes, String error);

    //void banOnError(Peer peer, String error);
    boolean isKnownAddress(InetAddress address, boolean andUsed);

    boolean isKnownPeer(Peer peer, boolean andUsed);

    List<Peer> getActivePeers(boolean onlyWhite);

    int getActivePeersCounter(boolean onlyWhite);

    Peer getKnownPeer(Peer peer);

    void onMessage(Message message);

    Peer startPeer(Socket socket);

}