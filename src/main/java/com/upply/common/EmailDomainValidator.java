package com.upply.common;

import java.util.Set;

/**
 * Utility for classifying email domains as public (consumer) vs. corporate.
 *
 * <p>Note: The public-domain list is curated and not exhaustive. It covers the most
 * common consumer providers. Subdomain variants (e.g. {@code mail.yahoo.com}) are
 * resolved to their root domain before matching.
 */
public final class EmailDomainValidator {

    /**
     * Known public/consumer email providers.
     * Update periodically as new providers gain traction.
     */
    private static final Set<String> PUBLIC_DOMAINS = Set.of(
            // Google
            "gmail.com", "googlemail.com",
            // Microsoft
            "outlook.com", "hotmail.com", "live.com", "msn.com",
            // Yahoo
            "yahoo.com", "ymail.com",
            // Apple
            "icloud.com", "me.com", "mac.com",
            // Privacy-focused
            "protonmail.com", "proton.me", "tutanota.com", "tutanota.de",
            "tutamail.com", "tuta.io",
            // Other popular providers
            "mail.com", "gmx.com", "gmx.net", "gmx.de",
            "yandex.com", "yandex.ru",
            "zoho.com",
            "fastmail.com", "fastmail.fm",
            "aol.com",
            "hey.com",
            "duck.com"
    );

    /**
     * Maps known subdomain prefixes to their root public domain.
     * Handles cases like {@code mail.yahoo.com} → {@code yahoo.com}.
     */
    private static final Set<String> PUBLIC_DOMAIN_ROOTS = Set.of(
            "yahoo.com", "yandex.com", "yandex.ru", "zoho.com",
            "fastmail.com", "protonmail.com", "gmx.com", "gmx.net"
    );

    /**
     * Known multi-part TLDs (country-code second-level domains).
     * Used to correctly identify the SLD in domains like {@code company.co.uk}.
     */
    private static final Set<String> MULTI_PART_TLDS = Set.of(
            "co.uk", "co.in", "co.jp", "co.za", "co.nz", "co.kr", "co.au",
            "com.au", "com.br", "com.ar", "com.mx", "com.eg", "com.tr",
            "org.uk", "net.au", "ac.uk", "gov.uk"
    );

    private EmailDomainValidator() {}

    /**
     * Returns {@code true} if the email belongs to a known public/consumer provider.
     *
     * @param email a non-null, non-blank email address
     * @return {@code true} if the domain is a known public provider
     * @throws IllegalArgumentException if the email is null, blank, or structurally invalid
     */
    public static boolean isPublicDomain(String email) {
        String domain = extractDomain(email);
        return PUBLIC_DOMAINS.contains(domain) || matchesPublicDomainRoot(domain);
    }

    /**
     * Extracts and lowercases the domain part of an email address.
     *
     * @param email the email address to parse
     * @return the lowercase domain (e.g. {@code "gmail.com"})
     * @throws IllegalArgumentException if the email is null, blank, or missing a valid domain
     */
    public static String extractDomain(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email must not be null or blank");
        }

        int atIndex = email.indexOf('@');
        if (atIndex == -1) {
            throw new IllegalArgumentException("Invalid email address: missing '@'");
        }
        if (atIndex != email.lastIndexOf('@')) {
            throw new IllegalArgumentException("Invalid email address: multiple '@'");
        }

        String domain = email.substring(atIndex + 1).toLowerCase();

        if (domain.isEmpty() || !domain.contains(".")) {
            throw new IllegalArgumentException("Invalid email address: malformed domain");
        }

        return domain;
    }

    /**
     * Extracts a human-readable, capitalized company name from a corporate email address.
     *
     * <p>Examples:
     * <ul>
     *   <li>{@code "kareem@upply.com"} → {@code "Upply"}</li>
     *   <li>{@code "john@morgan-stanley.com"} → {@code "Morgan Stanley"}</li>
     *   <li>{@code "john@mail.company.co.uk"} → {@code "Company"}</li>
     * </ul>
     *
     * @param email a non-null, non-blank corporate email address
     * @return a capitalized company name derived from the domain's SLD
     * @throws IllegalArgumentException if the email is invalid or belongs to a public provider
     */
    public static String extractCompanyName(String email) {
        String domain = extractDomain(email); // throws on invalid input

        if (PUBLIC_DOMAINS.contains(domain) || matchesPublicDomainRoot(domain)) {
            throw new IllegalArgumentException(
                    "Cannot extract company name from a public email provider: " + domain
            );
        }

        String sld = extractSld(domain);

        // Replace hyphens and underscores with spaces, then capitalize each word
        String[] words = sld.split("[-_]");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                if (!result.isEmpty()) result.append(" ");
                result.append(Character.toUpperCase(word.charAt(0)));
                result.append(word.substring(1).toLowerCase());
            }
        }

        return result.toString();
    }

    /**
     * Extracts the second-level domain (SLD) from a full domain, correctly handling
     * multi-part TLDs like {@code co.uk}.
     *
     * <p>Examples:
     * <ul>
     *   <li>{@code "upply.com"} → {@code "upply"}</li>
     *   <li>{@code "morgan-stanley.com"} → {@code "morgan-stanley"}</li>
     *   <li>{@code "mail.company.co.uk"} → {@code "company"}</li>
     * </ul>
     */
    private static String extractSld(String domain) {
        String[] parts = domain.split("\\.");

        // Check if the last two parts form a known multi-part TLD (e.g. "co.uk")
        // If so, SLD is parts[length - 3], otherwise parts[length - 2]
        if (parts.length >= 3) {
            String lastTwo = parts[parts.length - 2] + "." + parts[parts.length - 1];
            if (MULTI_PART_TLDS.contains(lastTwo)) {
                return parts[parts.length - 3];
            }
        }

        // Standard case: SLD is the part just before the TLD
        return parts[parts.length - 2];
    }

    /**
     * Checks whether the given domain is a subdomain of a known public provider root.
     * For example, {@code mail.yahoo.com} ends with {@code .yahoo.com}.
     *
     * <p>The dot-prefix check prevents false positives like {@code notgmail.com}.
     */
    private static boolean matchesPublicDomainRoot(String domain) {
        for (String root : PUBLIC_DOMAIN_ROOTS) {
            if (domain.endsWith("." + root)) {
                return true;
            }
        }
        return false;
    }
}