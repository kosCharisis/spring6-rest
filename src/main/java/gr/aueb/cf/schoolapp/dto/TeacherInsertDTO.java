package gr.aueb.cf.schoolapp.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TeacherInsertDTO {

    @NotEmpty(message = "Is active must not be null")
    private Boolean isActive;

    @NotEmpty(message = "User details must not be null")
    private UserInsertDTO user;

    @NotEmpty(message = "Personal info must not be null")
    private PersonalInfoInsertDTO personalInfo;
}
