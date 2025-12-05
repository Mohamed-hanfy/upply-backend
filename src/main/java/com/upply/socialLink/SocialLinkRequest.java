package com.upply.socialLink;

import jakarta.validation.constraints.NotNull;

public record SocialLinkRequest(
        @NotNull
        String url,
        @NotNull
        SocialType socialType
) {
}
