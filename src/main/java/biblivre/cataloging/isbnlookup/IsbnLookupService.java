package biblivre.cataloging.isbnlookup;

import biblivre.cataloging.isbnlookup.IsbnLookupException.Reason;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import org.marc4j.marc.Record;
import org.springframework.stereotype.Service;

/** Looks up book metadata by ISBN through the provider named in the configuration. */
@Service
public class IsbnLookupService {
    private final IsbnMetadataProvider provider;

    public IsbnLookupService(
            IsbnLookupProperties properties, List<IsbnMetadataProvider> availableProviders) {
        this.provider = properties.isEnabled() ? resolve(properties, availableProviders) : null;
    }

    public boolean isEnabled() {
        return provider != null;
    }

    public IsbnLookupResult lookup(String rawIsbn) {
        if (!isEnabled()) {
            throw new IsbnLookupException(Reason.DISABLED, "ISBN lookup is disabled");
        }

        String isbn = Isbn.normalize(rawIsbn);

        if (!Isbn.isValid(isbn)) {
            throw new IsbnLookupException(Reason.INVALID_ISBN, "Invalid ISBN");
        }

        Optional<Record> record;

        try {
            record = provider.lookup(isbn);
        } catch (IsbnLookupException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new IsbnLookupException(
                    Reason.UPSTREAM_ERROR, "ISBN provider " + provider.name() + " failed", e);
        }

        return record.map(found -> new IsbnLookupResult(isbn, provider.name(), found))
                .orElseThrow(() -> new IsbnLookupException(Reason.NOT_FOUND, "ISBN not found"));
    }

    private static IsbnMetadataProvider resolve(
            IsbnLookupProperties properties, List<IsbnMetadataProvider> availableProviders) {
        Map<String, IsbnMetadataProvider> byName = new TreeMap<>();

        for (IsbnMetadataProvider candidate : availableProviders) {
            byName.put(candidate.name().toLowerCase(Locale.ROOT), candidate);
        }

        String configured = properties.getProvider().trim().toLowerCase(Locale.ROOT);
        IsbnMetadataProvider resolved = byName.get(configured);

        if (resolved == null) {
            throw new IllegalStateException(
                    "Unknown biblivre.cataloging.isbn-lookup.provider '"
                            + properties.getProvider()
                            + "'. Use '"
                            + IsbnLookupProperties.DISABLED_PROVIDER
                            + "' or one of "
                            + byName.keySet());
        }

        return resolved;
    }
}
