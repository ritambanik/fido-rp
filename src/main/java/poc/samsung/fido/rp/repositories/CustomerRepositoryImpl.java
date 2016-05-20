/**
 * 
 */
package poc.samsung.fido.rp.repositories;

import org.springframework.beans.factory.annotation.Autowired;

import poc.samsung.fido.rp.domain.Customer;

/**
 * @author user
 *
 */
public class CustomerRepositoryImpl implements CustomerRepositoryCustom {

	@Autowired
	private CustomerRepository repository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see poc.samsung.fido.rp.repositories.CustomerRepositoryCustom#
	 * updateDeviceToken(java.lang.String, byte[])
	 */
	@Override
	public void updateDeviceToken(String userName, byte[] deviceToken) {
		if (repository.exists(userName)) {
			Customer user = repository.findOne(userName);
			user.setDeviceToken(deviceToken);
			repository.save(user);
		}
	}

}
