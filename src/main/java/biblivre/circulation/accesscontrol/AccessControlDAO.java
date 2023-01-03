package biblivre.circulation.accesscontrol;

public interface AccessControlDAO {

    boolean save(AccessControlDTO dto);

    boolean update(AccessControlDTO dto);

    AccessControlDTO getByCardId(Integer cardId);

    AccessControlDTO getByUserId(Integer userId);
}
