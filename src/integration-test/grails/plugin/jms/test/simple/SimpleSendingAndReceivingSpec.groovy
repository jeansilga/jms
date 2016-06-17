package grails.plugin.jms.test.simple

import grails.plugin.spock.*
import grails.test.mixin.integration.Integration

import org.springframework.beans.factory.annotation.Autowired

import spock.lang.Specification


@Integration
class SimpleSendingAndReceivingSpec extends Specification {

	@Autowired
    def simpleSendingService
	@Autowired
    def simpleReceivingService
    
    void testQueue() {
        when:
        simpleSendingService.sendToQueue("a")
        then:
        simpleReceivingService.message == "a"
    }

    void testSubscriber() {
        when:
        simpleSendingService.sendToTopic("a")
        then:
        simpleReceivingService.message == "a"
    }

}