package com.example.passwordrecovery_service.service;

import com.example.passwordrecovery_service.entity.PassengerCredentialsDTO;
import com.example.passwordrecovery_service.entity.UserCredential;
import com.example.passwordrecovery_service.repository.UserCredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class PassengerCredentialService {

    private final RestTemplate restTemplate;
    private final UserCredentialRepository userCredentialRepository;

    @Autowired
    public PassengerCredentialService(RestTemplate restTemplate, UserCredentialRepository userCredentialRepository) {
        this.restTemplate = restTemplate;
        this.userCredentialRepository = userCredentialRepository;
    }

    // Fetch all passenger credentials from external service
    public List<PassengerCredentialsDTO> getAllPassengerCredentialsFromAPI() {
        String url = "http://localhost:8081/api/reservations/passenger-credentials";
        PassengerCredentialsDTO[] response = restTemplate.getForObject(url, PassengerCredentialsDTO[].class);
        return List.of(response);
    }

    // Fetch credentials by email from external service
    public PassengerCredentialsDTO getPassengerCredentialFromAPIByEmail(String email) {
        List<PassengerCredentialsDTO> credentials = getAllPassengerCredentialsFromAPI();
        return credentials.stream()
                .filter(credential -> credential.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    // Save user credentials in local database
    public UserCredential saveCredentialToLocalDatabase(PassengerCredentialsDTO dto) {
        UserCredential userCredential = new UserCredential();
        userCredential.setEmail(dto.getEmail());
        userCredential.setPassword(dto.getPassword());
        return userCredentialRepository.save(userCredential);
    }

    // Ensure credentials are saved in the local database
    public UserCredential getOrSaveCredentials(String email) {
        Optional<UserCredential> existing = userCredentialRepository.findByEmail(email);
        if (existing.isPresent()) {
            return existing.get();
        } else {
            // Fetch from external API and save to local DB
            PassengerCredentialsDTO dto = getPassengerCredentialFromAPIByEmail(email);
            if (dto != null) {
                return saveCredentialToLocalDatabase(dto);
            }
        }
        return null;
    }
}
