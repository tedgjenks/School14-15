package edu.jenks.dist.banking;
public abstract class AbstractCheckingAccount extends Account {

	private int overdraftFee;
	private int numberOverdrafts; // reset to zero after fee calculation
	private int overdraftMax;
	private boolean overdraftProtected;
	private Account linkedSavingsAccount;
	
	
	
	public AbstractCheckingAccount() {
		this(0, 0);
	}

	public AbstractCheckingAccount(double balance, double accountInterestAPR) {
		super(balance, accountInterestAPR);
	}

	/**
	 * @return the savings account linked to this checking account
	 */
	public Account getLinkedSavingsAccount() {
		return linkedSavingsAccount;
	}

	/**
	 * @param linkedSavingsAccount
	 */
	public void setLinkedSavingsAccount(Account linkedSavingsAccount) {
		this.linkedSavingsAccount = linkedSavingsAccount;
	}
	
	/**
	 * @return the overdraft fee that will be charged for every overdraft
	 */
	public int getOverdraftFee() {
		return overdraftFee;
	}

	/**
	 * @param overdraftFee
	 */
	public void setOverdraftFee(int overdraftFee) {
		this.overdraftFee = overdraftFee;
	}

	/**
	 * @return the maximum amount of money that can be overdrawn from the account.
	 */
	public int getOverdraftMax() {
		return overdraftMax;
	}

	/**
	 * @param overdraftMax
	 */
	public void setOverdraftMax(int overdraftMax) {
		this.overdraftMax = overdraftMax;
	}

	/**
	 * @param numberOverdrafts
	 */
	public void setNumberOverdrafts(int numberOverdrafts) {
		this.numberOverdrafts = numberOverdrafts;
	}
	
	/**
	 * @param overdraftProtected
	 */
	public void setOverdraftProtected(boolean overdraftProtected) {
		this.overdraftProtected = overdraftProtected;
	}
	
	/**
	 * @return true if the account may be overdrawn, otherwise false.
	 */
	public boolean isOverdraftProtected() {
		return overdraftProtected;
	}
	
	/**
	 * @return the number of overdraft transactions
	 */
	public int getNumberOverdrafts() {
		return numberOverdrafts;
	}
}