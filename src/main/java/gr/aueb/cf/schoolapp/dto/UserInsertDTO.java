package gr.aueb.cf.schoolapp.dto;

import gr.aueb.cf.schoolapp.core.enums.GenderType;
import gr.aueb.cf.schoolapp.core.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserInsertDTO {

    @NotEmpty(message = "Is Active must not be null")
    private Boolean isActive;

    @NotEmpty(message = "Firstname must not be empty")
    private String firstname;

    @NotEmpty(message = "Lastname must not be empty")
    private String lastname;

    @Email(message = "Invalid username")
    private String username;

    @Pattern(regexp = "^(?=.*?[a-z])(?=.*?[A-Z])(?=.*?\\d)(?=.*?[@#$!%^&*]).{8,}$", message = "Invalid password")
    private String password;

    @NotEmpty(message = "Vat must not be empty")
    @Pattern(regexp = "^\\d{9}$")
    private String vat;

    @NotEmpty(message = "Father name must not be empty")
    private String fatherName;

    @NotEmpty(message = "Mother name must not be empty")
    private String motherName;

    @NotEmpty(message = "Father lastname must not be empty")
    private String fatherLastname;

    @NotEmpty(message = "Mother lastname must not be empty")
    private String motherLastname;

    @NotEmpty(message = "Date of birth must not be null")
    private LocalDate dateOfBirth;

    @NotEmpty(message = "Gender must not be null")
    private GenderType gender;

    @NotEmpty(message = "Role must not be null")
    private Role role;
}
