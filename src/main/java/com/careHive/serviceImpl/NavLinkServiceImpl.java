package com.careHive.serviceImpl;

import com.careHive.dtos.NavLinks.NavLinkRequestDTO;
import com.careHive.dtos.NavLinks.NavLinkResponseDTO;
import com.careHive.entities.NavLink;
import com.careHive.enums.ExceptionCodeEnum;
import com.careHive.enums.RoleEnum;
import com.careHive.exceptions.CarehiveException;
import com.careHive.repositories.NavLinkRepository;
import com.careHive.services.NavLinkService;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class NavLinkServiceImpl implements NavLinkService {

    private final NavLinkRepository navLinkRepository;
    private final MongoTemplate mongoTemplate;

    // Constructor injection
    public NavLinkServiceImpl(NavLinkRepository navLinkRepository, MongoTemplate mongoTemplate) {
        this.navLinkRepository = navLinkRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public NavLinkResponseDTO createNavLink(NavLinkRequestDTO request) throws CarehiveException {
        if (request == null) {
            throw new CarehiveException(ExceptionCodeEnum.NAV_LINK_NOT_FOUND, "Request is null");
        }

        if (navLinkRepository.existsByRoleCodeAndIndex(request.getRoleCode(), request.getIndex())) {
            throw new CarehiveException(ExceptionCodeEnum.NAV_LINK_ALREADY_EXISTS,"NavLink already exists for this role and index");
        }

        if (navLinkRepository.existsByRoleCodeAndPath(request.getRoleCode(), request.getPath())) {
            throw new CarehiveException(ExceptionCodeEnum.NAV_LINK_ALREADY_EXISTS,"Path already exists for this role");
        }
        NavLink navLink = NavLink.builder()
                .roleCode(request.getRoleCode())
                .index(request.getIndex())
                .name(request.getName())
                .path(request.getPath())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        navLinkRepository.save(navLink);

        return toResponseDTO(navLink);
    }

    @Override
    public NavLinkResponseDTO updateNavLink(String roleIndex, RoleEnum role, NavLinkRequestDTO request) throws CarehiveException {
        NavLink existing = navLinkRepository.findByRoleCodeAndIndex(role, roleIndex)
                .orElseThrow(() -> new CarehiveException(
                        ExceptionCodeEnum.NAV_LINK_NOT_FOUND, "Nav Link not found"
                ));

        NavLink pathConflict = navLinkRepository.findByRoleCodeAndPath(role, request.getPath())
                .orElse(null);
        if (pathConflict != null && !pathConflict.getId().equals(existing.getId())) {
            throw new CarehiveException(
                    ExceptionCodeEnum.NAV_LINK_ALREADY_EXISTS,
                    "Path already exists for this role"
            );
        }

        NavLink indexConflict = navLinkRepository
                .findByRoleCodeAndIndex(request.getRoleCode(), request.getIndex())
                .orElse(null);

        if (indexConflict != null && !indexConflict.getId().equals(existing.getId())) {
            throw new CarehiveException(
                    ExceptionCodeEnum.NAV_LINK_ALREADY_EXISTS,
                    "Index already exists for this role"
            );
        }

        // update fields
        existing.setName(request.getName());
        existing.setIndex(request.getIndex());
        existing.setPath(request.getPath());
        existing.setRoleCode(request.getRoleCode());
        existing.setUpdatedAt(LocalDateTime.now());

        navLinkRepository.save(existing);
        return toResponseDTO(existing);
    }

    @Override
    public void deleteNavLink(String roleIndex, RoleEnum role) throws CarehiveException {
        NavLink navLink = navLinkRepository.findByRoleCodeAndIndex(role, roleIndex)
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.NAV_LINK_NOT_FOUND, "Nav Link not found"));
        navLinkRepository.delete(navLink);
    }

    @Override
    public List<NavLinkResponseDTO> getNavLinks(RoleEnum role) {
        List<NavLink> navLinks = navLinkRepository.findByRoleCode(role);
        return navLinks.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }


    // ⭐⭐⭐ IMPROVED METHOD WITH FILTER + SEARCH + SORT + PAGINATION ⭐⭐⭐
    @Override
    public Page<NavLinkResponseDTO> getAllNavLinks(Pageable pageable, RoleEnum role, String search, String sortBy, String sortDir) {

        // Defaults & null-safety
        String effectiveSortBy = (sortBy == null || sortBy.isBlank()) ? "index" : sortBy;
        String effectiveSortDir = (sortDir == null || sortDir.isBlank()) ? "asc" : sortDir;
        Sort.Direction direction = effectiveSortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        // Build a pageable that includes the requested sort (so sorting is always applied predictably)
        Pageable effectivePageable;
        if (pageable == null) {
            effectivePageable = PageRequest.of(0, 20, Sort.by(direction, effectiveSortBy));
        } else {
            // If incoming pageable already has a sort, prefer explicit sort params (effectiveSortBy/Dir)
            effectivePageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(direction, effectiveSortBy));
        }

        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();

        // Filter: Role
        if (role != null) {
            criteriaList.add(Criteria.where("roleCode").is(role));
        }

        // Filter: Search in name, path, index (escape regex meta-characters)
        if (search != null && !search.isBlank()) {
            String escaped = Pattern.quote(search); // safer: treat entire search as literal
            String regex = ".*" + escaped + ".*";
            criteriaList.add(
                    new Criteria().orOperator(
                            Criteria.where("name").regex(regex, "i"),
                            Criteria.where("path").regex(regex, "i"),
                            Criteria.where("index").regex(regex, "i")
                    )
            );
        }

        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        // Apply sort to query (but final pageable is used for pagination)
        query.with(Sort.by(direction, effectiveSortBy));

        // Count BEFORE pagination
        long total = mongoTemplate.count(query, NavLink.class);

        // Apply pagination (page/size + sort). Use effectivePageable so sort is consistent.
        query.with(effectivePageable);

        List<NavLink> navLinks = mongoTemplate.find(query, NavLink.class);
        List<NavLinkResponseDTO> dtoList = navLinks.stream().map(this::toResponseDTO).collect(Collectors.toList());

        return new PageImpl<>(dtoList, effectivePageable, total);
    }


    @Override
    public NavLinkResponseDTO getNavLink(RoleEnum role, String roleIndex) throws CarehiveException {
        NavLink navLink = navLinkRepository.findByRoleCodeAndIndex(role, roleIndex)
                .orElseThrow(() -> new CarehiveException(ExceptionCodeEnum.NAV_LINK_NOT_FOUND, "Nav Link not found"));
        return toResponseDTO(navLink);
    }


    private NavLinkResponseDTO toResponseDTO(NavLink navLink) {
        return NavLinkResponseDTO.builder()
                .id(navLink.getId())
                .roleCode(navLink.getRoleCode())
                .index(navLink.getIndex())
                .name(navLink.getName())
                .path(navLink.getPath())
                .createdAt(navLink.getCreatedAt())
                .updatedAt(navLink.getUpdatedAt())
                .build();
    }
}
