import java.io.Serializable;
import java.util.ArrayList;

public class Profile implements Serializable {
	/**
	 * Default Serializable
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String password;
	private String name;
	private ArrayList<Account> accounts;
	
	public Profile() {
		
	}

	public Profile(int id, String password, String name, ArrayList<Account> accounts) {
		super();
		this.id = id;
		this.password = password;
		this.name = name;
		this.accounts = accounts;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(ArrayList<Account> accounts) {
		this.accounts = accounts;
	}
	
	public void listAccounts() {
		for(Account i: accounts) {
			System.out.println("*");
			System.out.println("| Account [" + i.getId() + "] ");
			System.out.println("| Balance: $" + i.getBalance());
		}
	}
	
}
