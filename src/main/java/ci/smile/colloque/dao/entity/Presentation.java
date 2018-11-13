/*
 * Created on 28 oct. 2018 ( Time 12:54:50 )
 * Generated by Telosys Tools Generator ( version 2.1.1 )
 */
// This Bean has a basic Primary Key (not composite) 

package ci.smile.colloque.dao.entity;

import java.io.Serializable;

//import javax.validation.constraints.* ;
//import org.hibernate.validator.constraints.* ;


import javax.persistence.*;

/**
 * Persistent class for entity stored in table "presentation"
 *
 * @author Telosys Tools Generator
 *
 */

@Entity
@Table(name="presentation" )
public class Presentation implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    //----------------------------------------------------------------------
    // ENTITY PRIMARY KEY ( BASED ON A SINGLE FIELD )
    //----------------------------------------------------------------------
	/*
	 * 
	 */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id", nullable=false)
    private Integer    id           ;


    //----------------------------------------------------------------------
    // ENTITY DATA FIELDS 
    //----------------------------------------------------------------------    
	/*
	 * 
	 */
    @Column(name="resumer", nullable=false, length=255)
    private String     resumer      ;

	// "conferencierId" (column "conferencier_id") is not defined by itself because used as FK in a link 
	// "exposerId" (column "exposer_id") is not defined by itself because used as FK in a link 
	// "colloqueId" (column "colloque_id") is not defined by itself because used as FK in a link 


    //----------------------------------------------------------------------
    // ENTITY LINKS ( RELATIONSHIP )
    //----------------------------------------------------------------------
    @ManyToOne
    @JoinColumn(name="colloque_id", referencedColumnName="id")
    private Colloque colloque    ;
    @ManyToOne
    @JoinColumn(name="conferencier_id", referencedColumnName="participant_id")
    private Conferencier conferencier;
    @ManyToOne
    @JoinColumn(name="exposer_id", referencedColumnName="id")
    private Exposer exposer     ;

    //----------------------------------------------------------------------
    // CONSTRUCTOR(S)
    //----------------------------------------------------------------------
    public Presentation() {
		super();
    }
    
    //----------------------------------------------------------------------
    // GETTER & SETTER FOR THE KEY FIELD
    //----------------------------------------------------------------------
    public void setId( Integer id ) {
        this.id = id ;
    }
    public Integer getId() {
        return this.id;
    }

    //----------------------------------------------------------------------
    // GETTERS & SETTERS FOR FIELDS
    //----------------------------------------------------------------------
    //--- DATABASE MAPPING : resumer ( VARCHAR ) 
    public void setResumer( String resumer ) {
        this.resumer = resumer;
    }
    public String getResumer() {
        return this.resumer;
    }


    //----------------------------------------------------------------------
    // GETTERS & SETTERS FOR LINKS
    //----------------------------------------------------------------------

    public void setColloque( Colloque colloque ) {
        this.colloque = colloque;
    }
    public Colloque getColloque() {
        return this.colloque;
    }


    public void setConferencier( Conferencier conferencier ) {
        this.conferencier = conferencier;
    }
    public Conferencier getConferencier() {
        return this.conferencier;
    }


    public void setExposer( Exposer exposer ) {
        this.exposer = exposer;
    }
    public Exposer getExposer() {
        return this.exposer;
    }


    //----------------------------------------------------------------------
    // toString METHOD
    //----------------------------------------------------------------------
    public String toString() { 
        StringBuffer sb = new StringBuffer(); 
        sb.append("["); 
        sb.append(id);
        sb.append("]:"); 
        sb.append(resumer);
        return sb.toString(); 
    } 

	//----------------------------------------------------------------------
    // clone METHOD
    //----------------------------------------------------------------------
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
