package biblivre.search.user;

public record IndexableUserQueryParameters(
        long id,
        String name,
        String schema,
        String tenant,
        boolean hasLogin,
        boolean hasPendingFines,
        boolean hasPendingLoans,
        boolean hasNeverPrintedUserCard,
        boolean isInactive) {}
