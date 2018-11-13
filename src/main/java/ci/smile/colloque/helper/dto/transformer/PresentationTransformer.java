package ci.smile.colloque.helper.dto.transformer;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import ci.smile.colloque.dao.entity.Colloque;
import ci.smile.colloque.dao.entity.Conferencier;
import ci.smile.colloque.dao.entity.Exposer;
import ci.smile.colloque.dao.entity.Presentation;
import ci.smile.colloque.helper.dto.PresentationDto;

@Mapper
public interface PresentationTransformer {

	PresentationTransformer INSTANCE = Mappers.getMapper( PresentationTransformer.class ); 
	
	//-- Entity vers Dto
	
	//--- Multiple Mapping
			@Mappings({
				
				@Mapping(source = "conferencier.participantId", target = "conferencierId"),
				@Mapping(source = "colloque.id", target = "colloqueId"),
				@Mapping(source = "exposer.id", target = "exposerId")
					})
	PresentationDto toDto(Presentation presentation); 
	List<PresentationDto> toDtos(List<Presentation> presentation);
	
	//-- Dto vers Entity
	
	//--- Multiple Mapping
	@Mappings({
		@Mapping(source = "presentationDto.id", target = "id"),
		@Mapping(source = "presentationDto.resumer", target = "resumer"),
		@Mapping(source = "conferencier", target = "conferencier"),
		@Mapping(source = "colloque", target = "colloque"),
		@Mapping(source = "exposer", target = "exposer")
			})
	Presentation toEntity( PresentationDto presentationDto,
			Conferencier conferencier, Colloque colloque, Exposer exposer
			);
	
	
}
