package ci.smile.colloque.helper.dto.transformer;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import ci.smile.colloque.dao.entity.Exposer;
import ci.smile.colloque.helper.dto.ExposerDto;

@Mapper
public interface ExposerTransformer {

	ExposerTransformer INSTANCE = Mappers.getMapper( ExposerTransformer.class ); 
	
	//-- Entity vers Dto
	
	ExposerDto toDto(Exposer exposer); 
	List<ExposerDto> toDtos(List<Exposer> exposer );
	
	//-- Dto vers Entity
	
	Exposer toEntity( ExposerDto exposerDto);
}
