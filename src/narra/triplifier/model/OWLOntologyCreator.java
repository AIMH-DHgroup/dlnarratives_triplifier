package narra.triplifier.model;

import org.apache.log4j.Logger;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.semanticweb.owlapi.formats.PrefixDocumentFormat;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.vocab.XSDVocabulary;
import org.apache.jena.vocabulary.OWL;

import narra.triplifier.resource.Vocabulary;
import narra.triplifier.util.Log4JClass;
import uk.ac.manchester.cs.owl.owlapi.OWLAnnotationPropertyImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDatatypeImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImpl;

public class OWLOntologyCreator {

	private enum PrefixNs{rdf, rdfs, owl, ecrm, crminf, efrbroo, cnt, dc, dctypes, narra, time, base}

	// Get logger instance
	private Logger log = null;
	private OWLOntology model;
	private OWLOntologyManager manager;
	private OWLDataFactory dataFactory;

	public OWLOntologyCreator(OWLOntologyManager manager, OWLOntology ontology) {
		dataFactory = manager.getOWLDataFactory();
		this.manager = manager;		
		this.model = ontology;
		log = Log4JClass.getLogger();
	}
	
	public static PrefixDocumentFormat setRdfPrefix(OWLOntology ontology){
		PrefixDocumentFormat preMan = new RDFXMLDocumentFormat();
		//		this.oNamespaceManager = new OWLOntologyXMLNamespaceManager(ontology, owlDocFormat);
		preMan.setPrefix(PrefixNs.rdfs.toString(),            Vocabulary.rdfs);
		preMan.setPrefix(PrefixNs.ecrm.toString(),            Vocabulary.ecrm);
		preMan.setPrefix(PrefixNs.crminf.toString(),          Vocabulary.crminf);
		preMan.setPrefix(PrefixNs.efrbroo.toString(),         Vocabulary.efrbroo);
		preMan.setPrefix(PrefixNs.cnt.toString(),             Vocabulary.cnt);
		preMan.setPrefix(PrefixNs.dc.toString(),              Vocabulary.dc);
		preMan.setPrefix(PrefixNs.dctypes.toString(),         Vocabulary.dctypes);
		preMan.setPrefix(PrefixNs.narra.toString(),           Vocabulary.narra);
		preMan.setPrefix(PrefixNs.time.toString(),            Vocabulary.time);
		preMan.setDefaultPrefix(Vocabulary.base);
		return preMan;
	}

