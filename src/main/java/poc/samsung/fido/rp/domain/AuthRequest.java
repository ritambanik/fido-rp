/**
 * 
 */
package poc.samsung.fido.rp.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author user
 *
 */
@Entity
@Table(name="AUTH_REQUEST")
public class AuthRequest {
    @Id
    @Column(name="auth_id")
	private int authId;
	@Column(name="user_id")
	private String userId;
	@Column(name="requested_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date reqTime;
	@Column(name="requested_type")
	private String reqType;
	@Column(name="status")
	private String reqStatus;
	/**
	 * @return the authId
	 */
	public int getAuthId() {
		return authId;
	}
	/**
	 * @param authId the authId to set
	 */
	public void setAuthId(int authId) {
		this.authId = authId;
	}
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * @return the reqTime
	 */
	public Date getReqTime() {
		return reqTime;
	}
	/**
	 * @param reqTime the reqTime to set
	 */
	public void setReqTime(Date reqTime) {
		this.reqTime = reqTime;
	}
	/**
	 * @return the reqType
	 */
	public String getReqType() {
		return reqType;
	}
	/**
	 * @param reqType the reqType to set
	 */
	public void setReqType(String reqType) {
		this.reqType = reqType;
	}
	/**
	 * @return the reqStatus
	 */
	public String getReqStatus() {
		return reqStatus;
	}
	/**
	 * @param reqStatus the reqStatus to set
	 */
	public void setReqStatus(String reqStatus) {
		this.reqStatus = reqStatus;
	}
	
	

}
