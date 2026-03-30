package com.gindevp.app.service;

import com.gindevp.app.domain.ReturnRequest;
import com.gindevp.app.domain.ReturnRequestLine;
import com.gindevp.app.domain.enumeration.ReturnRequestStatus;
import com.gindevp.app.repository.ReturnRequestLineRepository;
import com.gindevp.app.repository.ReturnRequestRepository;
import com.gindevp.app.service.dto.ReturnRequestLineDTO;
import com.gindevp.app.service.mapper.ReturnRequestLineMapper;
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
 * Service Implementation for managing {@link com.gindevp.app.domain.ReturnRequestLine}.
 */
@Service
@Transactional
public class ReturnRequestLineService {

    private static final Logger LOG = LoggerFactory.getLogger(ReturnRequestLineService.class);

    private static final String ENTITY_NAME = "returnRequestLine";

    private final ReturnRequestLineRepository returnRequestLineRepository;

    private final ReturnRequestLineMapper returnRequestLineMapper;

    private final ReturnRequestRepository returnRequestRepository;

    private final CurrentEmployeeService currentEmployeeService;

    public ReturnRequestLineService(
        ReturnRequestLineRepository returnRequestLineRepository,
        ReturnRequestLineMapper returnRequestLineMapper,
        ReturnRequestRepository returnRequestRepository,
        CurrentEmployeeService currentEmployeeService
    ) {
        this.returnRequestLineRepository = returnRequestLineRepository;
        this.returnRequestLineMapper = returnRequestLineMapper;
        this.returnRequestRepository = returnRequestRepository;
        this.currentEmployeeService = currentEmployeeService;
    }

    private void assertReturnRequestOpen(ReturnRequest rr) {
        ReturnRequestStatus st = rr.getStatus();
        if (
            st == ReturnRequestStatus.COMPLETED || st == ReturnRequestStatus.REJECTED || st == ReturnRequestStatus.CANCELLED
        ) {
            throw new BadRequestAlertException("Yêu cầu thu hồi đã kết thúc — không thao tác dòng", ENTITY_NAME, "requestclosed");
        }
    }

    private void assertCanAddLine(Long returnRequestId) {
        var rr = returnRequestRepository
            .findOneWithEagerRelationships(returnRequestId)
            .orElseThrow(() -> new BadRequestAlertException("Không tìm thấy yêu cầu thu hồi", ENTITY_NAME, "idnotfound"));
        assertReturnRequestOpen(rr);
        if (!currentEmployeeService.isAssetManagerOrAdmin()) {
            Long eid = currentEmployeeService
                .currentEmployeeId()
                .orElseThrow(() -> new AccessDeniedException("Tài khoản chưa liên kết nhân viên"));
            if (rr.getRequester() == null || !eid.equals(rr.getRequester().getId())) {
                throw new AccessDeniedException("Không phải yêu cầu của bạn");
            }
            if (rr.getStatus() != ReturnRequestStatus.PENDING) {
                throw new BadRequestAlertException("Chỉ thêm dòng khi yêu cầu đang chờ duyệt", ENTITY_NAME, "notpending");
            }
        }
    }

    public ReturnRequestLineDTO save(ReturnRequestLineDTO returnRequestLineDTO) {
        LOG.debug("Request to save ReturnRequestLine : {}", returnRequestLineDTO);
        if (returnRequestLineDTO.getRequest() == null || returnRequestLineDTO.getRequest().getId() == null) {
            throw new BadRequestAlertException("Thiếu yêu cầu thu hồi", ENTITY_NAME, "norequest");
        }
        assertCanAddLine(returnRequestLineDTO.getRequest().getId());
        ReturnRequestLine returnRequestLine = returnRequestLineMapper.toEntity(returnRequestLineDTO);
        returnRequestLine = returnRequestLineRepository.save(returnRequestLine);
        return returnRequestLineMapper.toDto(returnRequestLine);
    }

    public ReturnRequestLineDTO update(ReturnRequestLineDTO returnRequestLineDTO) {
        LOG.debug("Request to update ReturnRequestLine : {}", returnRequestLineDTO);
        if (!currentEmployeeService.isAssetManagerOrAdmin()) {
            throw new AccessDeniedException("Chỉ QLTS/Admin được sửa dòng thu hồi");
        }
        ReturnRequestLine existing = returnRequestLineRepository
            .findOneWithEagerRelationships(returnRequestLineDTO.getId())
            .orElseThrow(() -> new BadRequestAlertException("Không tìm thấy dòng thu hồi", ENTITY_NAME, "idnotfound"));
        assertParentReturnRequestOpen(existing);
        ReturnRequestLine returnRequestLine = returnRequestLineMapper.toEntity(returnRequestLineDTO);
        returnRequestLine = returnRequestLineRepository.save(returnRequestLine);
        return returnRequestLineMapper.toDto(returnRequestLine);
    }

    public Optional<ReturnRequestLineDTO> partialUpdate(ReturnRequestLineDTO returnRequestLineDTO) {
        LOG.debug("Request to partially update ReturnRequestLine : {}", returnRequestLineDTO);
        if (!currentEmployeeService.isAssetManagerOrAdmin()) {
            throw new AccessDeniedException("Chỉ QLTS/Admin được cập nhật dòng thu hồi");
        }

        return returnRequestLineRepository
            .findOneWithEagerRelationships(returnRequestLineDTO.getId())
            .map(existingReturnRequestLine -> {
                assertParentReturnRequestOpen(existingReturnRequestLine);
                returnRequestLineMapper.partialUpdate(existingReturnRequestLine, returnRequestLineDTO);

                return existingReturnRequestLine;
            })
            .map(returnRequestLineRepository::save)
            .map(returnRequestLineMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<ReturnRequestLineDTO> findAll() {
        LOG.debug("Request to get all ReturnRequestLines");
        return returnRequestLineRepository
            .findAll()
            .stream()
            .map(returnRequestLineMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    public Page<ReturnRequestLineDTO> findAllWithEagerRelationships(Pageable pageable) {
        return returnRequestLineRepository.findAllWithEagerRelationships(pageable).map(returnRequestLineMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<ReturnRequestLineDTO> findOne(Long id) {
        LOG.debug("Request to get ReturnRequestLine : {}", id);
        return returnRequestLineRepository.findOneWithEagerRelationships(id).map(returnRequestLineMapper::toDto);
    }

    public void delete(Long id) {
        LOG.debug("Request to delete ReturnRequestLine : {}", id);
        if (!currentEmployeeService.isAssetManagerOrAdmin()) {
            throw new AccessDeniedException("Chỉ QLTS/Admin được xóa dòng thu hồi");
        }
        ReturnRequestLine existing = returnRequestLineRepository
            .findOneWithEagerRelationships(id)
            .orElseThrow(() -> new BadRequestAlertException("Không tìm thấy dòng thu hồi", ENTITY_NAME, "idnotfound"));
        assertParentReturnRequestOpen(existing);
        returnRequestLineRepository.deleteById(id);
    }

    private void assertParentReturnRequestOpen(ReturnRequestLine line) {
        if (line.getRequest() == null || line.getRequest().getId() == null) {
            return;
        }
        ReturnRequest rr = returnRequestRepository
            .findOneWithEagerRelationships(line.getRequest().getId())
            .orElseThrow(() -> new BadRequestAlertException("Không tìm thấy yêu cầu thu hồi", ENTITY_NAME, "idnotfound"));
        assertReturnRequestOpen(rr);
    }
}
