package gr.aueb.cf.schoolapp.rest;

import gr.aueb.cf.schoolapp.core.exceptions.*;
import gr.aueb.cf.schoolapp.core.filters.Paginated;
import gr.aueb.cf.schoolapp.core.filters.TeacherFilters;
import gr.aueb.cf.schoolapp.dto.TeacherInsertDTO;
import gr.aueb.cf.schoolapp.dto.TeacherReadOnlyDTO;
import gr.aueb.cf.schoolapp.service.TeacherService;
import io.micrometer.common.lang.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TeacherRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TeacherRestController.class);
    private final TeacherService teacherService;

    @GetMapping("/teachers")
    public ResponseEntity<Page<TeacherReadOnlyDTO>> getPaginatedTeachers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1") int size) {

        Page<TeacherReadOnlyDTO> teachersPage = teacherService.getPaginatedTeachers(page, size);
        return new ResponseEntity(teachersPage, HttpStatus.OK);
    }

    @PostMapping("/teachers/save")
    public ResponseEntity<TeacherReadOnlyDTO> saveTeacher(
            @Valid @RequestPart(name = "teacher")TeacherInsertDTO teacherInsertDTO,
            @RequestPart("amkaFile") MultipartFile amkaFile,
            BindingResult bindingResult) throws AppObjectInvalidArgumentException, ValidationException, AppObjectAlreadyExists, AppServerException {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        try {
            TeacherReadOnlyDTO teacherReadOnlyDTO = teacherService.saveTeacher(teacherInsertDTO, amkaFile);
            return new ResponseEntity<>(teacherReadOnlyDTO, HttpStatus.OK);
        } catch (IOException e) {
            throw new AppServerException("Attachment", "Attachment can not get uploaded");
        }
    }

    @PostMapping("/teachers/all")
    public ResponseEntity<List<TeacherReadOnlyDTO>> getTeachers(@Nullable @RequestBody TeacherFilters filters,
                                                                Principal principal)
            throws AppObjectNotFoundException, AppObjectNotAuthorizedException {
        try {
            if (filters == null) filters = TeacherFilters.builder().build();
            return ResponseEntity.ok(teacherService.getTeachersFiltered(filters));
        } catch (Exception e) {
            LOGGER.error("ERROR: Could not get teachers.", e);
            throw e;
        }
    }

    @PostMapping("/teachers/all/paginated")
    public ResponseEntity<Paginated<TeacherReadOnlyDTO>> getTeachersPaginated(@Nullable @RequestBody TeacherFilters filters,
                                                                              Principal principal)
            throws AppObjectNotFoundException, AppObjectNotAuthorizedException {
        try {
            if (filters == null) filters = TeacherFilters.builder().build();
            return ResponseEntity.ok(teacherService.getTeachersFilteredPaginated(filters));
        } catch (Exception e) {
            LOGGER.error("ERROR: Could not get teachers.", e);
            throw e;
        }
    }
}
