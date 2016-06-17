package grails.plugin.jms.test.config

import grails.plugin.spock.*
import grails.test.mixin.integration.Integration

import org.springframework.beans.factory.annotation.Autowired

import spock.lang.Specification

@Integration
class OtherListenerServiceSpec extends Specification {

	@Autowired
    def jmsService
	@Autowired
    def otherListenerService
    
    void testIt() {
        when:
        jmsService.send(service: 'otherListener', "a", "other")
        then:
        otherListenerService.message
    }

}