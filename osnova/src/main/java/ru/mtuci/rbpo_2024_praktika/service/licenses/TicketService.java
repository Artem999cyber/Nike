package ru.mtuci.rbpo_2024_praktika.service.licenses;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.mtuci.rbpo_2024_praktika.dto.Ticket;
import ru.mtuci.rbpo_2024_praktika.model.Device;
import ru.mtuci.rbpo_2024_praktika.model.License;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Service
public class TicketService {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    public Ticket generateTicket(License license, Device device) {
        Date currentDate = new Date();
        Date activationDate = license.getFirst_activation_date();
        Date expirationDate = new Date(activationDate.getTime() + license.getDuration().toMillis());
        String signature = generateSignature(
                String.valueOf(currentDate.getTime()),
                license.getDuration().toMillis() + "",
                String.valueOf(activationDate.getTime()),
                String.valueOf(expirationDate.getTime()),
                license.getUser_id().getId().toString(),
                device.getId().toString()
        );
        return new Ticket(
                currentDate,
                license.getDuration(),
                activationDate,
                expirationDate,
                license.getUser_id().getId(),
                device.getId(),
                license.isBlocked(),
                signature
        );
    }

    public Ticket generateTicket(License license) {
        Date currentDate = new Date();
        Date activationDate = license.getFirst_activation_date();
        Date expirationDate = new Date(activationDate.getTime() + license.getDuration().toMillis());

        String signature = generateSignature(
                String.valueOf(currentDate.getTime()),
                license.getDuration().toMillis() + "",
                String.valueOf(activationDate.getTime()),
                String.valueOf(expirationDate.getTime()),
                license.getUser_id().getId().toString()
        );

        return new Ticket(
                currentDate,
                license.getDuration(),
                activationDate,
                expirationDate,
                license.getUser_id().getId(),
                null,
                license.isBlocked(),
                signature
        );
    }

    private String generateSignature(String... args) {
        try {
            StringBuilder dataToSignBuilder = new StringBuilder();
            for (String arg : args) {
                if (arg != null) {
                    dataToSignBuilder.append(arg).append("-");
                }
            }
            String dataToSign = dataToSignBuilder.substring(0, dataToSignBuilder.length() - 1);
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(keySpec);
            byte[] signatureBytes = mac.doFinal(dataToSign.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signatureBytes);
        } catch (Exception e) {
            return null;
        }
    }

}
