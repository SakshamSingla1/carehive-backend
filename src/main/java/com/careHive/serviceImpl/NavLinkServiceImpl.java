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

    @Override
    public Page<NavLinkResponseDTO> getAllNavLinks(
            Pageable pageable, RoleEnum role, String search, String sortBy, String sortDir) {
        Pageable p = PageRequest.of(
                pageable == null ? 0 : pageable.getPageNumber(),
                pageable == null ? 20 : pageable.getPageSize(),
                Sort.by("desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC,
                        (sortBy == null || sortBy.isBlank()) ? "index" : sortBy)
        );
        Query q = new Query().with(p);
        if (role != null)
            q.addCriteria(Criteria.where("roleCode").is(role));
        if (search != null && !search.isBlank()) {
            String r = ".*" + Pattern.quote(search) + ".*";
            q.addCriteria(new Criteria().orOperator(
                    Criteria.where("name").regex(r, "i"),
                    Criteria.where("path").regex(r, "i"),
                    Criteria.where("index").regex(r, "i")
            ));
        }
        long total = mongoTemplate.count(q, NavLink.class);
        return new PageImpl<>(
                mongoTemplate.find(q, NavLink.class)
                        .stream().map(this::toResponseDTO).toList(),
                p, total
        );
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
