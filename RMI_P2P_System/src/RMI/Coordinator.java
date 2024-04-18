package RMI;

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

public class Coordinator extends SharingImplementation {
    public static void main(String[] args) throws RemoteException, AlreadyBoundException {
        SharingImplementation impl = new SharingImplementation();
        SharingInterface stub = (SharingInterface) UnicastRemoteObject.exportObject(impl,0);
        LocateRegistry.createRegistry(4322);
        Registry registry = LocateRegistry.getRegistry("localhost",4322);
        registry.bind("SharingInterface", stub);
        System.err.println("Server ready");
    }
    public Coordinator() throws RemoteException {}
}