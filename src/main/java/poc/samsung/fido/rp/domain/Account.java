/**
 * 
 */
package poc.samsung.fido.rp.domain;

import java.util.Random;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author user
 *
 */
public class Account {
	
	private static transient final Random KEY_GENERATOR = new Random();
	private static transient final long RANGE = 9876543l;
	
	private long accNo;
	private String accFirstName;
	private String accLastName;
	private double balance;
	private String accContactNo;
	
	public Account() {
		accNo = (long)(KEY_GENERATOR.nextDouble() * RANGE);;
	}

	/**
	 * @return the accNo
	 */
	public long getAccNo() {
		return accNo;
	}

	/**
	 * @param accNo the accNo to set
	 */
	public void setAccNo(long accNo) {
		this.accNo = accNo;
	}

	/**
	 * @return the accFirstName
	 */
	public String getAccFirstName() {
		return accFirstName;
	}

	/**
	 * @param accFirstName the accFirstName to set
	 */
	public void setAccFirstName(String accFirstName) {
		this.accFirstName = accFirstName;
	}

	/**
	 * @return the accLastName
	 */
	public String getAccLastName() {
		return accLastName;
	}

	/**
	 * @param accLastName the accLastName to set
	 */
	public void setAccLastName(String accLastName) {
		this.accLastName = accLastName;
	}

	/**
	 * @return the balance
	 */
	public double getBalance() {
		return balance;
	}

	/**
	 * @param balance the balance to set
	 */
	public void setBalance(double balance) {
		this.balance = balance;
	}

	/**
	 * @return the accContactNo
	 */
	public String getAccContactNo() {
		return accContactNo;
	}

	/**
	 * @param accContactNo the accContactNo to set
	 */
	public void setAccContactNo(String accContactNo) {
		this.accContactNo = accContactNo;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("accNo", accNo);
		builder.append("accFirstName", accFirstName);
		builder.append("accLastName", accLastName);
		builder.append("balance", balance);
		builder.append("accContactNo", accContactNo);
		return builder.toString();
	}
	
	

}
