package com.recruitment.taskmanager.service;

import com.recruitment.taskmanager.model.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;


public class UserSpecification implements Specification<User> {

    private List<SearchCriteria> searchCriteriaList;

    public UserSpecification() {
        this.searchCriteriaList = new ArrayList<>();
    }

    public void add(SearchCriteria criteria) {
        searchCriteriaList.add(criteria);
    }

    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        List<Predicate> predicates = new ArrayList<>();

        for (SearchCriteria criteria : searchCriteriaList) {
            if (criteria.getOperation().equalsIgnoreCase(">")) {
                predicates.add(
                        builder.greaterThan(
                        root.<String>get(criteria.getKey()), criteria.getValue().toString())
                );
            } else if (criteria.getOperation().equalsIgnoreCase(">=")) {
                predicates.add(
                        builder.greaterThanOrEqualTo(
                        root.<String>get(criteria.getKey()), criteria.getValue().toString())
                );
            } else if (criteria.getOperation().equalsIgnoreCase("<")) {
                predicates.add(
                        builder.lessThan(
                        root.<String>get(criteria.getKey()), criteria.getValue().toString())
                );
            } else if (criteria.getOperation().equalsIgnoreCase("<=")) {
                predicates.add(
                        builder.lessThanOrEqualTo(
                        root.<String>get(criteria.getKey()), criteria.getValue().toString())
                );
            } else if (criteria.getOperation().equalsIgnoreCase(":")) {
                if (root.get(criteria.getKey()).getJavaType() == String.class) {
                    predicates.add(
                            builder.like(
                            root.<String>get(criteria.getKey()), "%" + criteria.getValue() + "%")
                    );
                } else {
                    predicates.add(
                            builder.equal(root.get(criteria.getKey()), criteria.getValue())
                    );
                }
            }

        }
        return builder.and(predicates.toArray(new Predicate[0]));
    }

}

