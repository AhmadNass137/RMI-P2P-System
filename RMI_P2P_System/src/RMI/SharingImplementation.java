package RMI;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.HashMap;

public class SharingImplementation implements SharingInterface {
    static HashMap<Integer, Integer> ports = new HashMap<>();
    static int current_port = 4501;
    @Override
    public void setPort(int ref) throws RemoteException {
        ports.put(ref, current_port);
        current_port++;
    }

    @Override
    public int getPort(int ref) throws RemoteException {
        return ports.get(ref);
    }

    @Override
    public int findFile(String lookup) throws RemoteException {
        try {
            File root = new File("peers");
            ObjectInputStream objectInputStream = new ObjectInputStream(Files.newInputStream(Paths.get("accounts.ser")));
            HashMap<Integer, String> accounts = (HashMap<Integer, String>) objectInputStream.readObject();
            File[] peers = root.listFiles((dir, name) -> name.matches("peer[0-9]+"));
            File x;
            File[] files;
            assert peers != null;
            for (File peer : peers) {
                if (peer.isDirectory()) {
                    x = peer;
                    files = x.listFiles();
                    assert files != null;
                    for (File file : files)
                        if (file.isFile())
                            if (file.getName().equals(lookup))
                                if (accounts.containsKey(Integer.parseInt(x.getName().substring(4)))) // peer has an account
                                    if (ports.containsKey(Integer.parseInt(x.getName().substring(4)))) { // peer currently has a port
                                        objectInputStream.close();
                                        return Integer.parseInt(x.getName().substring(4));
                                    }
                }
            }
            objectInputStream.close();
            return -1; // File not found
        }
        catch (Exception e) {
            return -2; // IO error
        }
    }

    @Override
    public int signUp(int ref, String password) {
        try {
            if (ref <= 0)
                return 2; // ref must be positive
            ObjectInputStream objectInputStream = new ObjectInputStream(Files.newInputStream(Paths.get("accounts.ser")));
            HashMap<Integer, String> accounts = (HashMap<Integer, String>) objectInputStream.readObject();
            if (accounts.containsKey(ref))
                return 3; // user already exists
            objectInputStream.close();
            File create = new File("peers\\peer" + ref);
            create.mkdirs();
            accounts.put(ref, password);
            FileOutputStream fileOutputStream = new FileOutputStream("accounts.ser");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(accounts);
            objectOutputStream.close();
            return 0; // success
        } catch (Exception e) {
            return 1; // IO error
        }
    }

    @Override
    public int logIn(int ref, String password) throws RemoteException {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(Files.newInputStream(Paths.get("accounts.ser")));
            HashMap<Integer, String> accounts = (HashMap<Integer, String>) objectInputStream.readObject();
            if (!accounts.containsKey(ref))
                return 2; // no such user
            if (!accounts.get(ref).equals(password))
                return 3; // incorrect password
            objectInputStream.close();
            return 0; // success
        }
        catch (Exception e) {
            return 1; // IO error
        }
    }

//    @Override
//    public void removeUser(int ref) throws IOException, ClassNotFoundException, RemoteException {
//        ObjectInputStream objectInputStream = new ObjectInputStream(Files.newInputStream(Paths.get("accounts.ser")));
//        HashMap<Integer,String> accounts = (HashMap<Integer, String>) objectInputStream.readObject();
//        accounts.remove(ref);
//        FileOutputStream fileOutputStream = new FileOutputStream("accounts.ser");
//        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
//        objectOutputStream.writeObject(accounts);
//        objectOutputStream.close();
//    }

    @Override
    public int addFile(int ref, String sourceFile) throws RemoteException {
        File source = new File(sourceFile);
        if (!source.exists())
            return 2; // no such file
        if (!source.isFile())
            return 3; // source is directory, not file
        File dest = new File("peers\\peer" + ref);
        if (!dest.exists())
            if (!dest.mkdir())
                return 4; // could not create directory
        File output = new File("peers\\peer" + ref + "\\" + source.getName());
        if (output.exists() && output.isFile())
            return 5; // file already exists
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
}
