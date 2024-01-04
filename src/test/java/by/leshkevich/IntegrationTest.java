package by.leshkevich;

import by.leshkevich.entity.Client;
import by.leshkevich.entity.Server;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IntegrationTest {

    @Test
    void testReturnResponse() throws Exception {
        // given
        List<Integer> dataList = new ArrayList<>();
        List<Integer> serverDataList = new ArrayList<>();
        Server server = new Server(serverDataList);
        int sizeListData = 100;

        for (int i = 1; i <= sizeListData; i++) {
            dataList.add(i);
        }
        List<Integer> listToCheck = new ArrayList<>(dataList);
        Client client = new Client(dataList, server);

        // when
        client.sendData();
        boolean actualCheckData = server.checkData(listToCheck);
        boolean actualCheckResponse = checkResponse(sizeListData, client.getAccumulator().get());

        // then
        assertAll(
                () -> assertTrue(actualCheckData),
                () -> assertTrue(actualCheckResponse)
        );
    }

    public boolean checkResponse(int sizeResponse, int accumulator) {
        int expectedSum = (1 + sizeResponse) * (sizeResponse / 2);
        return accumulator == expectedSum;
    }
}
