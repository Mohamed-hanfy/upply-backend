package com.upply.organization.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectToOrganizationRequest {

    @NotBlank(message = "Business email is required")
    @Email(message = "Email must be valid")
    private String businessEmail;

    @Valid
    private OrganizationDetails organization;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrganizationDetails {

        @NotBlank(message = "Organization name is required")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        private String name;

        @NotBlank(message = "Industry is required")
        @Size(max = 100, message = "Industry must not exceed 100 characters")
        private String industry;

        @NotBlank(message = "Size is required")
        @Size(max = 50, message = "Size must not exceed 50 characters")
        private String size;

        @NotBlank(message = "Location is required")
        @Size(max = 150, message = "Location must not exceed 150 characters")
        private String location;
    }
}