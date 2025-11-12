package it.pagopa.pn.cucumber.steps.paperTracker.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@ToString
@Getter
public class NotificationEvent implements Comparable<NotificationEvent> {
    private String deliveryDetailCode;
    private List<String> attachmentUrlName;
    private String failureCause;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotificationEvent)) return false;
        NotificationEvent that = (NotificationEvent) o;

        if (!Objects.equals(deliveryDetailCode, that.deliveryDetailCode)) {
            return false;
        }

        if (!Objects.equals(failureCause, that.failureCause)) {
            return false;
        }

        List<String> thisList = new ArrayList<>(attachmentUrlName);
        List<String> thatList = new ArrayList<>(that.attachmentUrlName);

        Collections.sort(thisList);
        Collections.sort(thatList);

        return thisList.equals(thatList);
    }

    public boolean equalsRelaxed(NotificationEvent other) {
        if (other == null) return false;

        if (!this.deliveryDetailCode.equals(other.deliveryDetailCode)) {
            return false;
        }

        if (this.attachmentUrlName.size() != other.attachmentUrlName.size()) {
            return false;
        }

        if (!Objects.equals(failureCause, other.failureCause)) {
            return false;
        }

        for (String attachmentUrlNameOther : other.attachmentUrlName) {
            boolean found = this.attachmentUrlName.stream()
                    .anyMatch(tagThis -> tagThis.startsWith(attachmentUrlNameOther));
            if (!found) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        List<String> sortedList = new ArrayList<>(attachmentUrlName);
        Collections.sort(sortedList);
        return Objects.hash(deliveryDetailCode, sortedList);
    }

    @Override
    public int compareTo(NotificationEvent other) {
        if (this.deliveryDetailCode == null && other.deliveryDetailCode == null) return 0;
        if (this.deliveryDetailCode == null) return -1;
        if (other.deliveryDetailCode == null) return 1;
        return this.deliveryDetailCode.compareTo(other.deliveryDetailCode);
    }
}