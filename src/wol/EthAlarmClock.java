package wol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class EthAlarmClock {
	private final int WOL_PORT = 9;
	private final int MAC_SIZE = 6;
	private final int STR_MAC_SIZE = (MAC_SIZE * 2) + (MAC_SIZE - 1);
	
	private DatagramSocket connection;
	
	public EthAlarmClock(boolean lan) throws SocketException {
		connection = new DatagramSocket();
		connection.setBroadcast(lan);
	}
	
	public void wakeUp(String host, String macAddress) throws MacAddressFormatException, IOException {
		byte[] payload = createPayload(macAddress);
		
		DatagramPacket packet = new DatagramPacket(payload, payload.length);
		packet.setSocketAddress(new InetSocketAddress(host, WOL_PORT));
		
		connection.send(packet);
	}
	
	private byte[] createPayload(String macAddress) throws MacAddressFormatException {
		byte[] mac = macStrToBytes(macAddress);
		byte[] bytes = new byte[MAC_SIZE * 17];
		int i;
		
		for(i = 0 ; i < MAC_SIZE ; i++) bytes[i] = (byte) 0xFF;
		for(int offset = i ; i < bytes.length ; i++) bytes[i] = mac[(i - offset) % mac.length];
		
		return bytes;
	}
	
	private String[] parseMacString(String macAddress) throws MacAddressFormatException {
		if(macAddress.length() != STR_MAC_SIZE)
			throw new MacAddressFormatException("MAC address is not right sized");
		
		String[] splitted = macAddress.split(":");
		
		if(splitted.length != MAC_SIZE)
			throw new MacAddressFormatException("MAC address does not contains six Hexadecimal bytes separated by \":\"");
		
		return splitted;
	}
	
	private byte[] macStrToBytes(String macAddress) throws MacAddressFormatException {
		String[] splitted = parseMacString(macAddress);

		try {
			byte[] bytes = new byte[MAC_SIZE];
			
			for(int i = 0 ; i < MAC_SIZE ; i++) {
				bytes[i] = Byte.parseByte(splitted[i]);
			}
			
			return bytes;
		}
		catch(NumberFormatException e) {
			throw new MacAddressFormatException("MAC address is not composed of Hexadecimal symbols");
		}
	}
}
