package es.redmic.viewlib.geodata.repository;

import java.util.List;

import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.models.es.geojson.base.Feature;
import es.redmic.models.es.geojson.wrapper.GeoHitWrapper;
import es.redmic.models.es.geojson.wrapper.GeoHitsWrapper;
import es.redmic.models.es.geojson.wrapper.GeoSearchWrapper;
import es.redmic.viewlib.common.repository.IBaseRepository;

public interface IGeoDataRepository<TModel extends Feature<?, ?>, TQueryDTO extends SimpleQueryDTO>
		extends IBaseRepository<TModel> {

	// R

	public GeoHitWrapper<?> findById(String id);

	public List<String> suggest(TQueryDTO queryDTO);

	public GeoHitsWrapper<?> mget(MgetDTO dto);

	public GeoSearchWrapper<?> find(TQueryDTO queryDTO);
}