package ru.some.wm.model;
import java.io.File;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.text.Collator;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import javax.security.auth.login.LoginException;

import ru.some.wm.controller.Dto;
import jwiki.core.WTask;
import jwiki.core.Wiki;

import java.util.ArrayList;

public class WikiKit{
	static WikiKit wikiKit = null;
	Wiki wiki = null;
	Wiki wikiLoc = null;
	String domain = null;
	final String figs = "/tmp/wikiImages/";
	
	private WikiKit(){
		File dir = new File(figs);
		if(!dir.isDirectory())
			dir.mkdir();
	}
	static public WikiKit getInstance(){
		if (wikiKit==null){
			wikiKit = new WikiKit();
		}
		return wikiKit;
	}
	/*
	 * @return true if ok, other false 
	 */
	public boolean initConnection(String login,String pass,String domain){
		try{
        	//System.setProperty("javax.net.ssl.trustStore", "/home/zheka/jssecacerts");// use server jvm options instead, or copy jssasert instead cacert.jks to gfRoot/config and gfRoot/domains/domain1/config
			wiki = new Wiki(login,pass,domain);
			wikiLoc = new Wiki("TestUser", "1", "localhost");
			this.domain = domain;
			return true;
		}catch (LoginException e){
			return false;
		}
	}
	public Dto go(String phrase){
		Dto dto = new Dto();
		ArrayList<String> pages = wiki.allPages(phrase, false, -1, null);
		ArrayList<String> lPages = wikiLoc.allPages(phrase, false, -1, null);
		ArrayList<String> allPages = (ArrayList<String>) pages.clone();
		final Collator collator = Collator.getInstance(Locale.getDefault());
		final int EQUAL = 0; 
		allPages.addAll(lPages);
		Comparator c = new Comparator<String>() {
			public int compare(String o1, String o2) {
				return collator.compare(o1, o2);
			};
		};		

		//make all elements unique
		String prevPage=null;
		ArrayList<String> uniqueAllPages = new ArrayList<String>(new HashSet<String>(allPages));
		//sort
		uniqueAllPages.sort(c);

		pages.sort(c);
		lPages.sort(c);

		int key = 1, i1 = 0, i2 = 0;
		for (Iterator iterator = uniqueAllPages.iterator(); iterator.hasNext();) {
			String page = (String) iterator.next();
			String ref = null;
			if (i1<lPages.size())
				if (collator.compare(lPages.get(i1),page)==EQUAL){
					dto.title.put(String.valueOf(key), page);
					ref = "https://localhost/w/index.php/"+page;//zhenkhokh.ddns.net or evgenii.ddns.net
					dto.invRef.put(ref,String.valueOf(key));
					dto.ref.put(String.valueOf(key++),ref);
					i1++;
				}
			if (i2<pages.size())
				if (collator.compare(pages.get(i2),page)==EQUAL){
					dto.title.put(String.valueOf(key), page);
					ref = "https://"+domain+"/w/index.php/"+page;
					dto.invRef.put(ref,String.valueOf(key));
					dto.ref.put(String.valueOf(key++),ref);
					i2++;
				}
		}
		return dto;
	}
	public Dto merge(Collection<String> titles){
		Dto dto = new Dto();
		dto.domain = domain;
		for (Iterator iterator = titles.iterator(); iterator.hasNext();) {
			String title = (String) iterator.next();
			if (!wikiLoc.exists(title))
				wikiLoc.edit(title
						, wiki.getPageText(title).replace("Файл:", "File:Файл-") //TODO fix wiki
						,"копирование с https://"+domain+"/w/index.php/"+title);
			String pageDirPath = figs+title;
			File dir = new File(pageDirPath);
			if (!dir.isDirectory())
				dir.mkdir();
			ArrayList<String> images=wiki.getImagesOnPage(title);
			ArrayList<String> upFiles = new ArrayList<String>();
			ArrayList<String> nonUpFiles = new ArrayList<String>();
			ArrayList<String> undwnFiles = new ArrayList<String>();
			for (Iterator iterator2 = images.iterator(); iterator2.hasNext();) {
				String image = (String) iterator2.next();
				String imagePath = pageDirPath+"/"+image;
				File file = new File(imagePath);
				boolean existImg = wikiLoc.exists(image);
				if (!existImg && !Files.isReadable(file.toPath()))
					if (!WTask.downloadFile(image, imagePath, wiki)){
						//String image_ = image.replace(" ", "_"); //do not help
						//System.out.println("try download for other url ");
						//if (!WTask.downloadFile(image_, imagePath, wiki))
							undwnFiles.add(image);
					}else
					System.out.println("Do not download: file is exist on "+imagePath);
				if (!existImg){
					if (wikiLoc.upload(file.toPath(), image, "", "download from https://"+domain+"/w/index.php/"+title))
						upFiles.add(image);
					else
						nonUpFiles.add(image);
				}else {
					upFiles.add(image);
					System.out.println("file "+imagePath+" is uploaded ");
				}
			}
			dto.unDownloadedImages.put(title, undwnFiles);
			dto.loadedImages.put(title, upFiles);
			dto.unloadedImages.put(title, nonUpFiles);
		}
		return dto;
	}
}
