package grails.plugin.jms.test.simple

import static grails.plugin.jms.test.simple.SimpleQueueBrowserService.BROWSER_QUEUE
import grails.test.mixin.integration.Integration

import org.springframework.beans.factory.annotation.Autowired

import spock.lang.Specification

@Integration
class SimpleQueueBrowsingSpec extends Specification {
	
	@Autowired
    SimpleQueueBrowserService simpleQueueBrowserService
	@Autowired
    SimpleSendingService simpleSendingService


    def setup() {
        //simpleQueueBrowserService.purge()
    }

    def cleanup() {
        simpleQueueBrowserService.purge()
    }


    void send() {
        simpleSendingService.sendToGivenQueue(BROWSER_QUEUE, '1', null) { msg ->
            msg.setStringProperty 'aproperty', '1'
            msg
        }
        simpleSendingService.sendToGivenQueue(BROWSER_QUEUE, '2', null) { msg ->
            msg.setStringProperty 'aproperty', '2'
            msg
        }
        simpleSendingService.sendToGivenQueue(BROWSER_QUEUE, '3', null) { msg ->
            msg.setStringProperty 'aproperty', '3'
            msg
        }

    }
     
    def "Should be able to browse the contents of a Queue"() {
        when:
        send()
        simpleQueueBrowserService.browse()
        then:
        simpleQueueBrowserService.message == '1'
        simpleQueueBrowserService.message == '2'
        simpleQueueBrowserService.message == '3'
    }

    def "Should be able to browse the contents of a Queue and receive the raw javax#jms#Message "() {
        when:
        send()
        simpleQueueBrowserService.browseNoConvert()

        then:
        simpleQueueBrowserService.message.getStringProperty('aproperty') == '1'
        simpleQueueBrowserService.message.getStringProperty('aproperty') == '2'
        simpleQueueBrowserService.message.getStringProperty('aproperty') == '3'
    }

    def "Should be able to browse the contents of a Queue with a selector"() {
        when:
        send()
        simpleQueueBrowserService.browseSelected("aproperty='2'")

        then:
        simpleQueueBrowserService.message == '2'
        simpleQueueBrowserService.message == null
    }

    def "Should be able to browse the contents of a Queue with a selector and receive the raw javax#jms#Message "() {
        when:
        send()
        simpleQueueBrowserService.browseSelectedNotConvert("aproperty='2'")

        then:
        simpleQueueBrowserService.message.getStringProperty('aproperty') == '2'
        simpleQueueBrowserService.message == null
    }


    def "Should be able to browse the contents of a Queue and define a callback"() {
        when:
        send()
        simpleQueueBrowserService.browse() { msg ->
            "$msg:$msg"
        }

        then:
        simpleQueueBrowserService.message == '1:1'
        simpleQueueBrowserService.message == '2:2'
        simpleQueueBrowserService.message == '3:3'
    }

    def "Should be able to browse the contents of a Queue, define a callback and receive the raw javax#jms#Message"() {
        when:
        send()
        int cx = 0
        simpleQueueBrowserService.browseNoConvert() { msg ->
            cx++
            ['fromCallback' : msg.getStringProperty('aproperty') ]
        }

        then:
        cx == 3
        simpleQueueBrowserService.message.fromCallback == '1'
        simpleQueueBrowserService.message.fromCallback == '2'
        simpleQueueBrowserService.message.fromCallback == '3'
    }

    def "Should be able to browse the contents of a Queue with a selector and define a callback"() {
        when:
        send()
        simpleQueueBrowserService.browseSelected("aproperty='2'") { msg ->
            "$msg:$msg"
        }

        then:
        simpleQueueBrowserService.message == '2:2'
        simpleQueueBrowserService.message == null
    }

    def "Should be able to browse the contents of a Queue with a selector, define a callback and receive the raw javax#jms#Message "() {
        when:
        send()
        simpleQueueBrowserService.browseSelectedNotConvert("aproperty='2'") { javax.jms.Message msg ->
            ['fromCallback' : msg.getStringProperty('aproperty') ]
        }

        then:
        simpleQueueBrowserService.message.fromCallback == '2'
        simpleQueueBrowserService.message == null
    }
}
