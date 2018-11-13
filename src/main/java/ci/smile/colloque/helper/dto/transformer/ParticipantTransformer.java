package ci.smile.colloque.helper.dto.transformer;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import ci.smile.colloque.dao.entity.Participant;
import ci.smile.colloque.helper.dto.ParticipantDto;

@Mapper
public interface ParticipantTransformer {

	ParticipantTransformer INSTANCE = Mappers.getMapper( ParticipantTransformer.class ); 
	
	//-- Entity vers Dto
	
	ParticipantDto toDto(Participant participant); 
	List<ParticipantDto> toDtos(List<Participant> participant );
	
	//-- Dto vers Entity
	
	Participant toEntity( ParticipantDto participantDto);
	
}
