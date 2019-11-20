/**
 * 만든이 : 이영명
 * 프로그램 설명: 안드로이드에서 작동하는 클라이언트 간에 중계 해주는 서버
 * 클라이언트에 정보를 제공해 주며 여러 클라이언트 중에 지정된 클라이언트에 정보를 보내준다.
 * */

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * -SocketList-
 * 서버에 들어오는 여러 클라이언트들의 소켓을 정보 관리
 * */
class SocketList{
    private List<Socket> socketList;

    public SocketList(){
        socketList = new ArrayList<Socket>();
    }

    /**
     * addSocket
     * 소켓을 받으면 리스트에 추가한다.*/
    public void addSocket(Socket socket){
        this.socketList.add(socket);
        System.out.println(connectSockets());
        new Thread(new IndividualThread(socket)).start();
    }

    /**
     * connectSockets
     * 현재 소켓 리스트에 있는 소켓을 제공해준다.
     * */
    public String connectSockets(){
        String tempList = null;

        for(Socket s : socketList){
            if(tempList == null) {
                tempList = new String();
                tempList += "{\"REFRESH\" : [{\"address\" : \""+ s.getInetAddress() + "\", \"port\" : \"" + s.getPort() + "\"}";
            }else {
                tempList += ", { \"address\" : \""+ s.getInetAddress() + "\", \"port\" : \"" + s.getPort() + "\"}";
            }
        }
        tempList += "]}";
        return tempList;
    }

    /**
     * -IndividualThread-
     * 멀티로 구성된 쓰레드 공간
     * 각각의 소켓을 가지고 있어야한다.
     */
    class IndividualThread implements Runnable{
        private Socket socket;

        public IndividualThread(Socket socket){
            this.socket = socket;
        }
        @Override
        public void run() {
            try{
                //소켓으로 데이터를 받아온다.
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter bw;
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject;
                JSONArray jsonArray;
                Socket tempSocket = null;

                while(true){
                    //소켓으로 받은 데이터를 제이슨으로 전환한다.
                    jsonObject = (JSONObject) jsonParser.parse(br.readLine());

                    try{
                        //refresh 요청이 오면 연결된 소켓 목록을 보내준다.
                        if(jsonObject.containsKey("REFRESH")){
                            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                            bw.write(connectSockets() + "\n");
                            bw.flush();
                        }else if(jsonObject.containsKey("HACKING")){
                            jsonArray = (JSONArray) jsonObject.get("HACKING");

                            //받은 소켓 포트로 소켓리스트에서 비교하여 정보를 보내준다.
                            for(Object o : jsonArray){
                                if(o instanceof JSONObject){
                                    for(Socket s : socketList){
                                        tempSocket = s;
                                        //JSONObject 에서 port 정보를 추출하여 비교한다.
                                        if(tempSocket.getPort() == Integer.valueOf((String) ((JSONObject) o).get("port"))){
                                            System.out.println(((JSONObject) o).toString());

                                            bw = new BufferedWriter(new OutputStreamWriter(tempSocket.getOutputStream()));
                                            bw.write(((JSONObject) o).toString() + "\n");
                                            bw.flush();
                                            break;
                                        }
                                    }
                                }else {
                                    System.out.println("it is not JSON");
                                }
                            }
                        }else{
                            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                            bw.write(jsonObject.toString() + "\n");
                            bw.flush();
                        }
                    }catch(Exception e){
                        //작동 도중에 에러시 처리
                        System.out.println(tempSocket.getPort() + "ERROR" + new Date());
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                //입출력 예외처리
            } catch (ParseException e) {
                //json parser 에외처리
            } finally{
                if(socket != null){
                    //연결을 끊은 크라이언트 소켓 제거
                    System.out.println(socket.getPort() + "연결 종료" + new Date());
                    socketList.remove(socket);
                }
            }
        }
    }
}


/**
 * Server
 * 서버 유지 및 데이터 처리
 * */
public class Server {
    public static void main(String args[]){
        SocketList socketList = new SocketList();

        try{
            ServerSocket serverSocket = new ServerSocket(22223);

            while(true){
                Socket socket = serverSocket.accept();
                System.out.println(socket.getPort() + "연결 " + new Date());

                socketList.addSocket(socket);
            }

        }catch(IOException e){
            e.printStackTrace();
        }

    }
}