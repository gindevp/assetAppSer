package com.gindevp.app.service;

import com.gindevp.app.domain.AllocationRequestLine;
import com.gindevp.app.domain.enumeration.AllocationRequestStatus;
import com.gindevp.app.repository.AllocationRequestLineRepository;
import com.gindevp.app.repository.AllocationRequestRepository;
import com.gindevp.app.service.dto.AllocationRequestLineDTO;
import com.gindevp.app.service.mapper.AllocationRequestLineMapper;
import com.gindevp.app.web.rest.errors.BadRequestAlertException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.gindevp.app.domain.AllocationRequestLine}.
 */
@Service
@Transactional
public class AllocationRequestLineService {

    private static final Logger LOG = LoggerFactory.getLogger(AllocationRequestLineService.class);

    private static final String ENTITY_NAME = "allocationRequestLine";

    private final AllocationRequestLineRepository allocationRequestLineRepository;

    private final AllocationRequestLineMapper allocationRequestLineMapper;

    private final AllocationRequestRepository allocationRequestRepository;

    private final CurrentEmployeeService currentEmployeeService;

    public AllocationRequestLineService(
        AllocationRequestLineRepository allocationRequestLineRepository,
        AllocationRequestLineMapper allocationRequestLineMapper,
        AllocationRequestRepository allocationRequestRepository,
        CurrentEmployeeService currentEmployeeService
    ) {
        this.allocationRequestLineRepository = allocationRequestLineRepository;
        this.allocationRequestLineMapper = allocationRequestLineMapper;
        this.allocationRequestRepository = allocationRequestRepository;
        this.currentEmployeeService = currentEmployeeService;
    }

    private void assertCanAddLineToRequest(Long requestId) {
        var ar = allocationRequestRepository
            .findOneWithEagerRelationships(requestId)
            .orElseThrow(() -> new BadRequestAlertException("Không tìm thấy yêu cầu cấp phát", ENTITY_NAME, "idnotfound"));
        if (!currentEmployeeService.isAssetManagerOrAdmin()) {
            Long eid = currentEmployeeService
                .currentEmployeeId()
                .orElseThrow(() -> new AccessDeniedException("Tài khoản chưa liên kết nhân viên"));
            if (ar.getRequester() == null || !eid.equals(ar.getRequester().getId())) {
                throw new AccessDeniedException("Không phải yêu cầu của bạn");
            }
            if (ar.getStatus() != AllocationRequestStatus.PENDING) {
                throw new BadRequestAlertException("Chỉ thêm dòng khi yêu cầu đang chờ duyệt", ENTITY_NAME, "notpending");
            }
        }
    }

    /**
     * Save a allocationRequestLine.
     *
     * @param allocationRequestLineDTO the entity to save.
     * @return the persisted entity.
     */
    public AllocationRequestLineDTO save(AllocationRequestLineDTO allocationRequestLineDTO) {
        LOG.debug("Request to save AllocationRequestLine : {}", allocationRequestLineDTO);
        if (allocationRequestLineDTO.getRequest() == null || allocationRequestLineDTO.getRequest().getId() == null) {
            throw new BadRequestAlertException("Thiếu yêu cầu cấp phát", ENTITY_NAME, "norequest");
        }
        assertCanAddLineToRequest(allocationRequestLineDTO.getRequest().getId());
        AllocationRequestLine allocationRequestLine = allocationRequestLineMapper.toEntity(allocationRequestLineDTO);
        allocationRequestLine = allocationRequestLineRepository.save(allocationRequestLine);
        return allocationRequestLineMapper.toDto(allocationRequestLine);
    }

    /**
     * Update a allocationRequestLine.
     *
     * @param allocationRequestLineDTO the entity to save.
     * @return the persisted entity.
     */
    public AllocationRequestLineDTO update(AllocationRequestLineDTO allocationRequestLineDTO) {
        LOG.debug("Request to update AllocationRequestLine : {}", allocationRequestLineDTO);
        AllocationRequestLine allocationRequestLine = allocationRequestLineMapper.toEntity(allocationRequestLineDTO);
        allocationRequestLine = allocationRequestLineRepository.save(allocationRequestLine);
        return allocationRequestLineMapper.toDto(allocationRequestLine);
    }

    /**
     * Partially update a allocationRequestLine.
     *
     * @param allocationRequestLineDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AllocationRequestLineDTO> partialUpdate(AllocationRequestLineDTO allocationRequestLineDTO) {
        LOG.debug("Request to partially update AllocationRequestLine : {}", allocationRequestLineDTO);
        if (!currentEmployeeService.isAssetManagerOrAdmin()) {
            throw new AccessDeniedException("Chỉ QLTS/Admin được cập nhật dòng (gán thiết bị tồn kho)");
        }

        return allocationRequestLineRepository
            .findById(allocationRequestLineDTO.getId())
            .map(existingAllocationRequestLine -> {
                allocationRequestLineMapper.partialUpdate(existingAllocationRequestLine, allocationRequestLineDTO);

                return existingAllocationRequestLine;
            })
            .map(allocationRequestLineRepository::save)
            .map(allocationRequestLineMapper::toDto);
    }

    /**
     * Get all the allocationRequestLines.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<AllocationRequestLineDTO> findAll() {
        LOG.debug("Request to get all AllocationRequestLines");
        return allocationRequestLineRepository
            .findAll()
            .stream()
            .map(allocationRequestLineMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the allocationRequestLines with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<AllocationRequestLineDTO> findAllWithEagerRelationships(Pageable pageable) {
        return allocationRequestLineRepository.findAllWithEagerRelationships(pageable).map(allocationRequestLineMapper::toDto);
    }

    /**
     * Get one allocationRequestLine by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AllocationRequestLineDTO> findOne(Long id) {
        LOG.debug("Request to get AllocationRequestLine : {}", id);
        return allocationRequestLineRepository.findOneWithEagerRelationships(id).map(allocationRequestLineMapper::toDto);
    }

    /**
     * Delete the allocationRequestLine by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete AllocationRequestLine : {}", id);
        allocationRequestLineRepository.deleteById(id);
    }
}
