package execution;


import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CapMetroConsumerTest {

    @Test
    public void testApp() {
        DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE;
        System.out.println(dtf.format(LocalDateTime.now()));
    }

}
