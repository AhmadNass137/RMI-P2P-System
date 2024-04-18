package RMI;
import java.io.IOException;
import java.rmi.*;
public interface SharingInterface extends Remote {
    int findFile(String lookup) throws RemoteException;
     int signUp(int ref, String password) throws IOException, ClassNotFoundException;
     int logIn(int ref, String password) throws RemoteException;
//     void removeUser(int ref) throws IOException, ClassNotFoundException, RemoteException;
     int addFile(int ref , String sourceFile) throws RemoteException;

      void setPort(int ref) throws RemoteException;
      int getPort(int ref) throws RemoteException;
}