package biblivre.administration.accesscards;

public record PagedAccessCardSearchDTO(
        String code, AccessCardStatus status, int limit, int offset) {}
