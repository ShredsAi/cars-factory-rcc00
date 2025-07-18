package ai.shreds.domain.services;

import ai.shreds.domain.entities.DomainGPSDeviceEntity;
import ai.shreds.domain.exceptions.DomainDeviceNotAuthorizedException;
import ai.shreds.domain.ports.DomainInputPortValidateDevice;
import ai.shreds.domain.ports.DomainOutputPortGPSDeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@Transactional(readOnly = true)
public class DomainDeviceValidationService implements DomainInputPortValidateDevice {

    private static final Pattern IMEI_PATTERN = Pattern.compile("^\\d{15}$");

    private final DomainOutputPortGPSDeviceRepository gpsDeviceRepository;

    public DomainDeviceValidationService(@Lazy DomainOutputPortGPSDeviceRepository gpsDeviceRepository) {
        this.gpsDeviceRepository = gpsDeviceRepository;
    }

    @Override
    public boolean validateDeviceAuthorization(String imei) {
        if (!validateImeiFormat(imei)) {
            return false;
        }
        Optional<DomainGPSDeviceEntity> device = gpsDeviceRepository.findDeviceByImei(imei);
        return device.isPresent() && device.get().isActive();
    }

    @Override
    public boolean validateImeiFormat(String imei) {
        return imei != null && IMEI_PATTERN.matcher(imei).matches();
    }

    @Override
    public DomainGPSDeviceEntity checkDeviceRegistration(String imei) {
        return gpsDeviceRepository.findDeviceByImei(imei)
                .filter(DomainGPSDeviceEntity::isActive)
                .orElseThrow(() -> new DomainDeviceNotAuthorizedException(imei, "Device with IMEI " + imei + " is not registered or inactive."));
    }

    @Override
    @Transactional
    public void updateDeviceCommunication(String imei, Instant timestamp) {
        DomainGPSDeviceEntity device = checkDeviceRegistration(imei);
        device.setLastCommunicationTimestamp(timestamp);
        gpsDeviceRepository.saveDevice(device);
    }
}
