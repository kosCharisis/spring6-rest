package gr.aueb.cf.schoolapp.service;

import gr.aueb.cf.schoolapp.core.exceptions.AppObjectAlreadyExists;
import gr.aueb.cf.schoolapp.core.exceptions.AppObjectInvalidArgumentException;
import gr.aueb.cf.schoolapp.core.filters.Paginated;
import gr.aueb.cf.schoolapp.core.filters.TeacherFilters;
import gr.aueb.cf.schoolapp.core.specifications.TeacherSpecification;
import gr.aueb.cf.schoolapp.dto.TeacherInsertDTO;
import gr.aueb.cf.schoolapp.dto.TeacherReadOnlyDTO;
import gr.aueb.cf.schoolapp.mapper.Mapper;
import gr.aueb.cf.schoolapp.model.Attachment;
import gr.aueb.cf.schoolapp.model.PersonalInfo;
import gr.aueb.cf.schoolapp.model.Teacher;
import gr.aueb.cf.schoolapp.repository.PersonalInfoRepository;
import gr.aueb.cf.schoolapp.repository.TeacherRepository;
import gr.aueb.cf.schoolapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final PersonalInfoRepository personalInfoRepository;
    private final Mapper mapper;

//    @Autowired
//    public TeacherService(TeacherRepository teacherRepository, UserRepository userRepository, PersonalInfoRepository personalInfoRepository, Mapper mapper) {
//        this.teacherRepository = teacherRepository;
//        this.userRepository = userRepository;
//        this.personalInfoRepository = personalInfoRepository;
//        this.mapper = mapper;
//    }

    @Transactional(rollbackOn = Exception.class)
    public TeacherReadOnlyDTO saveTeacher(TeacherInsertDTO teacherInsertDTO, MultipartFile amkaFile)
            throws AppObjectAlreadyExists, AppObjectInvalidArgumentException, IOException {

        if (userRepository.findByVat(teacherInsertDTO.getUser().getVat()).isPresent()) {
            throw new AppObjectAlreadyExists("User", "User with vat: " + teacherInsertDTO.getUser().getVat() + " already exists");
        }

        if (userRepository.findByUsername(teacherInsertDTO.getUser().getUsername()).isPresent()) {
            throw new AppObjectAlreadyExists("User", "User with username: " + teacherInsertDTO.getUser().getUsername() + " already exists");
        }

        if (personalInfoRepository.findByAmka(teacherInsertDTO.getPersonalInfo().getAmka()).isPresent()) {
            throw new AppObjectAlreadyExists("PersonalInfo", "Personal info with AMKA: " + teacherInsertDTO.getPersonalInfo().getAmka() + " already exists");
        }

        if (personalInfoRepository.findByIdentityNumber(teacherInsertDTO.getPersonalInfo().getIdentityNumber()).isPresent()) {
            throw new AppObjectAlreadyExists("PersonalInfo", "Personal info with identity number: " + teacherInsertDTO.getPersonalInfo().getIdentityNumber() + " already exists");
        }

        Teacher teacher = mapper.mapToTeacherEntity(teacherInsertDTO);

        saveAmkaFile(teacher.getPersonalInfo(), amkaFile);

        Teacher savedTeacher = teacherRepository.save(teacher);
        return mapper.mapToTeacherReadOnlyDTO(savedTeacher);
    }

    @Transactional(rollbackOn = Exception.class)
    public void saveAmkaFile(PersonalInfo personalInfo, MultipartFile amkaFile) throws IOException {

        if (amkaFile != null && !amkaFile.isEmpty()) {

            String originalFileName = amkaFile.getOriginalFilename();
            String savedName = UUID.randomUUID() + getFileExtension(originalFileName);
            String uploadDir = "uploads/";
            Path filepath = Paths.get(uploadDir + savedName);
            Files.createDirectories(filepath.getParent());
            Files.write(filepath, amkaFile.getBytes());

            Attachment attachment = new Attachment();
            attachment.setFilename(originalFileName);
            attachment.setSavedName(savedName);
            attachment.setFilePath(filepath.toString());
            attachment.setContentType(amkaFile.getContentType());
            attachment.setExtension(getFileExtension(originalFileName));

            personalInfo.setAmkaFile(attachment);
        }
    }

    public String getFileExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return "";
    }

    public Page<TeacherReadOnlyDTO> getPaginatedTeachers(int page, int size) {
        String defaultSort = "id";
        Pageable pageable = PageRequest.of(page, size, Sort.by(defaultSort).ascending());
        return teacherRepository.findAll(pageable).map(mapper::mapToTeacherReadOnlyDTO);
    }

    public Page<TeacherReadOnlyDTO> getPaginatedSortedTeachers(int page, int size, String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return teacherRepository.findAll(pageable).map(mapper::mapToTeacherReadOnlyDTO);
    }

    @org.springframework.transaction.annotation.Transactional
    public Paginated<TeacherReadOnlyDTO> getTeachersFilteredPaginated(TeacherFilters filters) {
        var filtered = teacherRepository.findAll(getSpecsFromFilters(filters), filters.getPageable());
        return new Paginated<>(filtered.map(mapper::mapToTeacherReadOnlyDTO));
    }

    @org.springframework.transaction.annotation.Transactional
    public List<TeacherReadOnlyDTO> getTeachersFiltered(TeacherFilters filters) {
        return teacherRepository.findAll(getSpecsFromFilters(filters))
                .stream().map(mapper::mapToTeacherReadOnlyDTO).toList();
    }

    private Specification<Teacher> getSpecsFromFilters(TeacherFilters filters) {
        return Specification
                .where(TeacherSpecification.trStringFieldLike("uuid", filters.getUuid()))
                .and(TeacherSpecification.teacherUserVatIs(filters.getUserVat()))
                .and(TeacherSpecification.trPersonalInfoAmkaIs(filters.getUserAmka()))
                .and(TeacherSpecification.trUserIsActive(filters.getActive()));
    }
}