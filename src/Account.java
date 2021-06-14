import java.io.Serializable;

public class Account implements Serializable {
	/**
	 * Default Serializable
	 */
	private static final long serialVersionUID = 2L;
	private int id;
	private int[] pin;
	private float balance;
	
	public Account() {
		
	}
	
	public Account(int id, int[] pin, float balance) {
		super();
		this.id = id;
		this.pin = pin;
		this.balance = balance;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int[] getPin() {
		return pin;
	}

	public void setPin(int[] pin) {
		this.pin = pin;
	}

	public float getBalance() {
		return balance;
	}

	public void setBalance(float balance) {
		this.balance = balance;
	}
	
	public void withdraw(float amt) {
		this.balance -= amt;
	}
	
	public void deposit(float amt) {
		this.balance += amt;
	}
}
