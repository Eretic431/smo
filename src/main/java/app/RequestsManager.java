package app;

import java.util.ArrayList;
import java.util.List;

public class RequestsManager {
    private final Source[] sources;
    private final Request[] sourcesRequests;
    private final double[] timesToWait;
    private int counter = 0;

    public RequestsManager(final int amountOfSources,
                           final double alpha,
                           final double beta) {
        this.sources = new Source[amountOfSources];
        this.sourcesRequests = new Request[amountOfSources];
        this.timesToWait = new double[amountOfSources];
        initSources(alpha, beta);
    }

    private void initSources(final double alpha, final double beta) {
        for (int i = 0; i < sources.length; ++i) {
            sources[i] = new Source(i, alpha, beta);
        }
    }

    public Pair<Double, List<Request>> getNextRequest(final double currentTime) {
        generateRequestsIfNeed(currentTime);
        final int minTimeIndex = getMinTimeIndex();
        final double minWaitTime = timesToWait[minTimeIndex];
        declineWaitTime(minTimeIndex);
        final List<Request> generatedRequests = getGeneratedRequests(currentTime);
        return new Pair<>(minWaitTime, generatedRequests);
    }

    private List<Request> getGeneratedRequests(final double currentTime) {
        final List<Request> ready = new ArrayList<>();
        for (int i = 0; i < sourcesRequests.length; i++) {
            if (sourcesRequests[i] != null && sourcesRequests[i].getGenerationTime() <= currentTime) {
                ready.add(sourcesRequests[i]);
                sourcesRequests[i] = null;
            }
        }
        return ready;
    }

    private void declineWaitTime(final int minTimeIndex) {
        final double waitTime = timesToWait[minTimeIndex];
        for (int i = 0; i < sources.length; ++i) {
            timesToWait[i] -= waitTime;
        }
    }

    private int getMinTimeIndex() {
        int minIndex = 0;
        for (int i = 1; i < sources.length; ++i) {
            if (timesToWait[i] < timesToWait[minIndex]) {
                minIndex = i;
            }
        }
        return minIndex;
    }

    private void generateRequestsIfNeed(final double currentTime) {
        for (int i = 0; i < sources.length; ++i) {
            if (timesToWait[i] <= 0) {
                final Pair<Double, Request> generatedRequest = sources[i].generate(currentTime, counter);
                counter++;
                final double timeToWait = generatedRequest.getFirst();
                final Request newRequest = generatedRequest.getSecond();
                sourcesRequests[i] = newRequest;
                timesToWait[i] = timeToWait;
            }
        }
    }
}