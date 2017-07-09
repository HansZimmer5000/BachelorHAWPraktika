package Models;

import java.util.ArrayList;

public class User {

	private String name, mail, password;
	private ArrayList<Account> accountList;
	
	public User(String name, String mail, String password, ArrayList<Account> accountList){
		this.name = name;
		this.mail = mail;
		this.password = password;
		this.accountList = accountList;
	}
	
	public void addAccount(String mail, String password, String host, int port){
		Account acc = new Account(mail,password,host,port);
		addAccount(acc);
	}
	
	public void addAccount(Account acc){
		accountList.add(acc);
	}

	
	/*//////////////////
	 * GETTER & SETTER
	 *//////////////////
	public String getName(){return this.name;}
	public String getMail(){return this.mail;}
	public String getPassword(){return this.password;}
	public ArrayList<Account> getAccounts(){return this.accountList;}
}
