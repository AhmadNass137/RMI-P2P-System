package RMI;
import java.rmi.*;
public interface PeerInterface extends Remote {
    int requestFile(int requesterRef, int ownerRef, String filename) throws RemoteException;
}