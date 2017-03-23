package execution;


import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.*;

public class AppTest {

    @Test
    public void testApp() {
        DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE;
        System.out.println(dtf.format(LocalDateTime.now()));
    }

}
