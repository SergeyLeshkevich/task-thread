package by.leshkevich.entity;

import by.leshkevich.exception.ClientException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class ClientTest {

    @Test
    void shouldValueAccumulatorOne() {
        // given
        Client client = new Client(null, null);
        Response response = new Response(1);
        int expected = 1;

        // when
        client.addResponseToCheck(response);
        int actual = client.getAccumulator().get();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldValueAccumulatorTwo() {
        // given
        Client client = new Client(null, null);
        Response response = new Response(1);
        Response response2 = new Response(1);
        int expected = 2;

        // when
        client.addResponseToCheck(response);
        client.addResponseToCheck(response2);
        int actual = client.getAccumulator().get();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldReturnTrue() {
        // given
        int sizeListData = 100;
        Client client = new Client(null, null);
        Response response = new Response();
        for (int i = 1; i <= sizeListData; i++) {
            response.setValue(i);
            client.addResponseToCheck(response);
        }

        // when
        boolean actual = client.checkResponse(sizeListData);

        // then
        assertTrue(actual);
    }

    @Test
    void checkFindDataByIndex() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // given
        List<Integer> dataList = new ArrayList<>();
        dataList.add(1);
        dataList.add(2);
        int expected = 2;
        Client client = new Client(dataList, null);
        Method findDataByIndex = client.getClass().getDeclaredMethod("findDataByIndex", int.class);
        findDataByIndex.setAccessible(true);

        // when
        int actual = (int) findDataByIndex.invoke(client, 1);

        // then
        assertThat(actual).isEqualTo(expected);
        assertThat(dataList).hasSize(1);
    }

    @Test
    void shouldClientException() throws NoSuchMethodException {
        // given
        List<Integer> dataList = new ArrayList<>();
        Client client = new Client(dataList, null);
        Method findDataByIndex = client.getClass().getDeclaredMethod("findDataByIndex", int.class);
        findDataByIndex.setAccessible(true);

        // then
        assertThrows(ClientException.class, () -> {
            try {
                findDataByIndex.invoke(client, 1);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw e.getCause();
            }
        },"invalid index");
    }
}