	public void createOntology() {
		try {
			/*
			// Informazioni sull'ontologia
			Ontology onto = model.createOntology(Vocabulary.narr);

			model.add(onto, RDFS.label, "An Ontology for Narratives");
			model.add(onto, RDFS.comment, "");
			model.add(onto, DC.date, "2017-02-20");
			model.add(onto, OWL.versionInfo, "1.0");
			model.add(onto, DC.creator, "Carlo Meghini, Valentina Bartalesi, Daniele Metilli, Filippo Benedetti");
			model.add(onto, DC.publisher, "ISTI-CNR");
			//model.add(onto, DC.rights, "http://creativecommons.org/licenses/by-nc-sa/4.0/");

			// Importazione di ontologie di riferimento
			onto.addImport(model.createResource(Vocabulary.cnt));
			onto.addImport(model.createResource(Vocabulary.ecrm));
			onto.addImport(model.createResource(Vocabulary.efrbroo));
			onto.addImport(model.createResource(Vocabulary.time));
			//onto.addImport(model.createResource(Vocabulary.dc));
			//onto.addImport(model.createResource(Vocabulary.dctypes));
			*/

			
			// ///////////////////////////////////////////////////////////// //
			//	 															 //														
			//          Classes of the Narrative ontology                    //
			//                                                               // 
			// ///////////////////////////////////////////////////////////// //

			// Class narra:Narrative
			createClassWithDescriptionAndSuperclass(
				Vocabulary.narra + Vocabulary.narraNames.Narrative.toString(),
				"This class represents a narrative.",
				Vocabulary.narra + Vocabulary.narraNames.Information_Object.toString()
			);
			
			// Class narra:Event
			createClassWithDescriptionAndSuperclass(
				Vocabulary.narra + Vocabulary.narraNames.Event.toString(),
				"This class represents an event. Equivalent to the CRM class E5 Event.",
				Vocabulary.narra + Vocabulary.narraNames.Entity.toString()
			);
			
			// Class narra:Fabula
			createClassWithDescriptionAndSuperclass(
				Vocabulary.narra + Vocabulary.narraNames.Fabula.toString(),
				"This class represents the fabula of a narrative, i.e. the sequence of events in chronological order.",
				Vocabulary.narra + Vocabulary.narraNames.Period.toString()
			);
			
			// Class narra:Narration
			createClassWithDescriptionAndSuperclass(
				Vocabulary.narra + Vocabulary.narraNames.Narration.toString(),
				"This class represents the narration of a narrative, i.e. an individual work"
				+ " that tells the events of the narrative through some form of media (text, video, audio, etc.).",
				Vocabulary.narra + Vocabulary.narraNames.Individual_Work.toString()
			);
			
			// Class narra:Biography
			createClassWithDescriptionAndSuperclass(
				Vocabulary.narra + Vocabulary.narraNames.Biography.toString(),
				"This class represents a biographical narrative.",
				Vocabulary.narra + Vocabulary.narraNames.Narrative.toString()
			);
			
			// Class narra:ActorWithRole
			createClassWithDescriptionAndSuperclass(
				Vocabulary.narra + Vocabulary.narraNames.ActorWithRole.toString(),
				"In order to assign a role to an actor, this reification class was introduced."
				+ "Through the property hadParticipant an event is related with this class."
				+ "ActorWithRole is related with the class Actor through the property hasSubject"
				+ "and to a literal that represents the role through the property hasRole.",
				Vocabulary.narra + Vocabulary.narraNames.Entity.toString()
			);
			
			// Class narra:Role
			createClassWithDescriptionAndSuperclass(
				Vocabulary.narra + Vocabulary.narraNames.Role.toString(),
				"This class represents a role in the event.",
				Vocabulary.narra + Vocabulary.narraNames.Entity.toString()
			);

			// Class narra:Proposition
			createClassWithDescriptionAndSuperclass(
				Vocabulary.narra + Vocabulary.narraNames.Proposition.toString(),
				"This class represents a proposition endowed with a subject, predicate, and object.",
				Vocabulary.narra + Vocabulary.narraNames.Entity.toString()
			);


			// Equivalent classes to CIDOC CRM classes
			createClassWithDescription(
				Vocabulary.narra + Vocabulary.narraNames.Entity.toString(), 
				"This class represents an entity. Equivalent to the CRM class E1 CRM Entity."
			);
			createClassWithDescription(
				Vocabulary.narra + Vocabulary.narraNames.Appellation.toString(), 
				"This class represents an appellation. Equivalent to the CRM class E41 Appelation."
			);
			createClassWithDescription(
				Vocabulary.narra + Vocabulary.narraNames.Activity.toString(),
				 "This class represents an activity. Equivalent to the CRM class E7 Activity."
			);
			createClassWithDescription(
				Vocabulary.narra + Vocabulary.narraNames.Creation.toString(),
				 "This class represents a creation. Equivalent to the CRM class E65 Creation."
			);
			createClassWithDescription(
				Vocabulary.narra + Vocabulary.narraNames.Actor.toString(),
				 "This class represents an actor. Equivalent to the CRM class E39 Actor."
			);
			createClassWithDescription(
				Vocabulary.narra + Vocabulary.narraNames.Event.toString(),
				 "This class represents an event. Equivalent to the CRM class E5 Event."
			);
			createClassWithDescription(
				Vocabulary.narra + Vocabulary.narraNames.Place.toString(),
				 "This class represents a place. Equivalent to the CRM class E53 Place."
			);
			createClassWithDescription(
				Vocabulary.narra + Vocabulary.narraNames.Physical_Thing.toString(),
				 "This class represents a physical thing. Equivalent to the CRM class E18 Physical Thing."
			);
			createClassWithDescription(
				Vocabulary.narra + Vocabulary.narraNames.Information_Object.toString(),
				 "This class represents an information object. Equivalent to the CRM class E73 Information Object."
			);
			createClassWithDescription(
				Vocabulary.narra + Vocabulary.narraNames.Persistent_Item.toString(),
				 "This class represents a persistent item. Equivalent to the CRM class E77 Persistent Item."
			);
			createClassWithDescription(
				Vocabulary.narra + Vocabulary.narraNames.Space_Primitive.toString(),
				"This class represents a space primitive. Equivalent to the CRM class E94 Space primitive."
			);
			createClassWithDescription(
				Vocabulary.narra + Vocabulary.narraNames.Visual_Item.toString(),
				"This class represents a visual item. Equivalent to the CRM class E36 Visual Item."
			);
			createClassWithDescription(
				Vocabulary.narra + Vocabulary.narraNames.Time_Span.toString(),
				"This class represents a time span. Equivalent to the CRM class E52 Time Span."
			);
			createClassWithDescription(
				Vocabulary.narra + Vocabulary.narraNames.Person.toString(),
				"This class represents a person. Equivalent to the CRM class E21 Person."
			);
			createClassWithDescription(
				Vocabulary.narra + Vocabulary.narraNames.Birth.toString(),
				"This class represents a birth. Equivalent to the CRM class E67 Birth."
			);
			createClassWithDescription(
				Vocabulary.narra + Vocabulary.narraNames.Physical_Object.toString(),
				"This class represents a physical object. Equivalent to the CRM class E19 Physical Object."
			);
			createClassWithDescription(
				Vocabulary.narra + Vocabulary.narraNames.Propositional_Object.toString(),
				"This class represents a propositional object. Equivalent to the CRM class E89 Propositional Object."
			);
			createClassWithDescription(
				Vocabulary.narra + Vocabulary.narraNames.Death.toString(),
				"This class represents a death. Equivalent to the CRM class E69 Death."
			);
			createClassWithDescription(
				Vocabulary.narra + Vocabulary.narraNames.Joining.toString(),
				"This class represents a joining. Equivalent to the CRM class E85 Joining."
			);
			createClassWithDescription(
				Vocabulary.narra + Vocabulary.narraNames.Leaving.toString(),
				"This class represents a leaving. Equivalent to the CRM class E86 Leaving."
			);
			createClassWithDescription(
				Vocabulary.narra + Vocabulary.narraNames.Creation.toString(),
				"This class represents a creation. Equivalent to the CRM class E65 Creation."
			);
			createClassWithDescription(
				Vocabulary.narra + Vocabulary.narraNames.Conceptual_Object.toString(),
				"This class represents a conceptual object. Equivalent to the CRM class E28 Conceptual Object."
			);

			
			// CRMinf classes
			createClassWithDescription(
				Vocabulary.crminf + Vocabulary.crmsciNames.S15_Observable_Entity.toString(), 
				"This class comprises instances of E2 Temporal Entity or E77 Persistent Item, i.e. items or " +
				"phenomena, such as physical things, their behavior, states and interactions or events, that can " +
				"be observed by human sensory impression, often enhanced by using tools and measurement " +
				"devices.\n" +
				"Conceptual objects manifest through their carriers such as books, digital media, or even human " +
				"memory. Attributes of conceptual objects, such as number of words, can be observed on their " +
				"carriers. If the respective properties between carriers differ, either they carry different " +
				"instances of conceptual objects or the difference can be attributed to accidental deficiencies in " +
				"one of the carriers. In that sense even immaterial objects are observable. By this model we " +
				"address the fact that frequently, the actually observed carriers of conceptual objects are not " +
				"explicitly identified in documentation, i.e., they are assumed to have existed but they are " +
				"unknown as individuals."
			);
			createClassWithDescription(
				Vocabulary.crminf + Vocabulary.crmsciNames.S4_Observation.toString(), 
				"This class comprises the activity of gaining scientific knowledge about particular states of " +
				"physical reality through empirical evidence, experiments and measurements.\n" +
				"We define observation in the sense of natural sciences, as a kind of human activity: at some" +
				"place and within some time-span, certain physical things and their behavior and interactions "+
				"are observed by human sensory impression, and often enhanced by tools and measurement devices.\n" +
				"The output of the internal processes of measurement devices that do not require additional" +
				"human interaction are in general regarded as part of the observation and not as additional" +
				"inference. Manual recordings may serve as additional evidence. Measurements and witnessing" +
				"of events are special cases of observations. Observations result in a belief about certain" +
				"propositions. In this model, the degree of confidence in the observed properties is regarded to" +
				"be “true” by default, but could be described differently by adding a property P3 has note to an" +
				"instance of S4 Observation, or by reification of the property O16 observed value.\n" +
				"Primary data from measurement devices are regarded in this model to be results of observation" +
				"and can be interpreted as propositions believed to be true within the (known) tolerances and" +
				"degree of reliability of the device.\n" +
				"Observations represent the transition between reality and propositions in the form of instances" +
				"of a formal ontology, and can be subject to data evaluation from this point on. For instance," +
				"detecting an archaeological site on satellite images is not regarded as an instance of S4" +
				"Observation, but as an instance of S6 Data Evaluation. Rather, only the production of the" +
				"images is regarded as an instance of S4 Observation"
			);
			createClassWithDescription(
				Vocabulary.crminf + Vocabulary.crminfNames.I2_Belief.toString(), 
				"This class comprises the notion that the associated I4 Proposition Set is held to have" + 
				"a particular I6 Belief Value by a particular E39 Actor. This can be understood as the period " +
				"of time that an individual or group holds a particular set of propositions to be true, " +
				"false or somewhere in between."
			);
			createClassWithDescription(
				Vocabulary.crminf + Vocabulary.crminfNames.I5_Inference_Making.toString(), 
				"This class comprises the action of making honest propositions and statements about particular states " +
				"of affairs in reality or in possible realities or categorical descriptions of reality by using " +
				"inferences from other statements based on hypotheses and any form of formal or informal logic. " +
				"It includes evaluations, calculations, and interpretations based on mathematical formulations and propositions. " +
				"It is characterized by the use of an existing I2 Belief as the premise that together with a set of I3 Inference Logic " +
				"draws a further I2 Belief as a conclusion. " +
				"Documenting instances of I5 Inference Making primarily enables tracing the dependency of knowledge " +
				"from conclusion to premise through subsequent inferences, possibly back to primary evidence, " +
				"so that the range of influence of knowledge revision at any intermediate stage of complex inference " +
				"chains on current convictions can be narrowed down by query. The explicit reference to the applied " +
				"inference logic further allows scholars or scientists to assess if they can or would follow the documented argument. " +
				"The class is not intended to promote the use of computationally decidable systems of logic " +
				"as replacements of scholarly justifications of arguments, even though it allows for documenting " +
				"the use of decidable logic, if that was deemed adequate for the problem at hand. Principles " +
				"of scholarly justifications of arguments are also regarded as kinds of inference logic."
			);
			createClassWithDescription(
				Vocabulary.crminf + Vocabulary.crminfNames.I4_Proposition_Set.toString(), 
				"This class comprises the sets of formal, binary propositions that an I2 Belief is held about. " +
				"It could be implemented as a named graph, a spreadsheet or any other structured data-set. " +
				"Regardless of the specific syntax employed, the effective propositions it contains should be " +
				"made up of unambiguous identifiers, concepts of a formal ontology and constructs of logic."
			);

			// Equivalent narra classes of CRMinf classes
			createClassWithDescription(
				Vocabulary.narra + Vocabulary.narraNames.Observable_Entity.toString(), 
				"This class represents an observable entity. Equivalent to the CRMinf class S15 Observable Entity."
			);
			createClassWithDescription(
				Vocabulary.narra + Vocabulary.narraNames.Observation.toString(), 
				"This class represents an observation. Equivalent to the CRMinf class S4 Observation."
			);
			createClassWithDescription(
				Vocabulary.narra + Vocabulary.narraNames.Belief.toString(), 
				"This class represents a belief. Equivalent to the CRMinf class I2 Belief."
			);
			createClassWithDescription(
				Vocabulary.narra + Vocabulary.narraNames.Inference_Making.toString(), 
				"This class represents an inference making. Equivalent to the CRMinf class I5 Inference Making."
			);
			createClassWithDescription(
				Vocabulary.narra + Vocabulary.narraNames.Proposition_Set.toString(), 
				"This class represents a proposition set. Equivalent to the CRMinf class I4 Proposition Set."
			);

			
			// EFRBRoo classes
			createClassWithDescription(
				Vocabulary.efrbroo + Vocabulary.efrbrooNames.F23_Expression_Fragment.toString(), 
				"This class comprises parts of Expressions and these parts are not Self-Contained Expressions themselves." + 
				"The existence of an instance of F23 Expression Fragment can be due to accident, such as loss of material over time, " +
				"e.g. the only remaining manuscript of an antique text being partially eaten by worms, or due to deliberate isolation, " +
				"such as excerpts taken from a text by the compiler of a collection of excerpts.\n" +
				"An F23 Expression Fragment is only identified with respect to its occurrence in a known or assumed whole. \n" +
				"The size of an instance of F23 Expression Fragment ranges from more than 99% of an instance of " +
				"F22 Self-Contained Expression to tiny bits (a few words from a text, one bar from a musical composition, " +
				"one detail from a still image, a two-second clip from a movie, etc.)."
			);

			// Equivalent narra classes of EFRBRoo classes
			createClassWithDescription(
				Vocabulary.narra + Vocabulary.narraNames.Expression_Fragment.toString(), 
				"This class represents an expression fragment. Equivalent to the FRBRoo class F23 Expression Fragment."
			);


			// Equivalent narra classes of CRMgeo classes

			// Class narra:Phenomenal_Place, eqivalent to crmgeo:SP2_Phenomenal_Place
			createClassWithDescription(
				Vocabulary.narra + Vocabulary.narraNames.Phenomenal_Place.toString(),
			 "This class is equivalent to  the class crmgeo:SP2_Phenomenal_Place."
			);

			// Class narra:Spatial_Coordinate_Reference_System, eqivalent to crmgeo:SP4_Spatial_Coordinate_Reference_System
			createClassWithDescription(
				Vocabulary.narra + Vocabulary.narraNames.Spatial_Coordinate_Reference_System.toString(), 
				"This class is equivalent to  the class crmgeo:SP4_Spatial_Coordinate_Reference_System."
			);

			// Class narra:Spatial_Coordinate_Reference_System, eqivalent to crmgeo:SP5_Geometric_Place_Expression
			createClassWithDescription(
				Vocabulary.narra + Vocabulary.narraNames.Geometric_Place_Expression.toString(), 
				"This class is equivalent to  the class crmgeo:SP5_Geometric_Place_Expression."
			);

			// Time Ontology in OWL
			createClassWithDescription(
				Vocabulary.narra + Vocabulary.narraNames.Instant, 
				"This class is equivalent to the class time:Instant"
			);


			// ///////////////////////////////////////////////////////////// //
			//	 															 //														
			//           Properties of the Narrative ontology                //
			//                                                               // 
			// ///////////////////////////////////////////////////////////// //

			
			// Property narra:propSubject
			createObjPropertyWithDomainRangeAndDescription(
				Vocabulary.narra + Vocabulary.narraNames.propSubject.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Proposition.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Event.toString(),
				"This property relates a proposition to its subject (an event)."
			);

			// Property narra:propPredicate
			createObjPropertyWithDomainRangeAndDescription(
				Vocabulary.narra + Vocabulary.narraNames.propPredicate.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Proposition.toString(),
				OWL.ObjectProperty.toString(),
				"This property relates a proposition to its predicate (a property)."
			);

			// Property narra:propObject
			createObjPropertyWithDomainRangeAndDescription(
				Vocabulary.narra + Vocabulary.narraNames.propObject.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Proposition.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Entity.toString(),
				"This property relates a proposition to its object (an entity)."
			);

			// Property narra:hasText
			createObjPropertyWithDomainRangeAndDescription(
				Vocabulary.narra + Vocabulary.narraNames.hasText.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Narrative.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Symbolic_Object.toString(),
				"This property relates a narrative to the text that expresses it."
			);
			
			// Property narra:partOfNarrative
			createObjPropertyWithDomainRangeAndDescription(
				Vocabulary.narra + Vocabulary.narraNames.partOfNarrative.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Event.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Narrative.toString(),
				"This property relates an event to the narrative that contains it."
			);
			
			// Property narra:partOfNarrative
			createObjPropertyWithDomainRangeAndDescription(
				Vocabulary.narra + Vocabulary.narraNames.holdsBelief.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Actor.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Belief.toString(),
				"This property relates an actor to the belief held by him/her."
			);
			
			// Property narra:hasEntity
			createObjPropertyWithDomainRangeAndDescription(
				Vocabulary.narra + Vocabulary.narraNames.hasEntity.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Event.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Entity.toString(),
				"This property relates an entity to his event."
			);
			
			// Property narra:isEntityOf
			createObjPropertyWithDomainRangeAndDescription(
				Vocabulary.narra + Vocabulary.narraNames.isEntityOf.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Entity.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Event.toString(),
				"This property relates an event to his entity."
			);
			
			// Property narra:isPresentedBefore
			createObjPropertyWithDomainRangeAndDescription(
				Vocabulary.narra + Vocabulary.narraNames.isPresentedBefore.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Event.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Event.toString(),
				"This property relates an event to another event presented before"
			);
			
			// Property narra:isPresentedAfter
			//createObjPropertyWithDomainRangeAndDescription(
				//Vocabulary.narra + Vocabulary.narraNames.isPresentedAfter.toString(),
				//Vocabulary.ecrm + Vocabulary.ecrmNames.E5_Event.toString(),
				//Vocabulary.ecrm + Vocabulary.ecrmNames.E5_Event.toString(),
				//"This property relates an event to another event presented after"
			//);

			// Property narra:instantEquals
			createObjPropertyWithDomainRangeAndDescription(
				Vocabulary.narra + Vocabulary.narraNames.instantEquals.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Instant.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Instant.toString(),
				"This property relates an instant with another instant that is equal to it."
				+ "This is needed to match uncertain instants that are inferred to be the same by the reasoner."
			);

			// Property narra:hadParticipant
			createObjPropertyWithDomainRangeAndDescription(
				Vocabulary.narra + Vocabulary.narraNames.hadParticipant.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Event.toString(),
				Vocabulary.narra + Vocabulary.narraNames.ActorWithRole.toString(),
				"This property relates an event with an instance of the class ActorWithRole."
			);

			// Property narra:hasSubject
			createObjPropertyWithDomainRangeAndDescription(
				Vocabulary.narra + Vocabulary.narraNames.hasSubject.toString(),
				Vocabulary.narra + Vocabulary.narraNames.ActorWithRole.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Actor.toString(),
				"This property relates the class ActorWithRole with the class E39 Actor."
			);

			// Property narra:hasRole
			createObjPropertyWithDomainRangeAndDescription(
				Vocabulary.narra + Vocabulary.narraNames.hasRole.toString(),
				Vocabulary.narra + Vocabulary.narraNames.ActorWithRole.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Role.toString(),
				"This property relates the class ActorWithRole with a literal that represents."
			);

			// Property narra:hasSource
			createObjPropertyWithDomainRangeAndDescription(
				Vocabulary.narra + Vocabulary.narraNames.hasSource.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Information_Object.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Observable_Entity.toString(),
				"This property directly relates a proposition with an observable entity."
			);


			// Property narra:hasTextFragment
			createObjPropertyWithDomainRangeAndDescription(
				Vocabulary.narra + Vocabulary.narraNames.hasTextFragment.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Information_Object.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Expression_Fragment.toString(),
				"This property relates a proposition with a text fragment."
			);

			// Property narra:hasReference
			createObjPropertyWithDomainRangeAndDescription(
				Vocabulary.narra + Vocabulary.narraNames.hasReference.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Information_Object.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Expression_Fragment.toString(),
				"This property relates a proposition with a reference fragment."
				+ "For instance, the reference fragment \"Inferno II, 121\" +"
				+ "refers to a specific part of the work \"Divine Comedy\"."
			);

			// Property narra:causallyDependsOn
			createObjPropertyWithDomainRangeAndDescription(
				Vocabulary.narra + Vocabulary.narraNames.causallyDependsOn.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Event.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Event.toString(),
				"This property relates an event with another event that caused it."
				+ "This property connects events that in normal discourse are predicated"
				+ "to have a cause-effect relation, e.g. the eruption of the Vesuvius"
				+ "caused the destruction of Pompeii."
			
			);

			
			// Property narra:isAboutCountry
			createObjPropertyWithDomainRangeAndDescription(
				Vocabulary.narra + Vocabulary.narraNames.isAboutCountry.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Propositional_Object.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Entity.toString(),
				"This property relates a narrative with a country."
			);
			
			createObjectPropertySuperproperty(
				Vocabulary.narra + Vocabulary.narraNames.isAboutCountry.toString(), 
				Vocabulary.narra + Vocabulary.narraNames.is_about.toString()
			);

			// Property narra:isAboutLAU
			createObjPropertyWithDomainRangeAndDescription(
				Vocabulary.narra + Vocabulary.narraNames.isAboutLAU.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Propositional_Object.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Entity.toString(),
				"This property relates a narrative with a LAU."
			);

			createObjectPropertySuperproperty(
				Vocabulary.narra + Vocabulary.narraNames.isAboutLAU.toString(), 
				Vocabulary.narra + Vocabulary.narraNames.is_about.toString()
			);



			// ////////////////////////////////////////////////////////////////
			// Declaration Equivalent narra properties of reference ontologies properties
			// ///////////////////////////////////////////////////////////////

			// Equivalence object properties of CIDOC CRM properties
			createObjPropertyWithDomainRangeAndDescription(
				Vocabulary.narra + Vocabulary.narraNames.is_identified_by.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Entity.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Appellation.toString(),
				"This property relates an entity to its identifier. Equivalent to the CRM property P1 is identified by."
			);

			createObjPropertyWithDomainRangeAndDescription(
				Vocabulary.narra + Vocabulary.narraNames.carried_out_by.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Activity.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Actor.toString(),
				"This property relates an activity to the actor who carried it out. Equivalent to the CRM property P14 carried out by."
			);

			createDataPropertyWithDomainRangeAndDescription(
				Vocabulary.narra + Vocabulary.narraNames.has_note.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Entity.toString(),
				XSDVocabulary.STRING.toString(),
				"This property relates an entity to a note about it. Equivalent to the CRM property P3 has note."
			);

			createObjPropertyWithDomainRangeAndDescription(
				Vocabulary.narra + Vocabulary.narraNames.has_time_span.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Entity.toString(), // E2 Temporal Entity
				Vocabulary.narra + Vocabulary.narraNames.Time_Span.toString(),
				"This property relates an entity to its time span. Equivalent to the CRM property P4 has time span."
			);

			createObjPropertyWithDomainRangeAndDescription(
				Vocabulary.narra + Vocabulary.narraNames.occurred_in_the_presence_of.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Event.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Persistent_Item.toString(), 
				"This property relates an event to an actor who was present. Equivalent to the CRM property P12 occurred in the presence of."
			);

			createObjPropertyWithDomainRangeAndDescription(
				Vocabulary.narra + Vocabulary.narraNames.refers_to.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Propositional_Object.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Entity.toString(),
				"This property relates an entity to another entity it refers to. Equivalent to the CRM property P67 refers to."
			);

			createObjPropertyWithDomainRangeAndDescription(
				Vocabulary.narra + Vocabulary.narraNames.has_created.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Creation.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Conceptual_Object.toString(),
				"This property relates an actor to a creation they have created. Equivalent to the CRM property P94 has created."
			);

			createObjPropertyWithDomainRangeAndDescription(
				Vocabulary.narra + Vocabulary.narraNames.has_preferred_identifier.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Entity.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Identifier.toString(),
				"This property relates an entity to its preferred identifier. Equivalent to the CRM property P48 has preferred identifier."
			);

			createObjPropertyWithDomainRangeAndDescription(
				Vocabulary.narra + Vocabulary.narraNames.is_composed_of.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Symbolic_Object.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Symbolic_Object.toString(),
				"This property relates an entity to the entities it is composed of. Equivalent to the CRM property P106 is composed of."
			);

			// CRMinf and CRMSci equivalent properties
			// Property crminf:O8_observed
			createObjPropertyWithDomainAndRange(
				Vocabulary.narra + Vocabulary.narraNames.observed.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Observable_Entity.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Observation.toString()
			);

			// Property crminf:O16_observed_value
			createObjPropertyWithDomainAndRange(
				Vocabulary.narra + Vocabulary.narraNames.observed_value.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Observation.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Proposition_Set.toString()
			);

			// Property crminf:J1_was_premise_for
			createObjPropertyWithDomainAndRange(
				Vocabulary.narra + Vocabulary.narraNames.was_premise_for.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Belief.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Inference_Making.toString()
			);

			// Property crminf:J2_concluded_that
			createObjPropertyWithDomainAndRange(
				Vocabulary.narra + Vocabulary.narraNames.concluded_that.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Inference_Making.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Belief.toString()
			);

			// Property crminf:J4_that
			createObjPropertyWithDomainAndRange(
				Vocabulary.narra + Vocabulary.narraNames.that.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Belief.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Proposition_Set.toString()
			);	
			

			// CRMgeo equivalent properties
			
			// Property crmgeo:Q7_describes
			createObjPropertyWithDomainAndRange(
				Vocabulary.crmgeo + Vocabulary.crmgeoNames.Q7_describes.toString(),
				Vocabulary.crmgeo + Vocabulary.crmgeoNames.SP4_Spatial_Coordinate_Reference_System.toString(),
				Vocabulary.crmgeo + Vocabulary.crmgeoNames.SP3_Reference_Space.toString()
			);	
			
			// Property crmgeo:Q5_defined_in
			createObjPropertyWithDomainAndRange(
				Vocabulary.crmgeo + Vocabulary.crmgeoNames.Q5_defined_in.toString(),
				Vocabulary.ecrm + Vocabulary.ecrmNames.E53_Place.toString(),
				Vocabulary.crmgeo + Vocabulary.crmgeoNames.SP3_Reference_Space.toString()
			);	
		

			// //////////////////////////////////////////////////////////
			// DATA PROPERTIES
			// //////////////////////////////////////////////////////////

			// Property hasDescription
			createDataPropertyWithDomainAndRange(
				Vocabulary.narra + Vocabulary.narraNames.hasDescription.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Event.toString(),
				XSDVocabulary.STRING.toString()
			);
			
			// Property geo:asWKT
			createDataPropertyWithDomainAndRange(
				Vocabulary.narra + Vocabulary.narraNames.asWKT.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Geometry.toString(),
				"http://www.opengis.net/ont/geosparql#wktLiteral"
			);
			
			
			createDataPropertySuperproperty(
				Vocabulary.narra + Vocabulary.narraNames.asWKT.toString(),
				Vocabulary.narra + Vocabulary.narraNames.hasSerialization.toString()
			);
			// Property time:before
			createObjPropertyWithDomainAndRange(
				Vocabulary.narra + Vocabulary.narraNames.before.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Instant.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Instant.toString()
			);

			// Property time:after
			createObjPropertyWithDomainAndRange(
				Vocabulary.narra + Vocabulary.narraNames.after.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Instant.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Instant.toString()
			);


			// Property cnt:chars
			createDataPropertyWithDomainAndRange(
				Vocabulary.narra + Vocabulary.narraNames.chars.toString(),
				Vocabulary.narra + Vocabulary.narraNames.Expression_Fragment.toString(),
				XSDVocabulary.STRING.toString()
			);

			
		


			// ////////////////////////////////////////////////////////////////
			// Declaration of equivalence axioms between narra classes and reference ontologies classes
			// /////////////////////////////////////////////////////////////// 

			// Equivalence axioms between CIDOC CRM classes and narra classes
			addEquivalentClassAxiom(Vocabulary.ecrm + Vocabulary.ecrmNames.E1_CRM_Entity.toString(), Vocabulary.narra + Vocabulary.narraNames.Entity.toString());
			addEquivalentClassAxiom(Vocabulary.ecrm + Vocabulary.ecrmNames.E41_Appellation.toString(), Vocabulary.narra + Vocabulary.narraNames.Appellation.toString());
			addEquivalentClassAxiom(Vocabulary.ecrm + Vocabulary.ecrmNames.E7_Activity.toString(), Vocabulary.narra + Vocabulary.narraNames.Activity.toString());
			addEquivalentClassAxiom(Vocabulary.ecrm + Vocabulary.ecrmNames.E65_Creation.toString(), Vocabulary.narra + Vocabulary.narraNames.Creation.toString());
			addEquivalentClassAxiom(Vocabulary.ecrm + Vocabulary.ecrmNames.E39_Actor.toString(), Vocabulary.narra + Vocabulary.narraNames.Actor.toString());
			addEquivalentClassAxiom(Vocabulary.ecrm + Vocabulary.ecrmNames.E5_Event.toString(), Vocabulary.narra + Vocabulary.narraNames.Event.toString());
			addEquivalentClassAxiom(Vocabulary.ecrm + Vocabulary.ecrmNames.E53_Place.toString(), Vocabulary.narra + Vocabulary.narraNames.Place.toString());
			addEquivalentClassAxiom(Vocabulary.ecrm + Vocabulary.ecrmNames.E18_Physical_Thing.toString(), Vocabulary.narra + Vocabulary.narraNames.Physical_Thing.toString());
			addEquivalentClassAxiom(Vocabulary.ecrm + Vocabulary.ecrmNames.E73_Information_Object.toString(), Vocabulary.narra + Vocabulary.narraNames.Information_Object.toString());
			addEquivalentClassAxiom(Vocabulary.ecrm + Vocabulary.ecrmNames.E77_Persistent_Item.toString(), Vocabulary.narra + Vocabulary.narraNames.Persistent_Item.toString());
			addEquivalentClassAxiom(Vocabulary.ecrm + Vocabulary.ecrmNames.E94_Space_Primitive.toString(),Vocabulary.narra + Vocabulary.narraNames.Space_Primitive.toString());
			addEquivalentClassAxiom(Vocabulary.ecrm + Vocabulary.ecrmNames.E36_Visual_Item.toString(),Vocabulary.narra + Vocabulary.narraNames.Visual_Item.toString());
			addEquivalentClassAxiom(Vocabulary.ecrm + Vocabulary.ecrmNames.E52_Time_Span.toString(),Vocabulary.narra + Vocabulary.narraNames.Time_Span.toString());
			addEquivalentClassAxiom(Vocabulary.ecrm + Vocabulary.ecrmNames.E21_Person.toString(),Vocabulary.narra + Vocabulary.narraNames.Person.toString());
			addEquivalentClassAxiom(Vocabulary.ecrm + Vocabulary.ecrmNames.E67_Birth.toString(),Vocabulary.narra + Vocabulary.narraNames.Birth.toString());
			addEquivalentClassAxiom(Vocabulary.ecrm + Vocabulary.ecrmNames.E19_Physical_Object.toString(),Vocabulary.narra + Vocabulary.narraNames.Physical_Object.toString());
			addEquivalentClassAxiom(Vocabulary.ecrm + Vocabulary.ecrmNames.E89_Propositional_Object.toString(),Vocabulary.narra + Vocabulary.narraNames.Propositional_Object.toString());
			addEquivalentClassAxiom(Vocabulary.ecrm + Vocabulary.ecrmNames.E65_Creation.toString(),Vocabulary.narra + Vocabulary.narraNames.Creation.toString());
			addEquivalentClassAxiom(Vocabulary.ecrm + Vocabulary.ecrmNames.E28_Conceptual_Object.toString(),Vocabulary.narra + Vocabulary.narraNames.Conceptual_Object.toString());
			addEquivalentClassAxiom(Vocabulary.ecrm + Vocabulary.ecrmNames.E42_Identifier.toString(),Vocabulary.narra + Vocabulary.narraNames.Identifier.toString());
			addEquivalentClassAxiom(Vocabulary.ecrm + Vocabulary.ecrmNames.E90_Symbolic_Object.toString(),Vocabulary.narra + Vocabulary.narraNames.Symbolic_Object.toString());

			// Equivalence axioms between CRMinf and CRMsci classes and narra classes
			addEquivalentClassAxiom(Vocabulary.crmsci + Vocabulary.crmsciNames.S15_Observable_Entity.toString(), Vocabulary.narra + Vocabulary.narraNames.Observable_Entity.toString());
			addEquivalentClassAxiom(Vocabulary.crmsci + Vocabulary.crmsciNames.S4_Observation.toString(), Vocabulary.narra + Vocabulary.narraNames.Observation.toString());
			addEquivalentClassAxiom(Vocabulary.crminf + Vocabulary.crminfNames.I2_Belief.toString(), Vocabulary.narra + Vocabulary.narraNames.Belief.toString());
			addEquivalentClassAxiom(Vocabulary.crminf + Vocabulary.crminfNames.I5_Inference_Making.toString(), Vocabulary.narra + Vocabulary.narraNames.Inference_Making.toString());
			addEquivalentClassAxiom(Vocabulary.crminf + Vocabulary.crminfNames.I4_Proposition_Set.toString(), Vocabulary.narra + Vocabulary.narraNames.Proposition_Set.toString());
			
			// Equivalence axioms between EFRBRoo classes and narra classes
			addEquivalentClassAxiom(Vocabulary.efrbroo + Vocabulary.efrbrooNames.F23_Expression_Fragment.toString(), Vocabulary.narra + Vocabulary.narraNames.Expression_Fragment.toString());

			// Equivalence axioms between CRMgeo classes and narra classes
			addEquivalentClassAxiom(Vocabulary.crmgeo + Vocabulary.crmgeoNames.SP2_Phenomenal_Place.toString(), Vocabulary.narra + Vocabulary.narraNames.Phenomenal_Place.toString());
			addEquivalentClassAxiom(Vocabulary.crmgeo + Vocabulary.crmgeoNames.SP4_Spatial_Coordinate_Reference_System.toString(), Vocabulary.narra + Vocabulary.narraNames.Spatial_Coordinate_Reference_System.toString());
			addEquivalentClassAxiom(Vocabulary.crmgeo + Vocabulary.crmgeoNames.SP5_Geometric_Place_Expression.toString(), Vocabulary.narra + Vocabulary.narraNames.Geometric_Place_Expression.toString());

			// Equivalence axioms between Time Ontology in OWL classes and narra classes
			addEquivalentClassAxiom(Vocabulary.time + Vocabulary.timeNames.Instant.toString(), Vocabulary.narra + Vocabulary.narraNames.Instant.toString());

			// ////////////////////////////////////////////////////////////////
			// Declaration of equivalence axioms between narra classes and reference ontologies classes
			// /////////////////////////////////////////////////////////////// 

			// CRM Object Properties
			addEquivalentObjectPropertyAxiom(Vocabulary.ecrm + Vocabulary.ecrmNames.P1_is_identified_by.toString(), Vocabulary.narra + Vocabulary.narraNames.is_identified_by.toString());
			addEquivalentObjectPropertyAxiom(Vocabulary.ecrm + Vocabulary.ecrmNames.P14_carried_out_by.toString(), Vocabulary.narra + Vocabulary.narraNames.carried_out_by.toString());
			addEquivalentObjectPropertyAxiom(Vocabulary.ecrm + Vocabulary.ecrmNames.P3_has_note.toString(), Vocabulary.narra + Vocabulary.narraNames.has_note.toString());
			addEquivalentObjectPropertyAxiom(Vocabulary.ecrm + Vocabulary.ecrmNames.P4_has_time_span.toString(), Vocabulary.narra + Vocabulary.narraNames.has_time_span.toString());
			addEquivalentObjectPropertyAxiom(Vocabulary.ecrm + Vocabulary.ecrmNames.P12_occurred_in_the_presence_of.toString(), Vocabulary.narra + Vocabulary.narraNames.occurred_in_the_presence_of.toString());
			addEquivalentObjectPropertyAxiom(Vocabulary.ecrm + Vocabulary.ecrmNames.P67_refers_to.toString(), Vocabulary.narra + Vocabulary.narraNames.refers_to.toString());
			addEquivalentObjectPropertyAxiom(Vocabulary.ecrm + Vocabulary.ecrmNames.P94_has_created.toString(), Vocabulary.narra + Vocabulary.narraNames.has_created.toString());
			addEquivalentObjectPropertyAxiom(Vocabulary.ecrm + Vocabulary.ecrmNames.P48_has_preferred_identifier.toString(), Vocabulary.narra + Vocabulary.narraNames.has_preferred_identifier.toString());
			addEquivalentObjectPropertyAxiom(Vocabulary.ecrm + Vocabulary.ecrmNames.P106_is_composed_of.toString(), Vocabulary.narra + Vocabulary.narraNames.is_composed_of.toString());
			addEquivalentObjectPropertyAxiom(Vocabulary.ecrm + Vocabulary.ecrmNames.P7_took_place_at.toString(), Vocabulary.narra + Vocabulary.narraNames.took_place_at.toString());
			addEquivalentObjectPropertyAxiom(Vocabulary.ecrm + Vocabulary.ecrmNames.P9_consists_of.toString(), Vocabulary.narra + Vocabulary.narraNames.consists_of.toString());
			addEquivalentObjectPropertyAxiom(Vocabulary.ecrm + Vocabulary.ecrmNames.P129_is_about.toString(), Vocabulary.narra + Vocabulary.narraNames.is_about.toString());
			
			// CRMinf and CRMsci Object Properties
			addEquivalentObjectPropertyAxiom(Vocabulary.crminf + Vocabulary.crmsciNames.O8_observed.toString(), Vocabulary.narra + Vocabulary.narraNames.observed.toString());
			addEquivalentObjectPropertyAxiom(Vocabulary.crminf + Vocabulary.crmsciNames.O16_observed_value.toString(), Vocabulary.narra + Vocabulary.narraNames.observed_value.toString());
			addEquivalentObjectPropertyAxiom(Vocabulary.crminf + Vocabulary.crminfNames.J1_was_premise_for.toString(), Vocabulary.narra + Vocabulary.narraNames.was_premise_for.toString());
			addEquivalentObjectPropertyAxiom(Vocabulary.crminf + Vocabulary.crminfNames.J2_concluded_that.toString(), Vocabulary.narra + Vocabulary.narraNames.concluded_that.toString());
			addEquivalentObjectPropertyAxiom(Vocabulary.crminf + Vocabulary.crminfNames.J4_that.toString(), Vocabulary.narra + Vocabulary.narraNames.that.toString());

			// GeoSPARQL Object Properties		
			addEquivalentObjectPropertyAxiom(Vocabulary.geo + Vocabulary.geoNames.hasGeometry.toString(), Vocabulary.narra + Vocabulary.narraNames.hasGeometry.toString());
			addEquivalentObjectPropertyAxiom(Vocabulary.geo + Vocabulary.geoNames.hasDefaultGeometry.toString(), Vocabulary.narra + Vocabulary.narraNames.hasDefaultGeometry.toString());

			addEquivalentDataPropertyAxiom(Vocabulary.geo + Vocabulary.geoNames.asWKT.toString(), Vocabulary.narra + Vocabulary.narraNames.asWKT.toString());
			addEquivalentDataPropertyAxiom(Vocabulary.geo + Vocabulary.geoNames.hasSerialization.toString(), Vocabulary.narra + Vocabulary.narraNames.hasSerialization.toString());

			// Time Ontology in OWL Object Properties
			addEquivalentDataPropertyAxiom(Vocabulary.time + Vocabulary.timeNames.before.toString(), Vocabulary.narra + Vocabulary.narraNames.before.toString());
			addEquivalentDataPropertyAxiom(Vocabulary.time + Vocabulary.timeNames.after.toString(), Vocabulary.narra + Vocabulary.narraNames.after.toString());

			// CNT Data Properties
			addEquivalentDataPropertyAxiom(Vocabulary.cnt + Vocabulary.cntNames.chars.toString(), Vocabulary.narra + Vocabulary.narraNames.chars.toString());

			
			
			

		}
		// GESTIONE ECCEZIONI PRINCIPALE
		catch (Exception e) {
			log.error("EXCEPTION - In OwlOntologyCreator: " + e.getMessage());
			e.getStackTrace();

		}
	}//endMethod

