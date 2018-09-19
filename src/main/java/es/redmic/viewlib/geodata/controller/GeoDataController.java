package es.redmic.viewlib.geodata.controller;

import javax.validation.Valid;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.redmic.brokerlib.avro.geodata.common.FeatureDTO;
import es.redmic.exception.databinding.DTONotValidException;
import es.redmic.models.es.common.dto.ElasticSearchDTO;
import es.redmic.models.es.common.dto.SuperDTO;
import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.models.es.geojson.base.Feature;
import es.redmic.viewlib.common.controller.RController;
import es.redmic.viewlib.geodata.dto.GeoMetaDTO;
import es.redmic.viewlib.geodata.service.IGeoDataService;

public class GeoDataController<TModel extends Feature<?, ?>, TDTO extends FeatureDTO<?, ?>, TQueryDTO extends SimpleQueryDTO>
		extends RController<TModel, TDTO, TQueryDTO> {

	IGeoDataService<TModel, TDTO, TQueryDTO> service;

	public GeoDataController(IGeoDataService<TModel, TDTO, TQueryDTO> service) {
		super(service);
		this.service = service;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public SuperDTO _get(@PathVariable("activityId") String activityId, @PathVariable("id") String id) {

		GeoMetaDTO<?> response = service.findById(id, activityId);
		return new ElasticSearchDTO(response, response.get_source() == null ? 0 : 1);
	}

	@RequestMapping(value = "/_mget", method = RequestMethod.POST)
	@ResponseBody
	public SuperDTO _mget(@PathVariable("activityId") String activityId, @Valid @RequestBody MgetDTO dto,
			BindingResult errorDto) {

		if (errorDto.hasErrors())
			throw new DTONotValidException(errorDto);

		return new ElasticSearchDTO(service.mget(dto, activityId));
	}

	@RequestMapping(value = "/_suggest", method = RequestMethod.POST)
	@ResponseBody
	public SuperDTO _advancedSuggest(@PathVariable("activityId") String activityId,
			@Valid @RequestBody TQueryDTO queryDTO, BindingResult bindingResult) {

		return new ElasticSearchDTO(service.suggest(queryDTO, activityId));
	}

	@RequestMapping(value = "/_search", method = RequestMethod.POST)
	@ResponseBody
	public SuperDTO _advancedSearch(@PathVariable("activityId") String activityId,
			@Valid @RequestBody TQueryDTO queryDTO, BindingResult bindingResult) {

		if (bindingResult != null && bindingResult.hasErrors())
			throw new DTONotValidException(bindingResult);

		return new ElasticSearchDTO(service.find(queryDTO, activityId));
	}
}
