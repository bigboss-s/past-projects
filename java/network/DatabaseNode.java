import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseNode {

    volatile static HashSet<String> currentRequests = new HashSet<String>();

    volatile static ArrayList<ConnectionThread> connections = new ArrayList<ConnectionThread>();

    static Integer key=null;
    static Integer value=null;

    static Integer listenOn = null;

    public static void main(String[] args) {

        ServerSocket serverSocket = null;

        if(args.length==0){
            System.out.println();
        }

        for (int i = 0; i < args.length; i++) {
            try {
                switch (args[i]) {
                    case "-tcpport":
                        listenOn = Integer.parseInt(args[++i]);
                        break;
                    case "-record":
                        String keyVal = args[++i];
                        key = Integer.parseInt(keyVal.split(":")[0]);
                        value = Integer.parseInt(keyVal.split(":")[1]);
                        break;
                    case "-connect":
                        String addressPort = args[++i];
                        Socket socket;
                        String address = addressPort.split(":")[0];
                        int port = Integer.parseInt(addressPort.split(":")[1]);
                        try {
                            socket = new Socket(address, port);
                            connections.add(new ConnectionThread(socket, true));
                        } catch (IOException e) {
                            System.out.println("could not connect to " + address + ":" + port);
                            break;
                        }
                        break;
                    default:
                        System.out.println(args[i]);
                        System.out.println("incorrect parameters: parameter not recognised");
                        System.exit(-1);
                }
            } catch (NumberFormatException e){
                System.out.println("incorrect parameters: argument not a number");
                System.exit(-1);
            }
        }
        if(listenOn!=null && key!=null && value!=null){
            System.out.println("listening on: "
                    +listenOn+"\nkey="+key+", value="+value);
        } else {
            System.out.println("incorrect parameters: not enough parameters");
            System.exit(-1);
        }

        for (ConnectionThread connection : connections){
            try{
                connection.start();
            } catch (Exception e) {
                connections.remove(connection);
                e.printStackTrace();
            }
        }

        try {
            serverSocket = new ServerSocket(listenOn);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true){
            Socket socket = null;
            try{
                socket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("incoming connection from: "+socket.getInetAddress().getHostAddress()+":"+socket.getPort());
            ConnectionThread st = null;
            try {
                st = new ConnectionThread(socket, false);
                connections.add(st);
                st.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static HashSet<String> getCurrentRequests() {
        return currentRequests;
    }

    public static Integer getKey() {
        return key;
    }

    public static Integer getValue() {
        return value;
    }

    public static void setValue(Integer value) {
        DatabaseNode.value = value;
    }

    public static void setKey(Integer key) {
        DatabaseNode.key = key;
    }

    public static Integer getListenOn() {
        return listenOn;
    }

    public static ArrayList<ConnectionThread> getConnections() {
        return connections;
    }

    public static List<ConnectionThread> getValidConnections(){
        return connections.stream().filter( c -> !c.isTerminating && !c.isClient).collect(Collectors.toList());
    }

    public static void terminateNode(){
        Iterator<ConnectionThread> iterator = connections.iterator();
        while(iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }

        System.exit(0);
    }

    public static void checkThreads(){
        connections.removeIf(connectionThread -> connectionThread.isTerminating);
    }
}
