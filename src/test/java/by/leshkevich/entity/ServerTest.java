package by.leshkevich.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ServerTest {

    @Test
    void shouldCheckDataReturnTrue() throws InterruptedException {
        List<Integer> dataList = new ArrayList<>();
        Server server = new Server(dataList);
        server.processRequest(new Request(1));
        server.processRequest(new  Request(2));
        server.processRequest(new Request(3));

        boolean actual = server.checkData(List.of(1,2,3));

        assertThat(actual).isTrue();
    }

    @Test
    void shouldCheckDataReturnFalse() throws InterruptedException {
        List<Integer> dataList = new ArrayList<>();
        Server server = new Server(dataList);
        server.processRequest(new Request(1));
        server.processRequest(new  Request(2));
        server.processRequest(new Request(3));

        boolean actual = server.checkData(List.of(1,2));

        assertThat(actual).isFalse();
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForProcessRequestTest")
    void testProcessRequest(Request request) throws Exception {
        // given
        List<Integer> dataList = new ArrayList<>();
        Server server = new Server(dataList);
        server.setRequest(request);
        Response expected = new Response(1);

        // when
        Response actual = server.processRequest(request);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    static Stream<Arguments> getArgumentsForProcessRequestTest() {
        return Stream.of(
                Arguments.of(new Request(1))
        );
    }
}