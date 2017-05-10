package com.fileupload.service;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class Test {
	public static void main(String[] args) throws SocketException {

		  InetAddress ip;
		  try {

			ip = InetAddress.getLocalHost();
			System.out.println("Current IP address : " + ip.getHostAddress());

		  } catch (UnknownHostException e) {

			e.printStackTrace();

		  }
		  
		  Enumeration e = NetworkInterface.getNetworkInterfaces();
		  System.out.println("e->"+e);
		  while(e.hasMoreElements())
		  {
		      NetworkInterface n = (NetworkInterface) e.nextElement();
		      System.out.println("n->"+n);
		      Enumeration ee = n.getInetAddresses();
		      System.out.println("ee->"+ee);
		      while (ee.hasMoreElements())
		      {
		          InetAddress i = (InetAddress) ee.nextElement();
		          System.out.println("i->"+i.getHostAddress());
		      }
		  }

		}
}
