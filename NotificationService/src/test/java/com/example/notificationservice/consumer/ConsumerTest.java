package com.example.notificationservice.consumer;

import com.example.notificationservice.dto.RechargeEvent;
import com.example.notificationservice.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConsumerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private RechargeConsumer rechargeConsumer;

    @Test
    void testReceiveRechargeEvent() {
        // 1. Setup
        RechargeEvent dummyEvent = new RechargeEvent();

        // 2. Execute the listener
        rechargeConsumer.receiveRechargeEvent(dummyEvent);

        // 3. Verify that the consumer successfully handed the event off to the service
        verify(notificationService, times(1)).sendNotification(dummyEvent);
    }
}