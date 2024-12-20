import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ListIterator;

public class ConnectionThread extends Thread{

    private final int connectionID;

    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;

    protected static int counter = 1;
    private final String ip;
    private final int port;

    public boolean isTerminating = false;
    public boolean isClient;

    private static final CommunicationHandler communicationHandler = new CommunicationHandler();

    public ConnectionThread(Socket socket, boolean isPermanent) throws IOException {
        connectionID=counter++;
        this.socket = socket;
        this.ip = socket.getInetAddress().getHostAddress();
        this.port = socket.getPort();
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        isClient = !isPermanent;
        System.out.println("successfully connected to: "+ip+":"+port);
        if(!isClient) {
            System.out.println(this+" will not terminate after first answer");
            out.println("NOT_A_CLIENT");
        };
    }

    @Override
    public void run(){
        try {
            while (true){
                String line = in.readLine();
                if(line !=null){
                    if(line.equals("NOT_A_CLIENT")) {
                        isClient = false;
                        System.out.println(this +" will not terminate after first reply");
                        line = "STATUS NOT_A_CLIENT";
                    }
                    if(DatabaseNode.getCurrentRequests().contains(line)){
                        System.out.println("looped on "+line);
                        out.println("REPLY/"+ line.split("/")[1]+"/ERROR");
                        line ="STATUS REQUEST_LOOPED";
                    }
                    if(line.contains("FORWARD")) DatabaseNode.getCurrentRequests().add(line);
                    synchronized (communicationHandler){
                        if(line.contains("REPLY")) {
                            System.out.println("got a reply "+line);
                            if (communicationHandler.handleReply(line)){
                                line = "STATUS REPLY_SENT";
                                if(isClient){
                                    terminate();
                                }
                            } else System.out.println("reply ["+currentThread()+"] not ready, request forwarded to next node");
                        }
                    }
                    String opString = line.split("/").length > 1 ? line.split("/")[2] : line;
                    switch (opString.split(" ")[0]){
                        case "set-value":
                            System.out.println("operation set-value");
                            setValue(line);
                            break;
                        case "get-value":
                            System.out.println("operation get-value...");
                            getValue(line);
                            break;
                        case "find-key":
                            System.out.println("operation find-key...");
                            findKey(line);
                            break;
                        case "get-max":
                            System.out.println("operation get-max...");
                            getMax(line);
                            break;
                        case "get-min":
                            System.out.println("operation get-min...");
                            getMin(line);
                            break;
                        case "new-record":
                            System.out.println("operation new-record...");
                            newRecord(opString.split(" ")[1]);
                            out.println("OK");
                            System.out.println("new record is "+DatabaseNode.getKey()+":"+DatabaseNode.getValue());
                            terminate();
                            break;
                        case "terminate":
                            System.out.println("TERMINATING NODE");
                            terminateAll();
                            break;
                        case "TERMINATING_ON_REQUEST":
                            System.out.println("TERMINATING_ON_REQUEST");
                            terminate();
                            DatabaseNode.checkThreads();
                            return;
                        case "STATUS":
                            System.out.println("OPERATING STATUS: ");
                            System.out.println(line);
                            break;
                        default:
                            System.out.println("message is not an operation:");
                            System.out.println(line);
                            break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("exception on connection," + this + " closing...");
            terminate();
            DatabaseNode.checkThreads();
        }
        System.out.println("connection closed on: "+this);
    }

    private void setValue(String arg) {
        int setKey, setValue;
        if(isClient){
            setKey = Integer.parseInt(arg.split(" ")[1].split(":")[0]);
            setValue = Integer.parseInt(arg.split(" ")[1].split(":")[1]);
        } else {
            setKey = Integer.parseInt(arg.split("/")[2].split(" ")[1].split(":")[0]);
            setValue = Integer.parseInt(arg.split("/")[2].split(" ")[1].split(":")[1]);
        }
        System.out.println("set-value for "+setKey+":"+setValue);
        if(DatabaseNode.getKey() == setKey){
            DatabaseNode.setValue(setValue);
            System.out.println("set-value successful, current record: "+DatabaseNode.getKey()+":"+DatabaseNode.getValue());
            if(isClient) {
                out.println("OK");
                terminate();
            }
            else out.println("REPLY/"+arg.split("/")[1]+"/OK");
        } else {
            System.out.println("key not on this node, searching...");
            countConnections();
            if(DatabaseNode.getConnections().size()<2) {
                if(isClient) {
                    out.println("ERROR");
                    terminate();
                }
                else out.println("REPLY/"+arg.split("/")[1]+"/ERROR");
                return;
            }

            ListIterator<ConnectionThread> connectionIter = DatabaseNode.getValidConnections().listIterator();
            ConnectionThread connection = connectionIter.next();
            while(connectionIter.hasNext() && connection == this){
                connection = connectionIter.next();
            }
            System.out.println("sending set-value to first on list: "+connection);
            if(isClient) connection.send("FORWARD/"+ip+":"+port+"set-value/"+arg, this, connectionIter);
            else connection.send(arg, this, connectionIter);
        }
    }

    private void getValue(String arg){
        int getKey;
        if(isClient) getKey = Integer.parseInt(arg.split(" ")[1]);
        else getKey = Integer.parseInt(arg.split("/")[2].split(" ")[1]);
        if(DatabaseNode.getKey() == getKey) {
            System.out.println("get-value successful");
            if(isClient) {
                out.println("" + DatabaseNode.getValue());
                terminate();
            }
            else out.println("REPLY/"+arg.split("/")[1]+"/"+DatabaseNode.getValue());
        } else {
            System.out.println("key not on this node, searching...");
            countConnections();
            if(DatabaseNode.getConnections().size()<2){
                if(isClient) {
                    out.println("ERROR");
                    terminate();
                }
                else out.println("REPLY/"+arg.split("/")[1]+"/ERROR");
                return;
            }
            ListIterator<ConnectionThread> connectionIter = DatabaseNode.getValidConnections().listIterator();
            ConnectionThread connection = connectionIter.next();
            while(connectionIter.hasNext() && connection == this){
                connection = connectionIter.next();
            }

            if (isClient) connection.send("FORWARD/"+ip+":"+port+"get-value/"+arg, this, connectionIter);
            else connection.send(arg, this, connectionIter);
        }
    }

    private void findKey(String arg){
        int getKey;
        if(isClient) getKey = Integer.parseInt(arg.split(" ")[1]);
        else getKey = Integer.parseInt(arg.split("/")[2].split(" ")[1]);

        if(DatabaseNode.getKey() == getKey){
            System.out.println("find-key successful");
            if(isClient) {
                out.println(socket.getLocalAddress().getHostAddress() + ":" + DatabaseNode.getListenOn());
                terminate();
            }
            else out.println("REPLY/"+arg.split("/")[1]+"/"+socket.getLocalAddress().getHostAddress()+":"+DatabaseNode.getListenOn());
        } else {
            System.out.println("key not on this node, searching...");
            countConnections();
            if(DatabaseNode.getConnections().size()<2){
                if(isClient) {
                    out.println("ERROR");
                    terminate();
                }
                else out.println("REPLY/"+arg.split("/")[1]+"/ERROR");
                return;
            }
            ListIterator<ConnectionThread> connectionIter = DatabaseNode.getValidConnections().listIterator();
            ConnectionThread connection = connectionIter.next();
            while(connectionIter.hasNext() && connection == this){
                connection = connectionIter.next();
            }

            if (isClient) connection.send("FORWARD/"+ip+":"+port+"find-key/"+arg, this, connectionIter);
            else connection.send(arg, this, connectionIter);
        }
    }

    private void getMax(String arg){
        String maxVal = DatabaseNode.getKey()+":"+DatabaseNode.getValue();
        if(DatabaseNode.getConnections().size() < 2){
            if(isClient) {
                out.println(maxVal);
                terminate();
            }
            else out.println("REPLY/"+arg.split("/")[1]+"/"+maxVal);
            return;
        }
        ListIterator<ConnectionThread> connectionIter = DatabaseNode.getValidConnections().listIterator();
        ConnectionThread connection = connectionIter.next();
        while(connectionIter.hasNext() && connection == this){
            connection = connectionIter.next();
        }

        if(isClient) connection.send("FORWARD/"+ip+":"+port+"get-max/"+arg, this, connectionIter);
        else connection.send(arg, this, connectionIter);
    }

    private void getMin(String arg){
        String minVal = DatabaseNode.getKey()+":"+DatabaseNode.getValue();
        if(DatabaseNode.getConnections().size() < 2){
            if(isClient) {
                out.println(minVal);
                terminate();
            }
            else out.println("REPLY/"+arg.split("/")[1]+"/"+minVal);
            return;
        }
        ListIterator<ConnectionThread> connectionIter = DatabaseNode.getValidConnections().listIterator();
        ConnectionThread connection = connectionIter.next();
        while(connectionIter.hasNext() && connection == this){
            connection = connectionIter.next();
        }
        if(isClient) connection.send("FORWARD/"+ip+":"+port+"get-min/"+arg, this, connectionIter);
        else connection.send(arg, this, connectionIter);
    }

    private void newRecord(String record){
        DatabaseNode.setKey(Integer.parseInt(record.split(":")[0]));
        DatabaseNode.setValue(Integer.parseInt(record.split(":")[1]));

    }

    private void terminateAll(){
        countConnections();
        for (ConnectionThread connection : DatabaseNode.getConnections()) {
            if(connection != this) connection.out.println("FORWARD//TERMINATING_ON_REQUEST");
        }
        out.println("OK");
        DatabaseNode.terminateNode();
    }

    public void send(String str, ConnectionThread author, ListIterator<ConnectionThread> iter) {
        communicationHandler.addRequest(str, author, iter);
        DatabaseNode.getCurrentRequests().add(str);
        out.println(str);
        System.out.println("sent "+str+" to "+this+", waiting for response...");
    }

    public void sendNext(String str){
        out.println(str);
        System.out.println("sent "+str+" to "+this+", waiting for response...");
    }

    public void sendReply(String str){
        if(isClient) {
            out.println(str.split("/")[2]);
            terminate();
        }
        else out.println(str);
    }

    public void terminate(){
        isTerminating=true;
        System.out.println("terminating " + this);
        out.close();
        try {
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void countConnections(){
        System.out.println("connected count: "+DatabaseNode.getConnections().size()+" (including current request client)");
    }

    @Override
    public String toString() {
        return "connection " + connectionID +
                ": ip='" + ip + '\'' +
                ", port=" + port;
    }
}

