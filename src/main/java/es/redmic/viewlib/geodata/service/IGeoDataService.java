package es.redmic.viewlib.geodata.service;

import java.util.List;

import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.models.es.geojson.common.dto.GeoJSONFeatureCollectionDTO;
import es.redmic.viewlib.common.dto.MetaDTO;
import es.redmic.viewlib.common.service.IBaseService;

public interface IGeoDataService<TModel, TDTO, TQueryDTO> extends IBaseService<TModel, TDTO, TQueryDTO> {

	public MetaDTO<?> findById(String id, String parentId);

	public GeoJSONFeatureCollectionDTO find(TQueryDTO query, String parentId);

	public List<String> suggest(TQueryDTO queryDTO, String parentId);

	public GeoJSONFeatureCollectionDTO mget(MgetDTO dto, String parentId);
}