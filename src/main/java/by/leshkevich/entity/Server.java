package by.leshkevich.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Data
public class Server {

    private static final int MAX_TIMEOUT = 901;
    private static final int MIN_TIMEOUT = 100;

    private final List<Integer> dataList;
    private final Lock lock;
    private final List<Integer> finalRequest;
    private Request request;

    public Server(List<Integer> dataList) {
        finalRequest = new ArrayList<>();
        lock = new ReentrantLock();
        this.dataList = dataList;
    }

    public Response processRequest(Request request) throws InterruptedException {
        int processTime = new Random().nextInt(MAX_TIMEOUT) + MIN_TIMEOUT;
        Thread.sleep(processTime);
        int value = request.getValue();
        dataList.add(value);
        finalRequest.add(value);

        return new Response(dataList.size());
    }

    public boolean checkData(List<Integer> dataList) {
        List<Integer> copyDataList = new ArrayList<>(dataList);
        copyDataList.sort(Integer::compareTo);
        finalRequest.sort(Integer::compareTo);
        return copyDataList.equals(finalRequest);
    }
}
