package it.pagopa.interop.notification;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OptInNotificationConfig {
    private String role;
    private List<String> inAppNotifications = new ArrayList<>();
    private List<String> emailNotifications = new ArrayList<>();
}
