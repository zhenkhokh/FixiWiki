package ru.some.wm.client;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ru.some.wm.controller.ACTION;
import ru.some.wm.controller.Dto;

import com.google.gwt.user.client.Window;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Wm implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";
	private static final String UNEXPECTED_SERVER_ERROR =
			"Произошел где-то сбой на сервере, процесс не завершен";
	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	Dto model = new Dto();
	
	TextField login = new TextField();
	PasswordTextBox passFld = new PasswordTextBox();
	TextField domainFld = new TextField();
	TextField goFld = new TextField();

	//ListBox appList = new ListBox();
	Button conBut = new Button("Соединиться");
	Button goBut = new Button("Вперед!!");
	Button mergeBut = new Button("СкачатьВсе");

	Tree tree = new Tree();
	
	Label loginLabel = new Label("Логин"); 
	Label passFldLabel = new Label("Пароль");
	Label domainFldLabel = new Label("Домен");

	Label textToServerLabel = new Label();
	HTML serverResponseLabel = new HTML();
	Button closeButton = new Button("Закрыть");
	Button okButton = new Button("Продолжить");
	DialogBox dialogBox = initDialog(textToServerLabel,
			serverResponseLabel,closeButton,okButton);
{	
	//model.id = -1;
	model.login = null;
	model.password = null;
	model.domain = null;
	
	RootPanel.get("nameFieldContainer").add(login);
	RootPanel.get("nameFieldContainer").add(loginLabel);

	RootPanel.get("nameFieldContainer").add(passFld);
	RootPanel.get("nameFieldContainer").add(passFldLabel);
	
	RootPanel.get("nameFieldContainer").add(domainFld);
	RootPanel.get("nameFieldContainer").add(domainFldLabel);
	
	RootPanel.get("nameFieldContainer").add(conBut);
	RootPanel.get("nameFieldContainer").add(tree);
	
	RootPanel.get("sendButtonContainer").add(goFld); 
	RootPanel.get("sendButtonContainer").add(goBut);
	RootPanel.get("sendButtonContainer").add(mergeBut);
}
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				setEnabled(true);
			}
		});
		// add Handler to merge articles
		okButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				//action(ACTION.MERGE);
				dialogBox.hide();
				UIObject.setVisible(closeButton.getElement(), false);
				UIObject.setVisible(okButton.getElement(), false);
				serverResponseLabel.setHTML("Процесс ...");
				dialogBox.center();
				Dto dto = model;// .this.setAction(a);// setAction(a);
				//Dto dto = null;
				greetingService.action(dto, new AsyncCallback<Dto>() {					
					@Override
					public void onSuccess(final Dto result) {
						dialogBox.hide();
						UIObject.setVisible(closeButton.getElement(), true);
						if (result!=null){
							if (result.isMergeDone()){
								mergeResponseUpdate(result);
								serverResponseLabel.setHTML(getUnloadedImages());
							}else{
								errorMsg("Неудачная попытка копирования");
							}
						}
						else{
							errorMsg(UNEXPECTED_SERVER_ERROR);
						}
						dialogBox.center();
						setEnabled(true);
							
					}
					@Override
					public void onFailure(Throwable caught) {
						dialogBox.hide();
						UIObject.setVisible(closeButton.getElement(), true);
						dialogBox
							.setText("Remote Procedure Call - Failure");
						errorMsg(SERVER_ERROR);
						dialogBox.center();
					}
				});
			}
		});

		// Create a handler for the goBut and nameField
		class Handler implements ClickHandler, KeyUpHandler {
			ACTION action;
			public Handler(ACTION a){
				action = a;
			}
			/**
			 * Fired when the user clicks on the goBut.
			 */
			public void onClick(ClickEvent event) {
				action(action);
			}

			/**
			 * Fired when the user types in the nameField.
			 */
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					action(action);
				}
			}
		}
		// Add a handler to send the name to the server
		Handler goHandler = new Handler(ACTION.GO);
		Handler conHandler = new Handler(ACTION.CONNECT);
		Handler mergeHandler = new Handler(ACTION.MERGE);
		conBut.addClickHandler(conHandler);
		login.addKeyUpHandler(conHandler);
		passFld.addKeyUpHandler(conHandler);
		domainFld.addKeyUpHandler(conHandler);
		
		goBut.addClickHandler(goHandler);
		goFld.addKeyUpHandler(goHandler);
		
		mergeBut.addClickHandler(mergeHandler);
		
		tree.addSelectionHandler(new SelectionHandler<TreeItem>() {
			@Override
			public void onSelection(SelectionEvent<TreeItem> event) {
				String ref = tree.getSelectedItem().getText();
				// filter titles
				if (ref.contains("http"))
					com.google.gwt.user.client.Window.Location.assign(ref);
			}
		});
	}
	void setEnabled(boolean v){
		goBut.setEnabled(v);
		conBut.setEnabled(v);
		mergeBut.setEnabled(v);
	}
	Dto setAction(ACTION a){
		updateModel();
		Dto dto = model;
		dto.action = a;		
		return dto;
	}
	void updateModel(){
		model.domain = domainFld.getText();
		model.login = login.getText();
		model.password = passFld.getText();
		model.sPhrase = goFld.getText();
	}

	void action(final ACTION a) {

		setEnabled(false);
		final Dto dto = setAction(a);
		final String dialogHead,dialogResp;
		UIObject.setVisible(closeButton.getElement(), false);
		UIObject.setVisible(okButton.getElement(), false);
		switch (a){
			case MERGE: {
				dialogHead="Начало скачивания всех найденых статей";
				UIObject.setVisible(closeButton.getElement(), true);
				UIObject.setVisible(okButton.getElement(), true);
				dialogResp="<b>Внимание:</b> все найденные статьи будут перекопированы с \""+
	model.domain+"\" на локальный хост. Это не коснется текста и загруженных изображений "
					+ "уже существующих локальных статей, "
					+ "названия которых совпадает с названиями на удаленной wiki. В "
					+ "случае не загруженных рисунков необходимо загрузить их вручную : для"
					+ "этого они помещаются в /tmp/wikiImages/[articleName]/[imageName], "
					+ "где imageName имеет прифекс \"File:\", "
					+ "на локальном хосте и далее нужно повторить копирование статей(и).";
				break;}
			case GO: {
				dialogHead="Поиск статей ...";
				dialogResp="Процесс ...";
				break;}
			case CONNECT: {
				dialogHead="Соединение с " +domainFld.getText()+" ...";
				dialogResp="Процесс ...";
				break;}
			default: dialogHead=null; dialogResp=null;// for compiler only
		}
		dialogBox.setText(dialogHead);
		serverResponseLabel.setHTML(dialogResp);
		serverResponseLabel
		.removeStyleName("serverResponseLabelError");
		dialogBox.center();
		closeButton.setFocus(true);
		if (a!=ACTION.MERGE){ // do action if and inly if clicking oK
			greetingService.action(dto, 
					new AsyncCallback<Dto>() {
						public void onFailure(Throwable caught) {
							// Show the RPC error message to the user
							dialogBox
									.setText("Remote Procedure Call - Failure");
							errorMsg(SERVER_ERROR);
						}	
						public void onSuccess(Dto result) {
							if (result!=null){
								if (a==ACTION.GO){
									if (result.isTitlesOfartcliesReady()){
										dialogBox.hide();
										goResponseUpdate(result);
										udateTree();
									}else{
										errorMsg("Неудачная попытка поиска");
									}
								}
								if (a==ACTION.CONNECT){
									if (result.isConnected()){
										dialogBox.hide();
										connectResponseUpdate(result);
									}else{
										errorMsg("Неудачная попытка соединения");
									}
								}
								setEnabled(true);
							}else{
								errorMsg(UNEXPECTED_SERVER_ERROR);
							}
						}
					});
		}
	}
	//model.title must be sorted
	void udateTree(){
		String prevSS = null;
		TreeItem prev = null;
		tree.removeItems();
		//String ss = (String) iterator.next();
		for (Integer i=0;i<model.ref.size();i++){	
			String ss = model.title.get(String.valueOf(i+1));
			TreeItem item = null;
			if(prevSS==null||!prevSS.equalsIgnoreCase(ss)){ 
				prevSS = ss;
				item = new TreeItem();
				prev = item;
				//item.setStyleName("header");
				item.setText(ss);
				tree.addItem(item);
				//tree.add(t1);
			}else{
				item = prev;
			}
			TreeItem item1 = new TreeItem();
			item.addItem(item1);
			String refName = (String) model.ref.get(String.valueOf(i+1));
			item1.setText(refName);
		}
		for (int i=0; i<tree.getItemCount();i++){
			TreeItem item = tree.getItem(i);
			item.setStyleName("refs");
			item.setState(true); // open top level items
		}
	}
	String getUnloadedImages(){
		//HashSet<String> titles = (HashSet)model.title.keySet();// can't use it, try use gwt HashSet 
		StringBuilder sb = new StringBuilder();
		int i=1;
		sb = sb.append("<b>Список незагруженных файлов:</b><br>");
		//sb = sb.concat("<b>Список незагруженных файлов:</b><br>");
		for (Iterator iterator = model.unloadedImages.keySet().iterator(); iterator.hasNext();) {
			String title = (String) iterator.next();
			sb = sb.append(i+")"+title+": ").append(model.unloadedImages.get(title).toString()+"<br>");
			i++;
		}
		sb.append("<b>Список нескаченных файлов:</b><br>");
		i=1;
		for (Iterator iterator = model.unDownloadedImages.keySet().iterator(); iterator.hasNext();) {
			String title = (String) iterator.next();//model.title.get(String.valueOf(i));
			sb = sb.append(i+")"+title+": ").append(model.unDownloadedImages.get(title).toString()+"<br>");
			i++;
		}
		sb.append("<b>Список загруженных файлов:</b><br>");
		i=1;
		for (Iterator iterator = model.loadedImages.keySet().iterator(); iterator.hasNext();) {
		//for(i=1;i<=model.loadedImages.size();){
			String title = (String) iterator.next();//model.title.get(String.valueOf(i));
			sb = sb.append(i+")"+title+": ").append(model.loadedImages.get(title).toString()+"<br>");
			//sb = sb.concat(i+")"+title+": ")
			//		.concat(model.loadedImages.get(title).toString()+"<br>");
			i++;
		}

		return sb.toString();
	}
	void goResponseUpdate(final Dto dto){
		model.invRef = dto.invRef;
		model.ref = dto.ref;
		model.title = dto.title;
	}
	void mergeResponseUpdate(final Dto dto){
		model.unloadedImages = dto.unloadedImages;
		model.loadedImages = dto.loadedImages;
		model.unDownloadedImages = dto.unDownloadedImages;
	}
	void connectResponseUpdate(final Dto dto){
		model.login = dto.login;
		model.password = dto.password;
		model.domain = dto.domain;
	}
	DialogBox initDialog(Label textToServerLabel,HTML serverResponseLabel,Button closeButton,Button okButton){
		// Create the popup dialog box
		DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Процесс");
		dialogBox.setAnimationEnabled(true);
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		closeButton.setVisible(false);
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		//dialogVPanel.add(new HTML("<b>Отправлено на север:</b>"));
		dialogVPanel.add(textToServerLabel);
		//dialogVPanel.add(new HTML("<br><b>Ответ сервера:</b>"));
		dialogVPanel.add(serverResponseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogVPanel.add(okButton);
		dialogBox.setWidget(dialogVPanel);
		return dialogBox;
	}
	void errorMsg(String error){
		
		serverResponseLabel
		.addStyleName("serverResponseLabelError");
		serverResponseLabel.setHTML(error);
		UIObject.setVisible(closeButton.getElement(), true);
		//dialogBox.center();
		
		//Window.alert(error);
	}
}
