log4j = {
	error ''
	debug 'grails.plugin.jms'
	debug 'grails.app.service'
}

dataSource {
	pooled = true
	driverClassName = "org.hsqldb.jdbcDriver"
	username = "sa"
	password = ""
}
hibernate {
	cache.use_second_level_cache = true
	cache.use_query_cache = true
	cache.provider_class = 'com.opensymphony.oscache.hibernate.OSCacheProvider'
}

environments {
	development {
		dataSource {
			dbCreate = "create-drop"
			url = "jdbc:hsqldb:mem:devDB"
		}
	}
	test {
		dataSource {
			dbCreate = "create-drop"
			url = "jdbc:hsqldb:mem:testDb"
		}
	}
}

beans {
	/* If you want to define an executor and/or disable the auto-shutdown mechanism for the async executor.
	jmsService {
		asyncReceiverExecutor = Executors.newSingleThreadExecutor()
		asyncReceiverExecutorShutdown = false
	}
   */
}

jms {
	disabled = false

	templates {
		other {
			meta {
				parentBean = 'standardJmsTemplate'
			}
			connectionFactoryBean = "otherJmsConnectionFactory"
		}
		transacted {
			meta {
				parentBean = 'standardJmsTemplate'
			}
			sessionTransacted = true
		}
	}
	containers {
		other {
			meta {
				parentBean = 'standardJmsListenerContainer'
			}
			connectionFactoryBean = "otherJmsConnectionFactory"
		}
		other {
			meta {
				parentBean = 'standardJmsListenerContainer'
			}
			connectionFactoryBean = "otherJmsConnectionFactory"
		}
		transacted {
			meta {
				parentBean = 'standardJmsListenerContainer'
			}
			transactionManagerBean = "transactionManager"
			sessionTransacted = true
		}
	}
	adapters {
		other {
			meta {
				parentBean = 'standardJmsListenerAdapter'
			}
			messageConverter = null
		}
	}
}

grails {
	doc {
		title = "Grails JMS Plugin"
		subtitle = "JMS integration for Grails"
		authors = "Grails Plugin Collective"
		copyright = "Copies of this document may be made for your own use and for distribution to others, provided that you do not charge any fee for such copies and further provided that each copy contains this Copyright Notice, whether distributed in print or electronically."
		footer = "Developed by the <a href='http://gpc.github.com'>Grails Plugin Collective</a>"
		license = "Apache License 2.0"
	}
}