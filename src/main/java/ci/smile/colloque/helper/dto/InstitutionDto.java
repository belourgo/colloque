/*
 * Created on 18 oct. 2018 ( Time 18:53:49 )
 * Generated by Telosys Tools Generator ( version 2.1.1 )
 */
// This Bean has a basic Primary Key (not composite) 

package ci.smile.colloque.helper.dto;

import ci.smile.colloque.helper.contract.SearchParam;

/**
 * Persistent class for entity stored in table "institution"
 *
 * @author Telosys Tools Generator
 *
 */


public class InstitutionDto implements  Cloneable {

    private Integer    id           ;
    private String     nom          ;
    
    
    
    private SearchParam<Integer> idParam;
    private SearchParam<String> nomParam;
    
    

    //----------------------------------------------------------------------
    // GETTER & SETTER FOR THE KEY FIELD
    //----------------------------------------------------------------------
    public void setId( Integer id ) {
        this.id = id ;
    }
    public Integer getId() {
        return this.id;
    }

    public void setNom( String nom ) {
        this.nom = nom;
    }
    public String getNom() {
        return this.nom;
    }
    
    
    
    
    
    public SearchParam<Integer> getIdParam(){
    	return this.idParam;
    } 
    public void setIdParam(SearchParam<Integer> idParam) {
    	this.idParam = idParam;
    }
    
    
   
    public SearchParam<String> getNomParam(){
    	return this.nomParam;
    }
    public void setNomParam(SearchParam<String> nomParam) {
    	this.nomParam = nomParam;
    }
    
    


	//----------------------------------------------------------------------
    // clone METHOD
    //----------------------------------------------------------------------
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}