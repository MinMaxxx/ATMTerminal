import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Application {
	private static Scanner in = new Scanner(System.in);
	private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	
	private static ArrayList<Account> Accounts = new ArrayList<Account>();
	private static ArrayList<Profile> Profiles = new ArrayList<Profile>();
	
	private static int centralAccountID = 1000;
	private static int centralUserID = 100;
	
	private static Profile currentUser = null;
	private static boolean pinSession = false;
	private static boolean debug = false;
	
	public static void main(String[] args) {
		try {
			loadObjects();
			menu();
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("System Error, Restarting...");
			currentUser = null;
			pinSession = false;
			menu();
		}
	}
	
	public final static void writeObjects() throws IOException {
		File folder = new File("profiles");
		if(!folder.isDirectory()) {
			folder.mkdir();
		}
		
        for(Profile p: Profiles) {
        	String filename = "profiles/profile" + p.getId() +  ".ser";
        	FileOutputStream file = new FileOutputStream(filename); 
        	ObjectOutputStream out = new ObjectOutputStream(file);
        	out.writeObject(p);
        	if(debug) System.out.println("Object Saved: Profile ID: " + p.getId() + " Saved As " + filename);
        	out.close();
        	file.close();
        }
	}
	
	public final static void loadObjects() throws IOException {
		File path = new File("profiles");
		File[] files = path.listFiles();
		
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				Profile p = null;
				FileInputStream file = new FileInputStream(path.getName() + "/" + files[i].getName()); 
			    ObjectInputStream in = new ObjectInputStream(file);
			    
			    try {
			    	p = (Profile) in.readObject();
				    Profiles.add(p); 
				    centralUserID = p.getId() + 1;
					if(debug) System.out.println("Object Loaded: Profile ID: " + p.getId() + " From File " + path.getName() + "/" + files[i].getName());
				    for(Account a: p.getAccounts()) {
				    	Accounts.add(a);
				    	centralAccountID = a.getId() + 1;
				    	}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			    in.close();
			}
		}
	}
	
	private static void menu() {
		if(currentUser == null) {
			System.out.println("Welcome To The Bank!");
			System.out.println("1) Register");
			System.out.println("2) Login");
			System.out.println("x) Exit The Bank");
			String choice = in.next();
			switch(choice) {
			case "1": {
				register();
				break;
			}
			case "2": {
				login();
				break;
			}
			case "p": {
				populate();
				break;
			}
			case "x": {
				try {
					writeObjects();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println("Thank You For Using Our Bank!");
				System.exit(0);
			}
			case "d": {
				if(debug == false) {
					debug = true;
					System.out.println("Debug mode turned on");
					break;
				}
				
				if(debug == true) {
					debug = false;
					System.out.println("Debug mode turned off");
					break;
				}
				break;
			}
			default: {
				System.out.println("/!\\ Invalid Option, Please Try Again!");
				break;
			}
			}
			menu();
		}
		System.out.println("Welcome To The Bank, " + currentUser.getName());
		bankMenu();
	}
	
	private static void register() {
		Profile p = new Profile();
		Account a = new Account();
		ArrayList<Account> accounts = new ArrayList<Account>();
		
		String name = "";
		String password = "";
		String checkPass = "";
		int tries = 5;
		try {
		System.out.println("Register Account");
		System.out.println("Thank You For Choosing Our Bank!");
		System.out.println("Please Enter The Information Below");
		System.out.println("Enter Your Name:");
		name = reader.readLine();
		redoName:
			while(name.isEmpty()) {
				System.out.println("Your Name Can't Be Empty, Please Enter Your Name");
				name = reader.readLine();
				continue redoName;
			}
		System.out.println("Please Enter Your Password");
		password = reader.readLine();
		redoPass:
			while(password.isEmpty()) {
				System.out.println("Your Password Can't Be Empty, Please Enter Your Name");
				password = reader.readLine();
				continue redoPass;
			}
		System.out.println("Please Enter Your Password Again To Confirm");
		checkPass = reader.readLine();
		checkPassword:
		while(tries != 0) {
			if(!password.equals(checkPass)) {
				System.out.println("/!\\ Password Did Not Match");
				System.out.println("You have " + (tries) + " try left.");
				System.out.println("Enter Your Password Again To Confirm");
				checkPass = reader.readLine();
			tries--;
			continue checkPassword;
			}
			break checkPassword;
		}
		
		if(tries == 0) {
			System.out.println("Registeration Process Failed. Please Try Again Later");
			menu();
		}
		
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("System Error, Restarting...");
			register();
		}
		
		a.setId(centralAccountID);
		a.setPin(generatePins());
		a.setBalance(0);
		
		accounts.add(a);
		
		p.setId(centralUserID);
		p.setName(name);
		p.setPassword(password);
		p.setAccounts(accounts);
		
		Profiles.add(p);
		Accounts.add(a);
		
		System.out.println("Your Profile Has Been Registered Successfully!");
		System.out.println("\nProfile ID: \t" + p.getId());
		System.out.println("Name: \t\t" + p.getName());
		System.out.println("Password: \t" + p.getPassword());
		System.out.println("\nBank Account ID: \t" + a.getId());
		System.out.println("Balance: \t\t" + a.getBalance());
		System.out.println("Pin: \t\t\t" + listPins(a));
		
		centralUserID++;
		centralAccountID++;
		currentUser = p;
		update();
	}
	
	private static void login() {
		System.out.println("Log Into Existing Profile");
		System.out.println("Enter Profile ID");
		int id = in.nextInt();
		for(Profile i: Profiles) {
			if(i.getId() == id) {
				System.out.println("Enter Password");
				String password = in.next();
				while(!password.equals(i.getPassword())) {
					System.out.println("/!\\ Incorrect Password, Please Try Again!");
					password = in.next();
				}
				System.out.println("Successfully Logged In As " + i.getName() + " [ID: " + i.getId() + "]");
				currentUser = i;
				bankMenu();
			}
		}
		System.out.println("Invalid Profile, Please Try Again!");
	}
	
	private static int[] generatePins() {
		int[] pins = new int[5];
		int i = 0;
		while(i != pins.length) {
			pins[i] = (int) (Math.random() * 8) + 1;
			i++;
		}
		return pins;
	}
	
	private static Account selectAccount() {
		if(currentUser.getAccounts().isEmpty()) {
			System.out.println("Error! Your Profile Does Not Have Any Bank Account!");
			System.out.println("1) Request A New Bank Account");
			System.out.println("2) Back To Main Menu");
			String choice = in.next();
			if(choice.equals("1")) {
				newBankAccount();
				accountMenu();
			} else if(choice.equals("2")) {
				bankMenu();
			} else {
				System.out.println("Invalid Option, Please Try Again!");
				selectAccount();
			}
			return null;
		}
		
		listAccount();
		try {
		System.out.println("Enter The Account's ID");
		int id = in.nextInt();
		for(Account a: currentUser.getAccounts()) {
			if(id == a.getId()) {
				return a;
			}
		}
		} catch (InputMismatchException e) {
			System.out.println("Invalid Character, Please Make Sure You Enter The Right ID");
			selectAccount();
		}
		return null;
	}
	
	private static void listAccount() {
		currentUser.listAccounts();
	}
	
	private static void bankMenu() {
		System.out.println("Welcome Back To The Bank, " + currentUser.getName());
		System.out.println("1) Manage My Bank Account(s)");
		System.out.println("2) Manage My Profile");
		System.out.println("x) Logout");
		String choice = in.next();
		switch(choice) {
		case "1": {
			accountMenu();
			break;
		}
		case "2": {
			profileMenu();
			break;
		}
		case "x": {
			currentUser = null;
			update();
			menu();
			break;
		}
		default: {
			System.out.println("Invalid Option, Please Try Again!");
			break;
		}
		}
		bankMenu();
	}
	
	private static void accountMenu() {
		pinSession = false;
		System.out.println("1) View My Bank Accounts");
		System.out.println("2) Request A New Bank Account");
		System.out.println("3) Request Bank Account Cancellation");
		System.out.println("x) Return To Bank Menu");
		String choice = in.next();
		switch(choice) {
		case "1": {
			viewBankAccount(selectAccount());
			break;
		}
		case "2": {
			newBankAccount();
			break;
		}
		case "3": {
			removeBankAccount();
			break;
		}
		case "x": {
			pinSession = false;
			bankMenu();
			break;
		}
		default: {
			System.out.println("Invalid Option, Please Try Again!");
			break;
		}
		}
		accountMenu();
	}
	
	private static void viewBankAccount(Account a) {
		if(a == null) {
			System.out.println("/!\\ Account Not Found. Try gain!");
			viewBankAccount(selectAccount());
		}
		
		if(pinSession == false) {
			verifyPins(a);
		}
		System.out.println("Currently Viewing Account: " + a.getId());
		System.out.println("Account Balance: $" + a.getBalance());
		System.out.println("1) Withdraw Money");
		System.out.println("2) Deposit Money");
		System.out.println("3) Transfer Money");
		System.out.println("4) Change Pins");
		System.out.println("5) View Total Profile Balance");
		System.out.println("x) Return To Account Menu");
		String choice = in.next();
		switch(choice) {
		case "1": {
			System.out.println("Enter Amount To Withdraw");
			float amt = in.nextFloat();
			if(amt > a.getBalance()) {
				System.out.println("You Can't Withdraw This Amount!");
				break;
			}
			a.withdraw(amt);
			System.out.println("Successfully Withdrawn $" + amt);
			System.out.println("Account Balance: " + a.getBalance());
			break;
		}
		case "2": {
			System.out.println("Enter Amount To Deposit");
			float amt = in.nextFloat();
			a.deposit(amt);
			System.out.println("Successfully Deposited $" + amt);
			System.out.println("Account Balance: " + a.getBalance());
			break;
		}
		case "3": {
			transfer(a);
			System.out.println("/!\\ Target Account Not Found, Transferring Process Failed");
			break;
		}
		case "4": {
			int[] pins = new int[5];
			int i = 0;
			while(i<pins.length){
				System.out.println("Enter Pin " + (i+1));
				pins[i] = in.nextInt();
				i++;
			}
			a.setPin(pins);
			for(Account j: Accounts) {
				if(j.getId() == a.getId()) {
					j.setPin(pins);
				}
			}
			System.out.println("Your Pin Has Been Successfully Changed To " + listPins(a));
			break;
		}
		case "5": {
			float sum = 0;
			for(Account i: currentUser.getAccounts()) {
				sum += i.getBalance();
			}
			System.out.println("Your Total Profile Balance Is: $" + sum);
			break;
		}
		case "x": {
			pinSession = false;
			accountMenu();
			break;
		}
		default: {
			System.out.println("Invalid Option, Please Try Again!");
			break;
		}
		}
		viewBankAccount(a);
	}
	
	private static void newBankAccount() {
		Account a = new Account();
		
		a.setId(centralAccountID);
		a.setBalance(0);
		a.setPin(generatePins());
		centralAccountID++;
		currentUser.getAccounts().add(a);
		Accounts.add(a);
		
		System.out.println("Bank Account Created Successfully!");
		System.out.println("\nBank Account ID: \t" + a.getId());
		System.out.println("Balance: \t\t" + a.getBalance());
		System.out.println("Pin: \t\t\t" + listPins(a));
		pinSession = false;
	}
	
	private static void removeBankAccount() {
		if(currentUser.getAccounts().isEmpty()) {
			System.out.println("You cannot do this action! You do not have a bank account");
			accountMenu();
		}
		
		Account a = selectAccount();
		if(a == null) {
			System.out.println("/!\\ Account Not Found. Try gain!");
			removeBankAccount();
		}
		
		if(confirm()) {
			if(a.getBalance() != 0) {
				for(Profile j: Profiles) {
					for(Account z: j.getAccounts()) {
						if(z.getId() == a.getId()) {
						System.out.println("There's still money left in this account!");
						if(currentUser.getAccounts().size() == 1) {
							if(confirm()) {
								break;
							}
						} else {
							System.out.println("The balance of Account " + a.getId() + " will be transferred to your first Account [" + j.getAccounts().get(0).getId() + "] instead");
							j.getAccounts().get(0).deposit(a.getBalance());
							}
						}
					}
				}
			}
			Accounts.remove(a);
			for(Profile i: Profiles) {
				i.getAccounts().remove(a);
			}
			System.out.println("Your Bank Account Has Been Cancelled");
			accountMenu();
		}
	}

	private static void transfer(Account a) {
		System.out.println("Enter Bank Account ID To Transfer Money To");
		int id = in.nextInt();
		if(a.getId() == id) {
			System.out.println("You cannot transfer your money to the same bank account!");
			viewBankAccount(a);
		}
		
		float amt = 0;
		for(Account i: Accounts) {
			if(i.getId() == id) {
				System.out.println("Account Found!");
				for(Profile u: Profiles) {
					for(Account ua: u.getAccounts()) {
						if(id == ua.getId()) {
							System.out.println("Account Owner: " + u.getName());
						}
					}
				}
				System.out.println("Enter Amount To Transfer To Account " + i.getId());
				amt = in.nextFloat();
				while(amt > a.getBalance()) {
					System.out.println("You Do Not Have Enough Money For That!");
					System.out.println("Enter Amount To Transfer");
					amt = in.nextFloat();
				}
				if(confirm() == true) {
				a.withdraw(amt);
				i.deposit(amt);
				System.out.println("Successfully Transfered $" + amt +  " To " + i.getId());
				viewBankAccount(a);
				} else {
					viewBankAccount(a);
				}
			}
		}
	}
	
	private static void profileMenu() {
		System.out.println("1) Edit My Name");
		System.out.println("2) Change My Password");
		System.out.println("x) Return To Bank Menu");
		String choice = in.next();
		switch(choice) {
		case "1": {
			try {
				System.out.println("Enter Your New Name");
				String name = reader.readLine();
				redoName:
					while(name.isEmpty()) {
						System.out.println("Your name cannot be empty!");
						System.out.println("Enter Your New Name");
						name = reader.readLine();
						continue redoName;
					}
				currentUser.setName(name);
				System.out.println("Your Name Has Been Successfully Changed To: " + currentUser.getName());
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Error!");
				profileMenu();
			}
			break;
		}
		case "2": {
			try {
				System.out.println("Enter Your New Password");
				String password = reader.readLine();
				redoPass:
				while(password.isEmpty()) {
					System.out.println("Your password cannot be empty!");
					System.out.println("Enter Your New Password");
					password = reader.readLine();
					continue redoPass;
				}
				currentUser.setPassword(password);
				System.out.println("Your Password Has Been Successfully Changed To: " + currentUser.getPassword());
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Error!");
				profileMenu();
			}
			break;
		}
		case "x": {
			bankMenu();
			break;
		}
		default: {
			System.out.println("Invalid Option, Please Try Again!");
			break;
		}
		}
		profileMenu();
	}
	
	private static int[] getRandomPins() {
		int[] selectedPins = new int[3];
		int pin1 = 0, pin2 = 0, pin3 = 0;
		while(pin1 == pin2 || pin1 == pin3 || pin2 == pin3) {
		pin1 = (int) ((Math.random() * 4) + 1);
		pin2 = (int) ((Math.random() * 4) + 1);
		pin3 = (int) ((Math.random() * 4) + 1);
		}
		selectedPins[0] = pin1;
		selectedPins[1] = pin2;
		selectedPins[2] = pin3;
		return selectedPins;
	}
	
	private static void verifyPins(Account a) {
		int[] selectedPins = getRandomPins();
		int[] accPins = {a.getPin()[selectedPins[0]], a.getPin()[selectedPins[1]], a.getPin()[selectedPins[2]]};
		int[] enteredPins = new int[3];
		System.out.println("Please Enter Your Pins");
		int j=0;
		while(j != 3) {
			System.out.println("Pin " + (selectedPins[j]+1) + ":");
			enteredPins[j] = in.nextInt();
			j++;
		}
		
		if(enteredPins[0] == accPins[0] && enteredPins[1] == accPins[1] && enteredPins[2] == accPins[2]) {
			pinSession = true;
		} else {
			System.out.println("Wrong Pins! Please Try Again");
			verifyPins(a);
		}
	}
	
	private static boolean confirm() {
		System.out.println("Confirm This Action?");
		System.out.println("1) Yes");
		System.out.println("2) No");
		String choice = in.next();
		int c = Integer.parseInt(choice);
		if(c == 1) {
			return true;
		}
		return false;
	}
	
	private static String listPins(Account a) {
		String pins = "";
		int i = 0;
		while(i != 5) {
			pins += Integer.toString(a.getPin()[i]);
			i++;
		}
		return pins;
	}
	
	private static void update() {
		try {
			writeObjects();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void populate() {
		ArrayList<Account> u1Accounts = new ArrayList<Account>();
		ArrayList<Account> u2Accounts = new ArrayList<Account>();
		ArrayList<Account> u3Accounts = new ArrayList<Account>();
		ArrayList<Account> u4Accounts = new ArrayList<Account>();
		ArrayList<Account> u5Accounts = new ArrayList<Account>();
		
		Profile u1 = new Profile();
		Profile u2 = new Profile();
		Profile u3 = new Profile();
		Profile u4 = new Profile();
		Profile u5 = new Profile();
		
		Account au11 = new Account();
		Account au12 = new Account();
		
		Account au21 = new Account();
		Account au31 = new Account();
		Account au41 = new Account();
		Account au51 = new Account();
		
		int[] pin = {1, 2, 3, 4, 5};
		
		au11.setId(centralAccountID);
		au11.setBalance(100);
		au11.setPin(pin);
		u1Accounts.add(au11);
		centralAccountID++;
		
		au12.setId(centralAccountID);
		au12.setBalance(100);
		au12.setPin(pin);
		u1Accounts.add(au12);
		centralAccountID++;
		
		au21.setId(centralAccountID);
		au21.setBalance(300);
		au21.setPin(pin);
		u2Accounts.add(au21);
		centralAccountID++;
		
		au31.setId(centralAccountID);
		au31.setBalance(400);
		au31.setPin(pin);
		u3Accounts.add(au31);
		centralAccountID++;
		
		au41.setId(centralAccountID);
		au41.setBalance(1000);
		au41.setPin(pin);
		u4Accounts.add(au41);
		centralAccountID++;
		
		au51.setId(centralAccountID);
		au51.setBalance(50);
		au51.setPin(pin);
		u5Accounts.add(au51);
		centralAccountID++;
		
		u1.setId(centralUserID);
		u1.setPassword("123");
		u1.setName("John");
		u1.setAccounts(u1Accounts);
		centralUserID++;

		u2.setId(centralUserID);
		u2.setPassword("234");
		u2.setName("Pat");
		u2.setAccounts(u2Accounts);
		centralUserID++;
		
		u3.setId(centralUserID);
		u3.setPassword("345");
		u3.setName("Paula");
		u3.setAccounts(u3Accounts);
		centralUserID++;
		
		u4.setId(centralUserID);
		u4.setPassword("456");
		u4.setName("Mick");
		u4.setAccounts(u4Accounts);
		centralUserID++;
		
		u5.setId(centralUserID);
		u5.setPassword("687");
		u5.setName("Ann");
		u5.setAccounts(u5Accounts);
		centralUserID++;
		
		Profiles.add(u1);
		Profiles.add(u2);
		Profiles.add(u3);
		Profiles.add(u4);
		Profiles.add(u5);
		
		Accounts.add(au11);
		Accounts.add(au12);
		Accounts.add(au21);
		Accounts.add(au31);
		Accounts.add(au41);
		Accounts.add(au51);

	}
}
