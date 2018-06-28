package es.redmic.viewlib.common.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import es.redmic.brokerlib.avro.common.CommonDTO;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.listener.SendListener;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.viewlib.common.service.IBaseService;

public abstract class RWController<TModel extends BaseES<?>, TDTO extends CommonDTO, TQueryDTO extends SimpleQueryDTO>
		extends RController<TModel, TDTO, TQueryDTO> {

	private static Logger logger = LogManager.getLogger();

	@Autowired
	protected KafkaTemplate<String, Event> kafkaTemplate;

	public RWController(IBaseService<TModel, TDTO, TQueryDTO> service) {
		super(service);
	}

	protected void publishFailedEvent(Event event, String topic) {

		logger.info("sending FailedEvent='{}' to topic='{}'", event, topic);

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(topic, event.getAggregateId(), event);

		future.addCallback(new SendListener());
	}

	protected void publishConfirmedEvent(Event event, String topic) {

		logger.info("sending ConfirmEvent='{}' to topic='{}'", event, topic);

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(topic, event.getAggregateId(), event);

		future.addCallback(new SendListener());
	}
}
