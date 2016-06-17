package grails.plugin.jms.test.reply

import grails.plugin.spock.*
import grails.test.mixin.integration.Integration

import org.springframework.beans.factory.annotation.Autowired

import spock.lang.Specification

@Integration
class ReplyingListenerServiceSpec extends Specification {

	@Autowired
    def jmsService
	@Autowired
    def replyingListenerService
    
    void testIt() {
        when:
        jmsService.send(service: 'replyingListener', method: 'initial', 1) {
            it.JMSReplyTo = createDestination(service: 'replyingListener', method: 'reply')
            it
        }
        then:
        replyingListenerService.message == 1
    }
}