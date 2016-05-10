/**
 * 
 */
package poc.samsung.fido.rp.contoller.message;

import com.google.common.base.Optional;

/**
 * @author user
 *
 */
public class RPResponseMsg<T> {
	
	enum Status { SUCCESS, FAILURE};
	
	private final Status status;
	private final String service;
	private final String invokeAt;
	private final long elapseTime;
	private final Optional<String> errorMsg;
	private final Optional<T> output;
	/**
	 * @param status
	 * @param service
	 * @param invokeAt
	 * @param elapseTime
	 * @param errorMsg
	 * @param output
	 */
	public RPResponseMsg(String status, String service, String invokeAt, long elapseTime, Optional<String> errorMsg,
			Optional<T> output) {
		this.status = Status.valueOf(status);
		this.service = service;
		this.invokeAt = invokeAt;
		this.elapseTime = elapseTime;
		this.errorMsg = errorMsg;
		this.output = output;
	}
	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}
	/**
	 * @return the service
	 */
	public String getService() {
		return service;
	}
	/**
	 * @return the invokeAt
	 */
	public String getInvokeAt() {
		return invokeAt;
	}
	/**
	 * @return the elapseTime
	 */
	public long getElapseTime() {
		return elapseTime;
	}
	/**
	 * @return the errorMsg
	 */
	public String getErrorMsg() {
		return errorMsg.isPresent() ? errorMsg.get() : org.apache.commons.lang3.StringUtils.EMPTY;
	}
	/**
	 * @return the output
	 */
	public T getOutput() {
		return output.isPresent() ? output.get() : null;
	}
	
	

}