	/**
	 * Add a new declaration for the ObjectProperty identified from p IRI with domain d and range r
	 * @param p a String for the IRI predicate
	 * @param d a String for the IRI domain class (optional)
	 * @param r a String for the IRI range class (optional)
	 * @param desc a description for the property added like annotation
	 */
	protected void createObjPropertyWithDomainAndRange(IRI p, String d, String r) {
		OWLObjectProperty objProperty = dataFactory.getOWLObjectProperty(p);
		//TODO to add the possibility to add characteristic property
		//		OWLTransitiveObjectPropertyAxiom propertyChAxiom = dataFactory.getOWLTransitiveObjectPropertyAxiom(objProperty);
		//		manager.addAxiom(model, propertyChAxiom);
		OWLDeclarationAxiom declarationAxiom = dataFactory.getOWLDeclarationAxiom(objProperty);
		manager.addAxiom(model, declarationAxiom);
		OWLObjectPropertyDomainAxiom domainAxiom = dataFactory.getOWLObjectPropertyDomainAxiom(objProperty, dataFactory.getOWLClass(d));
		manager.addAxiom(model, domainAxiom);
		OWLObjectPropertyRangeAxiom rangeAxiom = dataFactory.getOWLObjectPropertyRangeAxiom(objProperty, dataFactory.getOWLClass(r));
		manager.addAxiom(model, rangeAxiom);
	}
	/**
	 * Add a new declaration for the DataProperty identified from p IRI with domain d and range r
	 * @param p a String for the IRI predicate
	 * @param d a String for the IRI domain class (optional)
	 * @param r a String for the IRI range primitive type (optional)
	 * @param desc a description for the property added like annotation
	 */
	protected void createDataPropertyWithDomainAndRange(IRI p, String d, String r) {
		OWLDataProperty dataProperty = dataFactory.getOWLDataProperty(p);
		OWLDeclarationAxiom declarationAxiom = dataFactory.getOWLDeclarationAxiom(dataProperty);
		manager.addAxiom(model, declarationAxiom);
		OWLDataPropertyDomainAxiom domainAxiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, dataFactory.getOWLClass(d));
		manager.addAxiom(model, domainAxiom);
		OWLDataPropertyRangeAxiom rangeAxiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, new OWLDatatypeImpl(IRI.create(r)));
		manager.addAxiom(model, rangeAxiom);
	}
	/**
	 * Add a new declaration for the ObjectProperty identified from p IRI with domain d and range r
	 * @param p a String for the IRI predicate
	 * @param d a String for the IRI domain class (optional)
	 * @param r a String for the IRI range class (optional)
	 * @param desc a description for the property added like annotation
	 */
	protected void createObjPropertyWithDomainAndRange(String p, String d, String r) {
		IRI propertyIRI = IRI.create(p);
		createObjPropertyWithDomainAndRange(propertyIRI, d, r);
	}
	/**
	 * Add a new declaration for the DataProperty identified from p IRI with domain d and range r
	 * @param p a String for the IRI predicate
	 * @param d a String for the IRI domain class (optional)
	 * @param r a String for the IRI range primitive type (optional)
	 * @param desc a description for the property added like annotation
	 */
	protected void createDataPropertyWithDomainAndRange(String p, String d, String r) {
		IRI propertyIRI = IRI.create(p);
		createDataPropertyWithDomainAndRange(propertyIRI, d, r);
	}
	/**
	 * Add a new declaration for the ObjectProperty identified from p IRI with domain d and range r
	 * @param p a String for the IRI predicate
	 * @param d a String for the IRI domain class (optional)
	 * @param r a String for the IRI range class (optional)
	 * @param desc a description for the property added like annotation
	 */
	protected void createObjPropertyWithDomainRangeAndDescription(String p, String d, String r, String desc) {
		IRI propertyIRI = IRI.create(p);
		createObjPropertyWithDomainAndRange(propertyIRI, d, r);
		//Add Annotation
		OWLAnnotationAssertionAxiom owlAnnotation = dataFactory.getOWLAnnotationAssertionAxiom(
				new OWLAnnotationPropertyImpl(IRI.create(RDFS.COMMENT.toString())), propertyIRI, new OWLLiteralImpl(desc, "", new OWLDatatypeImpl(XSDVocabulary.STRING.getIRI())));
		manager.addAxiom(model, owlAnnotation);
	}
	/**
	 * Add a new declaration for the DataProperty identified from p IRI with domain d and range r
	 * @param p a String for the IRI predicate
	 * @param d a String for the IRI domain class (optional)
	 * @param r a String for the IRI range primitive type (optional)
	 * @param desc a description for the property added like annotation
	 */
	protected void createDataPropertyWithDomainRangeAndDescription(String p, String d, String r, String desc) {
		IRI propertyIRI = IRI.create(p);
		createDataPropertyWithDomainAndRange(propertyIRI, d, r);
		//Add Annotation
		OWLAnnotationAssertionAxiom owlAnnotation = dataFactory.getOWLAnnotationAssertionAxiom(
				new OWLAnnotationPropertyImpl(IRI.create(RDFS.COMMENT.toString())), propertyIRI, new OWLLiteralImpl(desc, "", new OWLDatatypeImpl(XSDVocabulary.STRING.getIRI())));
		manager.addAxiom(model, owlAnnotation);
	}
	/**
	 * Add a Declaration for class c with description desc (note desc may be null for class without description)
	 * @param c the class for which create a new declaration axiom
	 * @param desc a description for the class added like annotation
	 */
	protected void createClassWithDescription(String c, String desc) {
		//Add class
		IRI classIRI = IRI.create(c);
		OWLClass newClass = dataFactory.getOWLClass(classIRI);
		OWLDeclarationAxiom declarationAxiom = dataFactory.getOWLDeclarationAxiom(newClass);
		manager.addAxiom(model, declarationAxiom);
		if(desc!=null && !"".equals(desc)){
			//Add Annotation
			OWLAnnotationAssertionAxiom owlAnnotation = dataFactory.getOWLAnnotationAssertionAxiom(
					new OWLAnnotationPropertyImpl(IRI.create(RDFS.COMMENT.toString())), classIRI, new OWLLiteralImpl(desc, "", new OWLDatatypeImpl(XSDVocabulary.STRING.getIRI())));
			manager.addAxiom(model, owlAnnotation);
		}

	}

	/**
	 * Add a Declaration for class c with description desc (note desc may be null for class without description)
	 * @param c the class for which create a new declaration axiom
	 * @param desc a description for the class added like annotation
	 * @param s the super class of c
	 */
	protected void createClassWithDescriptionAndSuperclass(String c, String desc, String s) {
		//Add class
		IRI classIRI = IRI.create(c);
		IRI superclassIRI = IRI.create(s);
		OWLClass newClass = dataFactory.getOWLClass(classIRI);
		OWLClass superClass = dataFactory.getOWLClass(superclassIRI);		
		OWLDeclarationAxiom declarationAxiom = dataFactory.getOWLDeclarationAxiom(newClass);
		manager.addAxiom(model, declarationAxiom);
		if(desc!=null && !"".equals(desc)){
			//Add subClassAxiom
			OWLSubClassOfAxiom subClassAxiom = dataFactory.getOWLSubClassOfAxiom(newClass, superClass);
			manager.addAxiom(model, subClassAxiom);
			
			//Add Annotation
			OWLAnnotationAssertionAxiom owlAnnotation = dataFactory.getOWLAnnotationAssertionAxiom(
					new OWLAnnotationPropertyImpl(IRI.create(RDFS.COMMENT.toString())), classIRI, new OWLLiteralImpl(desc, "", new OWLDatatypeImpl(XSDVocabulary.STRING.getIRI())));
			manager.addAxiom(model, owlAnnotation);
		}
		
	}

	/**
	 * Add object property p as subproperty of objectproperty s
	 * @param p the object property
	 * @param s the super property of p
	 */
	protected void createObjectPropertySuperproperty(String p, String s) {
		//Add class
		IRI propertyIRI = IRI.create(p);
		IRI superpropertyIRI = IRI.create(s);
		OWLObjectProperty newProperty = dataFactory.getOWLObjectProperty(propertyIRI);
		OWLObjectProperty superProperty = dataFactory.getOWLObjectProperty(superpropertyIRI);		
		OWLDeclarationAxiom declarationAxiom = dataFactory.getOWLDeclarationAxiom(newProperty);
		manager.addAxiom(model, declarationAxiom);

		//Add subPropertyAxiom
		OWLSubObjectPropertyOfAxiom subPropertyAxiom = dataFactory.getOWLSubObjectPropertyOfAxiom(newProperty, superProperty);
		manager.addAxiom(model, subPropertyAxiom);
		
	}
	/**
	 * Add object property p as subproperty of objectproperty s
	 * @param p the object property
	 * @param s the super property of p
	 */
	protected void createDataPropertySuperproperty(String p, String s) {
		//Add class
		IRI propertyIRI = IRI.create(p);
		IRI superpropertyIRI = IRI.create(s);
		OWLDataProperty newProperty = dataFactory.getOWLDataProperty(propertyIRI);
		OWLDataProperty superProperty = dataFactory.getOWLDataProperty(superpropertyIRI);		
		OWLDeclarationAxiom declarationAxiom = dataFactory.getOWLDeclarationAxiom(newProperty);
		manager.addAxiom(model, declarationAxiom);

		//Add subPropertyAxiom
		OWLSubDataPropertyOfAxiom subPropertyAxiom = dataFactory.getOWLSubDataPropertyOfAxiom(newProperty, superProperty);
		manager.addAxiom(model, subPropertyAxiom);
		
	}
	protected void addEquivalentClassAxiom(String c1, String c2) throws Exception {
		// Create equivalent class axiom
		OWLClass class1 = dataFactory.getOWLClass(IRI.create(c1));
		OWLClass class2 = dataFactory.getOWLClass(IRI.create(c2));
		OWLEquivalentClassesAxiom equivalenceAxiom = dataFactory.getOWLEquivalentClassesAxiom(class1, class2);
		manager.addAxiom(model, equivalenceAxiom);
	}//end method
	protected void addEquivalentObjectPropertyAxiom(String op1, String op2) throws Exception {
		// Create equivalent class axiom
		OWLObjectProperty objectProperty1 = dataFactory.getOWLObjectProperty(IRI.create(op1));
		OWLObjectProperty objectProperty2 = dataFactory.getOWLObjectProperty(IRI.create(op2));
		OWLEquivalentObjectPropertiesAxiom equivalenceAxiom = dataFactory.getOWLEquivalentObjectPropertiesAxiom(objectProperty1, objectProperty2);
		manager.addAxiom(model, equivalenceAxiom);
	}//end method
	protected void addEquivalentDataPropertyAxiom(String dp1, String dp2) throws Exception {
		// Create equivalent class axiom
		OWLDataProperty dataProperty1 = dataFactory.getOWLDataProperty(IRI.create(dp1));
		OWLDataProperty dataProperty2 = dataFactory.getOWLDataProperty(IRI.create(dp2));
		OWLEquivalentDataPropertiesAxiom equivalenceAxiom = dataFactory.getOWLEquivalentDataPropertiesAxiom(dataProperty1, dataProperty2);
		manager.addAxiom(model, equivalenceAxiom);
	}//end method
}

