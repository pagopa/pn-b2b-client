package it.pagopa.pn.client.b2b.pa.parsing.parser.utils;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class PnTextSlidingWindow {
    private String originalText;
    private String slidedText;
    private String tokenStart;
    private String tokenEnd;
    private String discardValue;


    public void slideWindow(String pointToSlide) {
        int slidingIndex = slidedText.indexOf(pointToSlide);
        if(slidingIndex == -1)
            return;
        else
            slidingIndex = slidingIndex + pointToSlide.length();
        this.slidedText = slidedText.substring(slidingIndex);
    }

    public boolean isToDiscard(String toDiscard) {
        if(discardValue == null)
            return false;
        return discardValue.equals(toDiscard);
    }
}