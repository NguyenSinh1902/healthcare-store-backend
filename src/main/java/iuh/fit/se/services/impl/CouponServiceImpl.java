package iuh.fit.se.services.impl;

import iuh.fit.se.dtos.coupon.CouponRequestDTO;
import iuh.fit.se.dtos.coupon.CouponResponseDTO;
import iuh.fit.se.entities.coupon.Coupon;
import iuh.fit.se.mappers.CouponMapper;
import iuh.fit.se.repositories.CouponRepository;
import iuh.fit.se.services.CouponService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final CouponMapper couponMapper;

    public CouponServiceImpl(CouponRepository couponRepository, CouponMapper couponMapper) {
        this.couponRepository = couponRepository;
        this.couponMapper = couponMapper;
    }

    @Override
    public CouponResponseDTO createCoupon(CouponRequestDTO dto) {
        //Check for duplicate code
        if (couponRepository.findByCode(dto.getCode()).isPresent()) {
            throw new RuntimeException("Coupon code already exists!");
        }

        Coupon coupon = couponMapper.toEntity(dto);
        Coupon saved = couponRepository.save(coupon);
        return couponMapper.toResponseDTO(saved);
    }

    @Override
    public CouponResponseDTO updateCoupon(Long id, CouponRequestDTO dto) {
        Coupon existing = couponRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Coupon not found with id: " + id));

        existing.setCode(dto.getCode());
        existing.setDiscountAmount(dto.getDiscountAmount());
        existing.setMinOrderValue(dto.getMinOrderValue());
        existing.setStartDate(dto.getStartDate());
        existing.setEndDate(dto.getEndDate());
        existing.setActive(dto.isActive());

        return couponMapper.toResponseDTO(couponRepository.save(existing));
    }

    @Override
    public void deleteCoupon(Long id) {
        if (!couponRepository.existsById(id)) {
            throw new EntityNotFoundException("Coupon not found with id: " + id);
        }
        couponRepository.deleteById(id);
    }

    @Override
    public CouponResponseDTO getCouponById(Long id) {
        return couponRepository.findById(id)
                .map(couponMapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Coupon not found with id: " + id));
    }

    @Override
    public List<CouponResponseDTO> getAllCoupons() {
        return couponRepository.findAll()
                .stream()
                .map(couponMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CouponResponseDTO getCouponByCode(String code) {
        return couponRepository.findByCode(code)
                .map(couponMapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Coupon not found with code: " + code));
    }
}
