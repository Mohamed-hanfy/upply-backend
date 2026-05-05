package com.upply.organization.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectToOrganizationResponse {

    private String message;
    private boolean organizationCreated;
    private OrganizationResponse organization;
}