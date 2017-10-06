package ru.some.wm.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Dto implements Serializable, IsSerializable{
	public String login;									// go to RMI
	public String password;
	public String domain;
	public String sPhrase;
	public ACTION action;								// go or merge
	// preview
	public HashMap<String, String> ref = new HashMap<String, String>();				// model: <userKey,refName>
	public HashMap<String, String> invRef = new HashMap<String, String>();			// for getting userkey <refName,userKey>
	public HashMap<String, String> title = new HashMap<String, String>();			// <userKey,title>
	// merging response from server
	public HashMap<String, ArrayList<String>> unloadedImages = 
			new HashMap<String, ArrayList<String>>();								// <title,images>
	public HashMap<String, ArrayList<String>> loadedImages = 
			new HashMap<String, ArrayList<String>>();								// <title,images>
	public HashMap<String, ArrayList<String>> unDownloadedImages = 
			new HashMap<String, ArrayList<String>>();								// <title,images>
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb = sb.append("login,password,domain,sPhrase,action are "+
				login+","+password+","+domain+","+sPhrase+","+sPhrase+","+action);
		sb = sb.append("ref is "+ref);
		sb = sb.append("invRef is "+invRef);
		sb = sb.append("title is "+title);
		sb = sb.append("unloadedImages are "+unloadedImages);
		sb = sb.append("loadedImages are "+loadedImages);
		sb = sb.append("unDownloadedImages are "+unDownloadedImages);
		return sb.toString();
	}
	public boolean isConnected(){
		return login!=null&&domain!=null&&password!=null;
	}
	public boolean isTitlesOfartcliesReady(){
		return !title.isEmpty()&&title.size()==ref.size();
	}
	public boolean isMergeDone(){
		return loadedImages!=null&&unloadedImages!=null;
	}
}