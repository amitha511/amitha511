package test;


import test.Commands.DefaultIO;
import test.Server.ClientHandler;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class AnomalyDetectionHandler implements ClientHandler{


	@Override
	public void handleClient(InputStream inFromClient, OutputStream outToClient) {
		SocketIO socketIO = new SocketIO(inFromClient,outToClient);
		CLI obj = new CLI(socketIO);
		obj.start();
		obj.dio.write("bye\n");
		socketIO.close();
	}

	public class SocketIO implements DefaultIO {

		BufferedReader in;
		PrintWriter out;
		public SocketIO(InputStream inFromClient,OutputStream outToClient) {
				in = new BufferedReader(new InputStreamReader(inFromClient));
				out = new PrintWriter(outToClient, true);

		}

		@Override
		public String readText() {
			try {
				return in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public void write(String text) {
			out.print(text);
			out.flush();
		}

		@Override
		public float readVal() {
			try {
				return (float)in.read();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return 0;
		}

		@Override
		public void write(float val) {
			out.print(val);
		}

		public void close() {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			out.close();
		}
	}

}
