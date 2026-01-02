package com.careHive.dtos.User;

import com.careHive.dtos.Elder.ElderDTO;
import com.careHive.entities.Address;
import com.careHive.entities.EmergencyContact;
import com.careHive.enums.GenderEnum;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserProfileRequestDTO {
    private LocalDate dateOfBirth;
    private GenderEnum gender;
    private Address address;
    private EmergencyContact emergencyContact;
    private ElderDTO elder;
}
