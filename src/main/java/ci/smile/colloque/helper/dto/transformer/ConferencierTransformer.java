package ci.smile.colloque.helper.dto.transformer;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import ci.smile.colloque.dao.entity.Conferencier;
import ci.smile.colloque.dao.entity.Institution;
import ci.smile.colloque.dao.entity.Participant;
import ci.smile.colloque.helper.dto.ConferencierDto;

@Mapper
public interface ConferencierTransformer {

	ConferencierTransformer INSTANCE = Mappers.getMapper( ConferencierTransformer.class ); 

	
	
	//from entity to dto
	
	@Mappings({
	@Mapping(source = "participant.id", target = "participantId"),
	@Mapping(source = "institution.id", target = "institutionId")
		})
	ConferencierDto toDto(Conferencier conferencier);
	List<ConferencierDto> toDtos(List<Conferencier> conferencier);
	
	
	
	// from dto to entity
	
	@Mappings({
	@Mapping(source = "participant", target = "participant"),
	@Mapping(source = "institution", target = "institution")
		})	
	Conferencier toEntity(ConferencierDto conferencierDto, Participant participant,
			Institution institution);
	




}
