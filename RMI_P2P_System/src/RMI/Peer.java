package RMI;
import java.nio.file.Files;
import java.rmi.*;
import java.io.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class Peer implements PeerInterface {
    static int ref = 0;
    @Override
    public int requestFile(int requesterRef, int ownerRef, String filename) throws RemoteException {
        if (ownerRef != ref)
            return 6;
        File source = new File("peers\\peer" + ownerRef + "\\" + filename);
        if (!source.exists())
            return 2; // File doesn't exist
        if (!source.isFile())
            return 3; // File is actually a directory
        File dest = new File("peers\\peer" + requesterRef);
        if (!dest.exists() || dest.isFile())
            if (!dest.mkdir())
                return 4; // couldn't create destination directory
        File output = new File("peers\\peer" + requesterRef + "\\" + filename);
        if (output.exists() && output.isFile())
            return 5; // a file with that name already exists
        try {
            InputStream in = Files.newInputStream(source.toPath());
            OutputStream out = Files.newOutputStream(output.toPath());
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > 0)
                out.write(buffer, 0, len);
            in.close();
            out.close();
            return 0; // success
        } catch (Exception e) {
            return 1; // IO error
        }
    }

    public static void main(String[] args) throws IOException, NotBoundException, ClassNotFoundException, AlreadyBoundException {
        String url = "rmi://localhost:4322/SharingInterface";
        Scanner scan = new Scanner(System.in);
        SharingInterface sharing = (SharingInterface) Naming.lookup(url);
        int choice;
        String str;
        boolean flag = true;
        System.out.println("Ahoy!");
        while (flag) {
            System.out.print("Enter 1 to login or 2 to sign up: ");
            choice = scan.nextInt();
            switch (choice) {
                case 1:
                    System.out.print("Enter your reference number: ");
                    ref = scan.nextInt();
                    System.out.print("Enter your password: ");
                    str = scan.next();
                    switch (sharing.logIn(ref, str)) {
                        case 0:
                            System.out.println("Aye aye! Successfully logged in!");
                            sharing.setPort(ref);
                            Peer peer = new Peer();
                            PeerInterface stub = (PeerInterface) UnicastRemoteObject.exportObject(peer,0);
                            LocateRegistry.createRegistry(sharing.getPort(ref));
                            Registry registry = LocateRegistry.getRegistry("localhost",sharing.getPort(ref));
                            registry.bind("PeerInterface", stub);
                            System.err.println("Peer ready");
                            flag = false;
                            break;
                        case 1:
                            System.out.println("IO exception try logging in again :(");
                            break;
                        case 2:
                            System.out.println("Arrrgh! User not found!");
                            break;
                        case 3:
                            System.out.println("Incorrect password dude");
                            break;
                    }
                    break;
                case 2:
                    System.out.print("Enter your new reference number: ");
                    ref = scan.nextInt();
                    System.out.print("Enter your new password: ");
                    str = scan.next();
                    switch (sharing.signUp(ref, str)) {
                        case 0:
                            System.out.println("Account created successfully! Welcome aboard!");
                            sharing.setPort(ref);
                            Peer peer = new Peer();
                            PeerInterface stub = (PeerInterface) UnicastRemoteObject.exportObject(peer,0);
                            LocateRegistry.createRegistry(sharing.getPort(ref));
                            Registry registry = LocateRegistry.getRegistry("localhost",sharing.getPort(ref));
                            registry.bind("PeerInterface", stub);
                            System.err.println("Peer ready");
                            flag = false;
                            break;
                        case 1:
                            System.out.println("IO exception try signing up again :(");
                            break;
                        case 2:
                            System.out.println("Make sure your reference number in a positive number ^_^");
                            break;
                        case 3:
                            System.out.println("A user with that reference number already exists! Try a different number.");
                            break;
                    }
                    break;
                default:
                    System.out.println("Invalid input! Try again.");
            }

        }
        while (true) {
            System.out.print("Enter 1 to add a file, 2 to request file, or 3 to exit: ");
            choice = scan.nextInt();
            if (choice == 3)
                break;
            switch (choice) {
                case 1:
                    System.out.print("Enter the full path of the file you want to add: ");
                    str = scan.next();
                    switch (sharing.addFile(ref, str)){
                        case 0:
                            System.out.println("File added successfully!");
                            break;
                        case 1:
                            System.out.println("IO exception :(");
                            break;
                        case 2:
                            System.out.println("File not found! Make sure the full path of the file is written correctly");
                            break;
                        case 3:
                            System.out.println("That's a directory, not a file!");
                            break;
                        case 4:
                            System.out.println("Couldn't create a folder for you!");
                            break;
                        case 5:
                            System.out.println("You've already added a file with the same name!");
                            break;
                        default:
                            System.out.println("Invalid input.");
                    }
                    break;
                case 2:
                    System.out.print("Enter the name of the file you want to look up: ");
                    str = scan.next();
                    int dest = sharing.findFile(str);
                    if (dest <= 0)
                        System.out.println("Couldn't find your file, make sure you typed its name correctly or try again later :(");
                    else {
                        System.out.println("File found with with reference " + dest);
                        System.out.println("Grabbing the file from peer " + dest + "...");
                        String con = "rmi://localhost:" + sharing.getPort(dest) +"/PeerInterface";
                        PeerInterface peerConnection = (PeerInterface) Naming.lookup(con);
                        switch (peerConnection.requestFile(ref, dest, str)) {
                            case 0:
                                System.out.println("File downloaded successfully!");
                                break;
                            case 1:
                                System.out.println("IO error!");
                                break;
                            case 2:
                                System.out.println("File not found in destination peer.");
                                break;
                            case 3:
                                System.out.println("Oops! Looks like the file is actually a directory.");
                                break;
                            case 4:
                                System.out.println("Couldn't create a directory for you :(");
                                break;
                            case 5:
                                System.out.println("You already have a file with that name! Can't overwrite your file.");
                                break;
                        }
                    }
                    break;
                default:
                    System.out.println("Invalid input.");
            }
        }
        System.out.println("Farewell, old friend");
    }
}