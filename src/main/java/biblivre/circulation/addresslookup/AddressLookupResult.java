package biblivre.circulation.addresslookup;

public record AddressLookupResult(
        String street, String neighborhood, String city, String state, boolean incomplete) {}
