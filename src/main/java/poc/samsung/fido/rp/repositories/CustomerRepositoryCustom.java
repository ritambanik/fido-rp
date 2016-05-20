/**
 * 
 */
package poc.samsung.fido.rp.repositories;

/**
 * @author user
 *
 */
public interface CustomerRepositoryCustom {
	void updateDeviceToken(String user, byte[] deviceToken);
}
