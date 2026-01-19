package com.n2s.infotech.service;

import com.n2s.infotech.dto.AddressDto;
import com.n2s.infotech.model.Address;
import com.n2s.infotech.model.User;
import com.n2s.infotech.repository.AddressRepository;
import com.n2s.infotech.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour gérer les adresses de livraison
 */
@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    /**
     * Récupère toutes les adresses d'un utilisateur
     */
    public List<AddressDto> getUserAddresses(Long userId) {
        return addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Crée une nouvelle adresse
     */
    public AddressDto createAddress(Long userId, AddressDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Si cette adresse doit être par défaut, retirer le flag des autres
        if (dto.getIsDefault()) {
            addressRepository.findByUserIdAndIsDefaultTrue(userId)
                    .ifPresent(addr -> {
                        addr.setIsDefault(false);
                        addressRepository.save(addr);
                    });
        }

        Address address = Address.builder()
                .user(user)
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .street(dto.getStreet())
                .city(dto.getCity())
                .postalCode(dto.getPostalCode())
                .country(dto.getCountry())
                .phone(dto.getPhone())
                .isDefault(dto.getIsDefault())
                .build();

        return convertToDto(addressRepository.save(address));
    }

    /**
     * Met à jour une adresse
     */
    public AddressDto updateAddress(Long addressId, Long userId, AddressDto dto) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // Vérifier que l'adresse appartient à l'utilisateur
        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only update your own addresses");
        }

        // Si cette adresse doit devenir par défaut, retirer le flag des autres
        if (dto.getIsDefault() && !address.getIsDefault()) {
            addressRepository.findByUserIdAndIsDefaultTrue(userId)
                    .ifPresent(addr -> {
                        addr.setIsDefault(false);
                        addressRepository.save(addr);
                    });
        }

        address.setFirstName(dto.getFirstName());
        address.setLastName(dto.getLastName());
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setPostalCode(dto.getPostalCode());
        address.setCountry(dto.getCountry());
        address.setPhone(dto.getPhone());
        address.setIsDefault(dto.getIsDefault());

        return convertToDto(addressRepository.save(address));
    }

    /**
     * Supprime une adresse
     */
    public void deleteAddress(Long addressId, Long userId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // Vérifier que l'adresse appartient à l'utilisateur
        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only delete your own addresses");
        }

        addressRepository.delete(address);
    }

    /**
     * Définit une adresse comme adresse par défaut
     */
    public AddressDto setDefaultAddress(Long addressId, Long userId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // Vérifier que l'adresse appartient à l'utilisateur
        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only modify your own addresses");
        }

        // Retirer le flag par défaut des autres adresses
        addressRepository.findByUserIdAndIsDefaultTrue(userId)
                .ifPresent(addr -> {
                    if (!addr.getId().equals(addressId)) {
                        addr.setIsDefault(false);
                        addressRepository.save(addr);
                    }
                });

        address.setIsDefault(true);
        return convertToDto(addressRepository.save(address));
    }

    /**
     * Convertit une Address en DTO
     */
    private AddressDto convertToDto(Address address) {
        return AddressDto.builder()
                .id(address.getId())
                .userId(address.getUser().getId())
                .firstName(address.getFirstName())
                .lastName(address.getLastName())
                .street(address.getStreet())
                .city(address.getCity())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .phone(address.getPhone())
                .isDefault(address.getIsDefault())
                .build();
    }
}

