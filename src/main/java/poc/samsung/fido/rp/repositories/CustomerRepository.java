/**
 * 
 */
package poc.samsung.fido.rp.repositories;

import org.springframework.data.repository.CrudRepository;

import poc.samsung.fido.rp.domain.Customer;

/**
 * @author user
 *
 */
public interface CustomerRepository extends CrudRepository<Customer, String> {

}
