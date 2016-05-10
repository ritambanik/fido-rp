/**
 * 
 */
package poc.samsung.fido.rp.domain;

import java.util.Date;
import java.util.Random;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import poc.samsung.fido.rp.domain.type.AuthRequetStatus;

/**
 * @author user
 *
 */
@Entity
@Table(name="AUTH_REQUEST")
public class AuthRequest {
	
	private static transient final Random KEY_GENERATOR = new Random();
	private static transient final long RANGE = 1234567l;
	
    @Id
    @Column(name="request_id")
	private long authId;
	@Column(name="user_id")
	private String userId;
	@Column(name="requested_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date reqTime;
	@Column(name="request_type")
	private String reqType;
	@Column(name="request_status")
	@Enumerated(EnumType.STRING)
	private AuthRequetStatus reqStatus;
	
	public AuthRequest() {
		authId = (long)(KEY_GENERATOR.nextDouble() * RANGE);
	}
	/**
	 * @return the authId
	 */
	public long getAuthId() {
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
	public AuthRequetStatus getReqStatus() {
		return reqStatus;
	}
	/**
	 * @param reqStatus the reqStatus to set
	 */
	public void setReqStatus(AuthRequetStatus reqStatus) {
		this.reqStatus = reqStatus;
	}
	
	

}
