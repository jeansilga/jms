package grails.plugin.jms.test.domain

import grails.plugin.jms.test.*
import grails.plugin.spock.*
import grails.test.mixin.integration.Integration

import org.springframework.beans.factory.annotation.Autowired

import spock.lang.Specification

@Integration
class DomainServiceSpec extends Specification {

    static transactional = false
	@Autowired
    def jmsService
	@Autowired
    def domainService
    
    void testIt() {
        given:
        def p = new Person(
            name: "p",
        )
        [1,2,3,4].each {
            p.addToThings(new Thing(name: it.toString()))
        }
        p.save(flush: true)
        when:
        jmsService.send(service: 'domain', 'p')
        then:
        domainService.message.sort() == ['1', '2', '3', '4']
    }
}
