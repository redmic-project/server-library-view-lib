package es.redmic.viewlib.common.controller;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.redmic.brokerlib.avro.common.CommonDTO;
import es.redmic.exception.databinding.DTONotValidException;
import es.redmic.models.es.common.dto.ElasticSearchDTO;
import es.redmic.models.es.common.dto.SuperDTO;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.viewlib.common.dto.MetaDTO;
import es.redmic.viewlib.common.service.IBaseService;

public abstract class RController<TModel extends BaseES<?>, TDTO extends CommonDTO, TQueryDTO extends SimpleQueryDTO> {

	protected Class<TDTO> typeOfTDTO;

	protected Class<TQueryDTO> typeOfTQueryDTO;

	private Set<String> fieldsExcludedOnQuery = new HashSet<String>();

	private Map<String, Object> fixedQuery = new HashMap<String, Object>();

	IBaseService<TModel, TDTO, TQueryDTO> service;

	public RController(IBaseService<TModel, TDTO, TQueryDTO> service) {
		this.service = service;
		defineTypeOfArguments();
	}

	@RequestMapping(value = "", method = RequestMethod.GET)
	@ResponseBody
	public SuperDTO _search(@RequestParam(required = false, value = "fields") String[] fields,
			@RequestParam(required = false, value = "text") String text,
			@RequestParam(required = false, value = "from") Integer from,
			@RequestParam(required = false, value = "size") Integer size) {

		return new ElasticSearchDTO(service.find(fields, text, from, size, fixedQuery, fieldsExcludedOnQuery));
	}

	@RequestMapping(value = "/_search", method = RequestMethod.POST)
	@ResponseBody
	public SuperDTO _advancedSearch(@Valid @RequestBody TQueryDTO queryDTO, BindingResult bindingResult) {

		if (bindingResult != null && bindingResult.hasErrors())
			throw new DTONotValidException(bindingResult);

		return new ElasticSearchDTO(service.find(queryDTO, fixedQuery, fieldsExcludedOnQuery));
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public SuperDTO _get(@PathVariable("id") String id) {

		MetaDTO<?> response = service.findById(id);
		return new ElasticSearchDTO(response, response.get_source() == null ? 0 : 1);
	}

	@RequestMapping(value = "/_mget", method = RequestMethod.POST)
	@ResponseBody
	public SuperDTO _mget(@Valid @RequestBody MgetDTO dto, BindingResult errorDto) {

		if (errorDto.hasErrors())
			throw new DTONotValidException(errorDto);

		return new ElasticSearchDTO(service.mget(dto));
	}

	@RequestMapping(value = "/_suggest", method = RequestMethod.GET)
	@ResponseBody
	public SuperDTO _suggest(@RequestParam(required = false, value = "fields") String[] fields,
			@RequestParam("text") String text, @RequestParam(required = false, value = "size") Integer size) {

		return new ElasticSearchDTO(service.suggest(fields, text, size, fixedQuery, fieldsExcludedOnQuery));
	}

	@RequestMapping(value = "/_suggest", method = RequestMethod.POST)
	@ResponseBody
	public SuperDTO _advancedSuggest(@Valid @RequestBody TQueryDTO queryDTO, BindingResult bindingResult) {

		return new ElasticSearchDTO(service.suggest(queryDTO, fixedQuery, fieldsExcludedOnQuery));
	}

	@RequestMapping(value = { "${controller.mapping.FILTER_SCHEMA}" }, method = RequestMethod.GET)
	@ResponseBody
	public ElasticSearchDTO getFilterSchema() {

		return new ElasticSearchDTO(service.getFilterSchema(fieldsExcludedOnQuery), 1);
	}

	@SuppressWarnings("unchecked")
	private void defineTypeOfArguments() {

		int numberOfArguments = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments().length;
		Type[] arguments = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();

		switch (numberOfArguments) {
		case 4:
			this.typeOfTDTO = (Class<TDTO>) (arguments[2]);
			this.typeOfTQueryDTO = (Class<TQueryDTO>) (arguments[3]);
			break;
		case 3:
			this.typeOfTDTO = (Class<TDTO>) (arguments[1]);
			this.typeOfTQueryDTO = (Class<TQueryDTO>) (arguments[2]);
			break;
		}
	}

	protected void setFieldsExcludedOnQuery(Set<String> fieldsExcludedOnQuery) {
		this.fieldsExcludedOnQuery = fieldsExcludedOnQuery;
	}

	protected void setFixedQuery(Map<String, Object> fixedQuery) {
		this.fixedQuery.putAll(fixedQuery);
	}

	protected void setFixedQuery(String term, Object value) {
		fixedQuery.put(term, value);
	}
}
