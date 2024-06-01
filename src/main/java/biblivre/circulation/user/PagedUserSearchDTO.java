package biblivre.circulation.user;

public record PagedUserSearchDTO(UserSearchDTO userSearchDTO, int limit, int offset) {}
