package by.leshkevich.entity;

import by.leshkevich.exception.ClientException;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Data
public class Client {

    private final Logger logger = LogManager.getLogger(Client.class.getName());

    private static final int MIN_INDEX = 0;
    private static final int MAX_TIMEOUT = 401;
    public static final int MIN_TIMEOUT = 100;

    private final List<Integer> dataList;
    private final Server server;
    private final AtomicInteger accumulator;
    private final Lock lock;

    public Client(List<Integer> dataList, Server server) {
        accumulator = new AtomicInteger();
        lock = new ReentrantLock();
        this.dataList = dataList;
        this.server = server;
    }

    private Integer findDataByIndex(int index) throws ClientException {
        lock.lock();
        try {
            if (index >= MIN_INDEX && index <= dataList.size() - 1) {
                return dataList.remove(index);
            } else {
                throw new ClientException("invalid index");
            }
        } finally {
            lock.unlock();
        }
    }

    public void addResponseToCheck(Response response) {
        accumulator.addAndGet(response.getValue());
    }

    public boolean checkResponse(int sizeResponse) {
        int actual = accumulator.get();
        int expectedSum = (1 + sizeResponse) * (sizeResponse / 2);
        return actual == expectedSum;
    }

    public void sendData() throws InterruptedException {
        ExecutorService executorServiceClient = Executors.newFixedThreadPool(5);
        List<Integer> copyDataListToCheck = new ArrayList<>(dataList);
        List<Callable<Response>> tasks = new ArrayList<>();

        dataList.forEach(e -> tasks.add(() -> {
            int processTime = new Random().nextInt(MAX_TIMEOUT) + MIN_TIMEOUT;
            Thread.sleep(processTime);
            int random = new Random().nextInt(getDataList().size());
            Integer dataClient = findDataByIndex(random);
            Request request = new Request(dataClient);
            logger.info("Request to server: {}", request);
            Response response = server.processRequest(request);
            logger.info("Response server: {}", response);
            return response;
        }));

        List<Future<Response>> futures = executorServiceClient.invokeAll(tasks);
        executorServiceClient.shutdown();

        futures.parallelStream().forEach(future->{
            Response response;
            try {
                response = future.get();
                this.addResponseToCheck(response);
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Error while receiving response: {}",future);
                Thread.currentThread().interrupt();
            }
        });

        logger.info("Checking size dataList: {}", dataList.size());
        logger.info("Checking accumulator size: {}", accumulator);
        logger.info("Checking data on the server : {}", server.checkData(copyDataListToCheck));
    }
}
