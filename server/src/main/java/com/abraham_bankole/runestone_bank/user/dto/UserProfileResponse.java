package com.abraham_bankole.runestone_bank.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    private String firstName;
    private String lastName;
    private String email;
    private String accountNumber;
    private Boolean emailNotifs;
    private Boolean loginAlerts;
    private Boolean transferAlerts;
}
