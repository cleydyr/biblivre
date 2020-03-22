package biblivre.circulation.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserDTO, Integer> {
	UserDTO findByLoginId(int loginId);
}
