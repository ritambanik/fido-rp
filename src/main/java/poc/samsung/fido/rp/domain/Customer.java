/**
 * 
 */
package poc.samsung.fido.rp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author user
 *
 */
@Entity
public class Customer {
	@Id
	@Column(name="user_id")
	private String userId;
	@Column(name="user_name")
	private String userName;
	@Column(name="password")
	private String password;
	@Column(name="device_token")
	private byte[] deviceToken;
	@Column(name="push_key")
	private byte[] pushKey;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public byte[] getDeviceToken() {
		return deviceToken;
	}
	public void setDeviceToken(byte[] deviceToken) {
		this.deviceToken = deviceToken;
	}
	public byte[] getPushKey() {
		return pushKey;
	}
	public void setPushKey(byte[] pushKey) {
		this.pushKey = pushKey;
	}
	

}
