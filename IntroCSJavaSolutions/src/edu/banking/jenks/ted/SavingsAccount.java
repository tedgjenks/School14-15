package edu.banking.jenks.ted;


import edu.jenks.dist.banking.AbstractSavingsAccount;
import edu.jenks.dist.banking.Account;

public class SavingsAccount extends AbstractSavingsAccount {
	
	/**
	 * 
	 */
	public SavingsAccount() {
		this(0, 0);
	}

	/**
	 * @param balance
	 * @param accountInterestAPR
	 */
	public SavingsAccount(double balance, double accountInterestAPR) {
		super(balance, accountInterestAPR);
	}

	/* (non-Javadoc)
	 * @see jenks.unit4.Account#payInterest(int)
	 */
	@Override
	public void payInterest(int days) {
		AccountHelper.payInterest(this, days);
	}

	/**
	 * <p>Attempt to withdraw if the number of transactions is less than the maximum allowed transactions.</p>
	 * POSTCONDITION: if an amount is withdrawn, <code>numTransactions</code> is incremented.<br>
	 */
	@Override
	public double withdraw(double requestedWithdrawal) {
		double withdrawAmt = 0;
		if(canTransact()) {
			withdrawAmt = AccountHelper.standardWithdraw(this, requestedWithdrawal);
			if(withdrawAmt > 0)
				setNumTransactions(getNumTransactions() + 1);
		}
		return withdrawAmt;
	}

	@Override
	public double transfer(Account destination, double amount) {
		double transferAmt = 0;
		if(amount <= getBalance())
			transferAmt = AccountHelper.transfer(this, destination, amount); // num transactions checked and/or increased in deposit/withdraw
		return transferAmt;
	}

	@Override
	public double deposit(double depositAmount) {
		double depositAmt = 0;
		if(canTransact()) {
			depositAmt = super.deposit(depositAmount);
			setNumTransactions(getNumTransactions() + 1);
		}
		return depositAmt;
	}

	@Override
	public boolean canTransact() {
		return getNumTransactions() < getMaxMonthlyTransactions();
	}
}
