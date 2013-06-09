package objects;


public class RoomObject{
	
	String roomName;
	String ip;
	int port;
	
	public RoomObject(String room, String ip){
		//port = 54308;
		roomName = room;
		this.ip = ip;
	}
	public String getRoomName(){
		return roomName;
	}
	public String getIP(){ return ip; }
}
