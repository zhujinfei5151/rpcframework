package com.gomo.rpcframework.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.gomo.rpcframework.core.RPCConfig;
import com.gomo.rpcframework.util.ByteUtil;

public class Connection {

	Socket socket;
	OutputStream outputStream;
	InputStream inputStream;
	String host;
	int port;

	public Connection(String host, int port) {
		this.host = host;
		this.port = port;
		init();
	}

	private void init() {
		try {
			socket = new Socket(host, port);
			outputStream = socket.getOutputStream();
			inputStream = socket.getInputStream();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void refresh() {
		close();
		init();
	}

	public String call(String data) {
		try {
			byte[] dataByte = data.getBytes(RPCConfig.ENCODE);
			outputStream.write(RPCConfig.FLAG);
			outputStream.write(ByteUtil.toByteArray(dataByte.length));
			outputStream.write(dataByte);
			outputStream.flush();

			int index;
			byte blenght[] = new byte[4];

			index = inputStream.read(blenght);

			if (index != 4) {
				System.err.println("not found length flag  with 4 byte");
			}

			int responseLength = ByteUtil.toInt(blenght);
			byte pkg[] = new byte[responseLength];
			index = inputStream.read(pkg);
			if (index != responseLength) {
				System.err.println("长度不匹配" + new String(pkg, 0, index));
			}
			return new String(pkg, 0, index);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void close() {
		try {
			outputStream.close();
		} catch (Exception e) {
		}
		try {
			inputStream.close();
		} catch (Exception e) {
		}
		try {
			socket.close();
		} catch (Exception e) {
		}
	}
}
