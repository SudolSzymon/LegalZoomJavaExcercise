package com.szymon.javaexcercise;

import com.szymon.javaexcercise.web.SorterController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JavaexcerciseApplicationTests {

    @Autowired
    private SorterController controller;

    @Test
    public void contextLoads() {
        assert (controller != null);
    }

}
