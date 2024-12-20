import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

public class CommunicationHandler {

    public class Request{

        ConnectionThread author;
        boolean isOK = false;
        boolean isReady = false;

        String forwardString;

        String currentMin = DatabaseNode.getKey()+":"+DatabaseNode.getValue();
        String currentMax = DatabaseNode.getKey()+":"+DatabaseNode.getValue();

        ListIterator<ConnectionThread> nextSend;

        public Request(ConnectionThread author, String forwardString, ListIterator<ConnectionThread> iter) {
            this.author = author;
            nextSend = iter;
            this.forwardString = forwardString;
        }

        @Override
        public String toString() {
            return "Request{" +
                    "author=" + author +
                    ", isOK=" + isOK +
                    '}';
        }
    }

    private final Map<String, Request> requestAuthorMap = new HashMap<String, Request>();

    public void addRequest(String request, ConnectionThread author, ListIterator<ConnectionThread> iter){
        requestAuthorMap.put(request.split("/")[1], new Request(author, request, iter));
    }

    public Request getRequest(String reply){
        return requestAuthorMap.get(reply.split("/")[1]);
    }

    public void setOK(String reply){
        Request request = requestAuthorMap.get(reply.split("/")[1]);
        request.isOK = true;
    }

    public void removeRequest(String reply){
        requestAuthorMap.remove(reply.split("/")[1]);
    }

    public ConnectionThread getAuthor(String reply){
        requestAuthorMap.forEach((key, val) -> System.out.println(key +":"+val));
        String requestConnection = reply.split("/")[1];
        return requestAuthorMap.get(requestConnection).author;
    }

    public boolean handleReply(String reply){
        Request request = getRequest(reply);
        ConnectionThread connection = null;
        if(!request.nextSend.hasNext()) request.isReady = true;
        else {
            connection = request.nextSend.next();
            while(connection == request.author && request.nextSend.hasNext()){
                connection = request.nextSend.next();
            }
        }
        if(!reply.contains("get-max") && !reply.contains("get-min")){
            if(!reply.contains("ERROR")) {
                setOK(reply);
                request.isReady= true;
            }
            if(request.isReady){
                System.out.println("reply for ["+reply.split("/")[1]+"] is ready, sending...");
                request.author.sendReply(request.isOK ? reply.replace("ERROR", "OK") : reply);
                DatabaseNode.getCurrentRequests().remove(reply);
                removeRequest(reply);
                return true;
            }
        } else {
            String valStr = reply.split("/")[2];
            int val = valStr.equals("ERROR") ? DatabaseNode.getValue() : Integer.parseInt(valStr.split(":")[1]);
            if (reply.contains("get-max")){
                System.out.println("get-max extreme, compare "+request.currentMax + " and "+val);
                if (val > Integer.parseInt(request.currentMax.split(":")[1])) {
                    request.currentMax = valStr;
                }
                if(request.isReady){
                    System.out.println("reply for ["+reply.split("/")[1]+"] is ready, sending...");
                    getAuthor(reply).sendReply("REPLY/"+reply.split("/")[1]+"/"+ request.currentMax);
                    removeRequest(reply);
                    return true;
                }
            } else if (reply.contains("get-min")){
                if (val < Integer.parseInt(request.currentMin.split(":")[1])) {
                    request.currentMin = valStr;
                }
                if(request.isReady){
                    System.out.println("reply for ["+reply.split("/")[1]+"] is ready, sending...");
                    getAuthor(reply).sendReply("REPLY/"+reply.split("/")[1]+"/"+ request.currentMin);
                    removeRequest(reply);
                    return true;
                }
            }
        }
        connection.sendNext(request.forwardString);
        return false;
    }
}
