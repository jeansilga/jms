package grails.plugin.jms.test.transactions

import grails.plugin.jms.test.*
import grails.plugin.spock.*
import grails.test.mixin.integration.Integration

import java.util.concurrent.CountDownLatch

import org.springframework.beans.factory.annotation.Autowired

import spock.lang.*

@Integration
class TransactionalSendingSpec extends Specification {

    static transactional = false
	@Autowired
    def simpleSendingService
	@Autowired
    def simpleReceivingService

    static PAYLOAD = "payload"

    @Timeout(10)
    void "message sent in successful transaction to a non transacted template is sent straightaway"() {
        given: "a thread sending a messaging in a transaction, but waiting to close the transaction"
        def latch = new CountDownLatch(1)

        
        Thread.start {
            Person.withTransaction {
                simpleSendingService.sendToQueue(PAYLOAD) // default template is not transacted
                latch.await() 
            }
        }
        
        expect: "the message has been sent (even though the transaction is still open)"
        simpleReceivingService.getMessage(2) == PAYLOAD
        
        cleanup:
        latch.countDown()
    }
    
    @Timeout(10)
    void "message sent in successful transaction is sent on commit"() {
        given: "a thread sending a messaging in a transaction, but waiting to close the transaction"
        def latch = new CountDownLatch(1)
        def payload = "payload"
        
        Thread.start {
            Person.withTransaction {
                simpleSendingService.sendToQueue(PAYLOAD, 'transacted')
                latch.await() 
            }
        }
        
        expect: "the message has not been sent (transaction not committed)"
        simpleReceivingService.getMessage(2) == null
        
        when: "the transaction is allowed to commit"
        latch.countDown()
        
        then: "the message is sent"
        simpleReceivingService.getMessage(2) == PAYLOAD
    }

    @Timeout(10)
    void "message sent in unsuccessful transaction is not sent on rollback"() {
        
        given: "a thread sending a messaging in a transaction, but waiting to close the transaction after erroring"
        def latch = new CountDownLatch(1)
        def payload = "payload"
        
        Thread.start {
            Person.withTransaction {
                simpleSendingService.sendToQueue(PAYLOAD, 'transacted')
                latch.await()
                throw new RuntimeException("Bang!!!!!")
            }
        }
        
        expect: "the message has not been sent (transaction not committed)"
        simpleReceivingService.getMessage(2) == null
        
        when: "the transaction is allowed to fail and rollback"
        latch.countDown()
        
        then: "the message is not sent"
        simpleReceivingService.getMessage(2) == null
    }
    

}