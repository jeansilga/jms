package grails.plugin.jms.test.servicelevel

import grails.plugin.spock.*
import grails.test.mixin.integration.Integration

import org.springframework.beans.factory.annotation.Autowired

import spock.lang.Specification

@Integration
class ServiceMethodListeningServiceSpec extends Specification {

	@Autowired
    def jmsService
	@Autowired
    def serviceMethodListeningService
    
    void testIt() {
        when:
        jmsService.send(service: 'serviceMethodListening', "a")
        then:
        serviceMethodListeningService.message == "a"
    }

}