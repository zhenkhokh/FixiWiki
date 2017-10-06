package ru.some.wm.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jwiki.core.Wiki;
import ru.some.wm.client.GreetingService;
import ru.some.wm.controller.Dto;
import ru.some.wm.model.WikiKit;

import com.google.gwt.user.server.rpc.RPCServletUtils;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {	
	public Dto action(Dto dto){
		Dto oDto;
		try{
			switch (dto.action){
				case MERGE: {oDto=merge(dto); break;} 
				case GO: {oDto=go(dto); break;}
				case CONNECT: {oDto=connect(dto); break;}
				default: {
					System.out.println("action result is null");
					return null;}
			}
		}catch (Throwable e){
			System.out.println(e.getMessage());
			return null;
		}
		System.out.println("action result is "+oDto);
		return oDto;
	}
	Dto merge(Dto dto) throws Throwable{
		System.out.println("hello merge");
		/*
		dto = new Dto();
		ArrayList<String> al = new ArrayList<String>(); al.add("File:fig1.png");
		dto.loadedImages.put("art1", al);		
		ArrayList<String> aL = new ArrayList<String>(); aL.add("File:fig2.png");aL.add("File:fig3.png");
		dto.unloadedImages.put("art1", aL);
		*/
		dto = WikiKit.getInstance().merge(dto.title.values());
		System.out.println(dto.toString());
		return dto;
	}
	Dto go(Dto dto) throws Throwable{
		System.out.println("hello go");
		/*dto = new Dto();
		dto.title.put("1","art1");
		dto.title.put("2","art1");
		dto.title.put("3","art2");
		dto.title.put("4","art2");		
		dto.ref.put("1", "http://localhost/w/index.php/Leonardo's robot");
		dto.ref.put("2", "http://en.wikipedia.org/w/index.php/Leonardo's robot");
		dto.ref.put("3", "https://localhost/w/index.php/Leonardo_da_Vinci");
		dto.ref.put("4", "https://en.wikipedia.org/w/index.php/Leonardo_da_Vinci");
		*/
		dto = WikiKit.getInstance().go(dto.sPhrase);
		System.out.println(dto.toString());
		return dto;
	}	
	Dto connect(Dto dto) throws Throwable{
		System.out.println("hello connect");
		System.out.println(dto.toString());
		//null if connection id failure
		if (!WikiKit.getInstance().initConnection(dto.login, dto.password, dto.domain)){
			dto.login = null;
			dto.password = null;
			dto.domain = null;
		}
		System.out.println(dto.toString());
		return dto;
	}

	public String greetServer(String input) throws IllegalArgumentException {

		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");

		// Escape data from the client to avoid cross-site script vulnerabilities.
		input = escapeHtml(input);
		userAgent = escapeHtml(userAgent);
        URL u=null;
        try {
			u = new URL("https://ya.ru");
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			InputStreamReader isr  = new InputStreamReader(u.openStream());
			StringBuffer respMsg = new StringBuffer();
	        int b = isr.read();
	        while(b!=-1){
	                respMsg.append((char)b);
	                b = isr.read();
	        }
			System.out.println("------"+respMsg);
			//System.out.println("------"+RPCServletUtils.readContentAsGwtRpc(getThreadLocalRequest()));
		    byte[] responseBytes = respMsg.toString().getBytes(Charset.forName("UTF-8"));
		    System.out.println("------ 1");
		    //getThreadLocalResponse().getOutputStream().write(responseBytes);
		    System.out.println(getThreadLocalRequest().getContextPath()+input);
		    //getThreadLocalResponse().sendRedirect(input);

		    //RPCServletUtils.writeResponse(getServletContext(), getThreadLocalResponse(), respMsg.toString(), false);
		    System.out.println("------ 2");
			if (isr!=null)
				isr.close();
		    System.out.println("------ 3");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("wtite something");
			e.printStackTrace();
		}
/*
        PrintWriter out;
        URL u=null;
		try {
			out = getThreadLocalResponse().getWriter();
			//res.setCharacterEncoding("UTF-8");
	        getThreadLocalRequest().setCharacterEncoding("UTF-8");
	        String url = getThreadLocalRequest().getParameter("url"); 
	        
	        u = new URL("http://ya.ru");
	        InputStream is = u.openStream();
	        int b = is.read();
	        while(b!=-1){
	                out.append((char)b);
	                b = is.read();
	        }
	        out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/
	    System.out.println("------ 4");

		return "Hello, " + input + "!<br><br>I am running " + serverInfo
				+ ".<br><br>It looks like you are using:<br>" + userAgent
				//+ u.toString()
				;
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}

}
