package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.papertracker.model.PaperTrackerOutputsResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.papertracker.model.TrackingErrorsResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.papertracker.model.TrackingsRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.papertracker.model.TrackingsResponse;

public interface IPnPaperTrackerClient {

    //PaperTrackerEventApi
    TrackingsResponse retrieveTrackerEvents(TrackingsRequest trackingsRequest);

    //PaperTrackerOutputApi
    PaperTrackerOutputsResponse retrieveTrackerOutputs(TrackingsRequest trackingsRequest);

    //PaperTrackerErrorApi
    TrackingErrorsResponse retrieveTrackerErrors(TrackingsRequest trackingsRequest);

    //PaperTrackerAttemptApi
    TrackingsResponse retrieveTrackingsByAttemptId(String attemptId);
}
