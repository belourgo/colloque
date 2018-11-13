package ci.smile.colloque.helper.extra.repository;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import ci.smile.colloque.helper.extra.classes.CountColloqueByUniv;
import ci.smile.colloque.helper.extra.classes.CountParticipantByColloque;
import ci.smile.colloque.helper.extra.classes.ParticipantExtra;

@Component
public class NativeQueryByJDBC {

	   @Autowired
	    private JdbcTemplate jdbcTemplate;
	
	   public List<CountColloqueByUniv> getNbreColloqueByUniversity()  {
		   
	        List<CountColloqueByUniv> count = null;
	     try{
	     String SQL = "select u.nom as nomUniv, count(c.nom) as nbreColloque from colloque c " +
	 			" inner join universite u on u.id = c.universite_id " + 
	    		 "group by c.universite_id " ;
	     count = jdbcTemplate.query(SQL, BeanPropertyRowMapper.newInstance(CountColloqueByUniv.class));
	     }
	        catch(DataAccessException e){
	         e.printStackTrace();
	         return Collections.emptyList();
	     }        
	        return count;
	    }
	
	  	  //--------------------------------------------
		  //LISTE DES Participant au Colloque
		  //--------------------------------------------   
	   
	   public List<CountParticipantByColloque> getNbreParticipantByColloque()  {
		   
	        List<CountParticipantByColloque> count = null;
	     try{
	    	 
	     String SQL = "	SELECT  c.nom nomColloque, u.nom universite,  count(i.participant_id) nombreDeColloque " + 
	     			  "	FROM inscription i " + 
	     			  "	INNER JOIN colloque c ON c.id = i.colloque_id " + 
	     			  "	INNER JOIN participant p ON p.id = i.participant_id " + 
	     			  "	INNER JOIN universite u ON u.id = c.universite_id " + 
	     			  "	GROUP BY i.colloque_id " ;
	     count = jdbcTemplate.query(SQL, BeanPropertyRowMapper.newInstance(CountParticipantByColloque.class));
	   
	     }
	        catch(DataAccessException e){
	         e.printStackTrace();
	         return Collections.emptyList();
	     }        
	        return count;
	    }
	   
	   
	   
 	  	  //--------------------------------------------
		  //LISTE DES Participant au Colloque
		  //--------------------------------------------  
	   
	   public List<CountParticipantByColloque> getNbrePart(String nomColloque)  {
		   
	        List<CountParticipantByColloque> count = null;
	     try{

		     String SQL = " SELECT  c.nom nomColloque, u.nom universite,  count(i.participant_id) nombreDeColloque " + 
		    		 	  "	FROM inscription i " + 
		    		 	  "	INNER JOIN colloque c ON c.id = i.colloque_id " + 
		    		 	  " INNER JOIN participant p ON p.id = i.participant_id " + 
		    		 	  " INNER JOIN universite u ON u.id = c.universite_id " + 
		     		      " WHERE c.nom = '"  + nomColloque +   
		     		      "'  GROUP BY i.colloque_id " ;
		     count = jdbcTemplate.query(SQL, BeanPropertyRowMapper.newInstance(CountParticipantByColloque.class));

	     }
	        catch(DataAccessException e){
	         e.printStackTrace();
	         return Collections.emptyList();
	     }        
	        return count;
	    }
	   
	   
	   
 
	     //--------------------------------------------
	     //LISTE DES Organisateurs
	     //--------------------------------------------
	   
	   public List<ParticipantExtra> getOrgParticipant(String nomColloque)  {
		   
	        List<ParticipantExtra> organisateur = null;
	     try{	   
	    	 
		     String SQL1 = 	" SELECT p.nom , p.prenom , p.adresse" + 
		    		 		" FROM organisateur o "     + 
		    		 		" INNER JOIN participant p ON p.id = o.participant_id" + 
		    		 		" INNER JOIN colloque c ON c.id = o.colloque_id " +
		    		 		" WHERE c.nom = '"  + nomColloque + "' "   ;
		     
		     organisateur = jdbcTemplate.query(SQL1, BeanPropertyRowMapper.newInstance(ParticipantExtra.class));

	     }
	        catch(DataAccessException e){
	         e.printStackTrace();
	         return Collections.emptyList();
	     }        
	        return organisateur;
	    }
	

	     //--------------------------------------------
	     //LIST DES Conferenciers
	     //--------------------------------------------
	   public List<ParticipantExtra> getConfParticipant( String nomColloque)  {
		   
	        List<ParticipantExtra> conferencier = null;
	        
	     try{
	    	 
	     String SQL2 = 	" SELECT p.nom , p.prenom , p.adresse " + 
 		 				" FROM presentation pres "     + 
 		 				" INNER JOIN conferencier conf ON conf.participant_id = pres.conferencier_id" + 
 		 				" INNER JOIN participant p ON p.id = conf.participant_id" + 
 		 				" INNER JOIN colloque c ON c.id = pres.colloque_id " +
 		 				" WHERE c.nom = '"  + nomColloque + "' "   ;	     		

	     conferencier = jdbcTemplate.query(SQL2, BeanPropertyRowMapper.newInstance(ParticipantExtra.class));  
	     }
	        catch(DataAccessException e){
	         e.printStackTrace();
	         return Collections.emptyList();
	     }        
	        return conferencier;
	    }
	
	   
	   
	   
	   
	   
	   
}
