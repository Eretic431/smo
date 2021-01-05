package app;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Buffer {
    private final int size;
    private int pointer;
    private final ArrayList<Request> requests;

    public Buffer(int size) {
        this.size = size;
        pointer = 0;
        requests = new ArrayList<>(size);
    }

    public ArrayList<Request> getRequests() {
        return requests;
    }

    public Request get(int i) {
        return requests.get(i);
    }

    private boolean findFreePlaceInBuffer(Request request, double currTime, int staticPointer) {
        for (int i = pointer; i < size; i++) {
            if (requests.get(i) == null) {
                requests.set(i, request);
                request.setTimeInBuffer(currTime);
                incrementPointer();
                return true;
            }
            incrementPointer();
        }
        for (int i = 0; i < staticPointer; i++) {
            if (requests.get(i) == null) {
                requests.set(i, request);
                request.setTimeInBuffer(currTime);
                incrementPointer();
                return true;
            }
            incrementPointer();
        }
        return false;
    }

    public Pair<Integer, ArrayList<Integer>> put(Request request, double currTime) {
        ArrayList<Integer> info = new ArrayList<>();
        info.add(-1);
        if (requests.size() < size) {
            requests.add(pointer, request);
            incrementPointer();
            return new Pair<>(0, info);
        } else {
            int staticPointer = pointer;
            if (findFreePlaceInBuffer(request, currTime, staticPointer)) {
                return new Pair<>(0, info);
            } else {
                ArrayList<Integer> rejectedReqInfo = removeNewestRequest();
                pointer = staticPointer;
                if (findFreePlaceInBuffer(request, currTime, staticPointer)) {
                    return new Pair<>(1, rejectedReqInfo);
                }
            }

        }
        return new Pair<>(2, info);
    }

    private ArrayList<Integer> removeNewestRequest() {
        int currIndex = 0;
        for (int i = 1; i < size; i++) {
            if (requests.get(i).getTimeInBuffer() > requests.get(currIndex).getTimeInBuffer()) {
                currIndex = i;
            }
        }
        int sourceNumber = requests.get(currIndex).getSourceNumber();
        int reqNum = requests.get(currIndex).getNumber();
        ArrayList<Integer> reqInfo = new ArrayList<Integer>();
        reqInfo.add(sourceNumber);
        reqInfo.add(reqNum);
        requests.set(currIndex, null);
        return reqInfo;
    }

    public Request getRequestForExecution() {
        if (isEmpty()) {
            System.out.println("empty");
        } else {
            int highestSourceNum = Integer.MAX_VALUE;
            for (Request request : requests) {
                if (request != null) {
                    if (request.getSourceNumber() < highestSourceNum) {
                        highestSourceNum = request.getSourceNumber();
                    }
                }
            }

            final Set<Integer> indexes = new HashSet<>();
            for (int i = 0; i < requests.size(); i++) {
                if (requests.get(i) != null) {
                    if (requests.get(i).getSourceNumber() == highestSourceNum) {
                        indexes.add(i);
                    }
                }
            }

            Iterator<Integer> indexIt = indexes.iterator();
            int currIndex = -1;
            if (indexIt.hasNext()) {
                currIndex = indexIt.next();
            } else {
                return null;
            }
            while (indexIt.hasNext()) {
                int nextIndex = indexIt.next();
                if (requests.get(nextIndex).getTimeInBuffer() > requests.get(currIndex).getTimeInBuffer()) {
                    currIndex = nextIndex;
                }
            }

            Request last = requests.get(currIndex);
            requests.set(currIndex, null);
            return last;
        }

        return null;
    }

    private boolean isFull() {
        return size == requests.size();
    }

    public boolean isEmpty() {
        return requests.isEmpty();
    }

    private void incrementPointer() {
        pointer++;
        if (pointer == size) {
            pointer = 0;
        }
    }

    public int getIndexPointer() {
        return pointer;
    }
}
