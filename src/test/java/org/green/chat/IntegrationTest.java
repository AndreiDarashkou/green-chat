package org.green.chat;

import org.green.chat.config.RSocketConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@Import(RSocketConfiguration.class)
@ContextConfiguration(initializers = DatabaseInitializer.class)
public class IntegrationTest {

    @Autowired
    protected RSocketRequester requester;
}
