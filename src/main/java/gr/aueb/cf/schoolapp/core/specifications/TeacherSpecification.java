package gr.aueb.cf.schoolapp.core.specifications;

import gr.aueb.cf.schoolapp.model.PersonalInfo;
import gr.aueb.cf.schoolapp.model.Teacher;
import gr.aueb.cf.schoolapp.model.User;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class TeacherSpecification {

    private TeacherSpecification() {

    }

    public static Specification<Teacher> teacherUserVatIs(String vat) {
        return ((root, query, criteriaBuilder) -> {
            if (vat == null || vat.isBlank()) {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }

            Join<Teacher, User> user = root.join("user");
            return criteriaBuilder.equal(user.get("vat"), vat);
        });
    }

    public static Specification<Teacher> trUserIsActive(Boolean isActive) {
        return ((root, query, criteriaBuilder) -> {
            if (isActive == null) {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }

            Join<Teacher, User> user = root.join("user");
            return criteriaBuilder.equal(user.get("isActive"), isActive);
        });
    }

    public static Specification<Teacher> trPersonalInfoAmkaIs(String amka) {
        return ((root, query, criteriaBuilder) -> {
            if (amka == null || amka.isBlank()) {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }
            Join<Teacher, PersonalInfo> personalInfo = root.join("personalInfo");
            return criteriaBuilder.equal(personalInfo.get("isActive"), amka);
        });
    }

        public static Specification<Teacher> trStringFieldLike(String field, String value) {
            return ((root, query, criteriaBuilder) -> {
                if (value == null || value.trim().isEmpty()) {
                    return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
                }
                return criteriaBuilder.like(criteriaBuilder.upper(root.get(field)), "%" +
                        value.toUpperCase() + "%");  // case-insensitive search
            });
        }
    }