package com.fileupload.healthcheck.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.fileupload.healthcheck.message.HealthCheckMessage;
import com.fileupload.propertyreader.PropertyMap;
import com.google.gson.Gson;

/**
 * A handler thread class.  Handlers are spawned from the listening
 * loop and are responsible for a dealing with a single client
 * and broadcasting its messages.
 */
public class ServerThread extends Thread{

	private static final Logger LOG = Logger.getLogger(ServerThread.class.getName());
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private Gson gson;

	/**
	 * Constructs a handler thread, squirreling away the socket.
	 * All the interesting work is done in the run method.
	 */
	public ServerThread(Socket accept) {
		this.socket = accept;
	}

	/**
	 * Services this thread's client by repeatedly requesting a
	 * screen name until a unique one has been submitted, then
	 * acknowledges the name and registers the output stream for
	 * the client in a global set, then repeatedly gets inputs and
	 * broadcasts them.
	 */

	public void run() {
		try {
			LOG.info("Created one connection");
			// Create character streams for the socket.
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			gson = new Gson();

			// Accept messages from this client and broadcast them.
			// Ignore other clients that cannot be broadcasted to.
			while (true) {
				String input = in.readLine();
				if (input == null) {
					continue;
				}
				HealthCheckMessage healthCheckMessage = gson.fromJson(input, HealthCheckMessage.class);
				LOG.info("Message Received "+ healthCheckMessage);
				//Setting if any new server comes up
				PropertyMap.getInstance().setSeedNodesByPort(healthCheckMessage.getMySeedNodes());
				out.println(healthCheckMessage);
				LOG.info("Message Sent "+ healthCheckMessage);
			}
		} catch (IOException e) {
			LOG.error(e);
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				LOG.error(e);
			}
		}
	}
}
