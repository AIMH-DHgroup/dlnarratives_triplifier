package narra.triplifier.model;

import java.io.Console;

import java.io.IOException;
import java.io.PrintWriter;


import java.io.StringWriter;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jena.vocabulary.RDFS;
import org.apache.log4j.Logger;
import org.eclipse.rdf4j.model.vocabulary.DC;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.gml2.GMLWriter;
import org.openrdf.model.vocabulary.OWL;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

import org.geotools.*;
import org.geotools.referencing.crs.DefaultGeographicCRS;

import com.bigdata.rdf.internal.impl.uri.VocabURIShortIV;

import narra.triplifier.resource.ActorWithRole;
import narra.triplifier.resource.Concept;
import narra.triplifier.resource.Other;
import narra.triplifier.resource.Entity;
import narra.triplifier.resource.Event;
import narra.triplifier.resource.Narrative;
import narra.triplifier.resource.PhysicalObject;
import narra.triplifier.resource.Place;
import narra.triplifier.resource.Proposition;
import narra.triplifier.resource.Reference;
import narra.triplifier.resource.Vocabulary;
import narra.triplifier.resource.Work;
import narra.triplifier.util.Log4JClass;

public class OWLOntologyPopulator {
	
	private HashMap<String, String> eventTypes = new HashMap<String, String>();

	private HashMap<String, Integer> LAUS = new HashMap<String, Integer>();
	

	// Get logger instance
	private Logger log = null;
	private OWLDataFactory dataFactory;
	
	public OWLOntologyPopulator() {
		dataFactory = OWLManager.getOWLDataFactory();
		log = Log4JClass.getLogger();
	}

	public String convertDecimal(String n) throws ParseException{
		String regex = "\\-?\\d+\\,\\d+";
		Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(n);
		boolean matchFound = matcher.find();
		if(matchFound) {
		
			NumberFormat nf_in = NumberFormat.getNumberInstance(Locale.GERMANY);
			double val = nf_in.parse(n).doubleValue();

			NumberFormat nf_out = NumberFormat.getNumberInstance(Locale.UK);
			nf_out.setMaximumFractionDigits(3);
			String output = nf_out.format(val);
			
			return output;
		} else {
			return n;
		}
		
	}

	static public String WKTToGML(String wkt) throws IOException, org.locationtech.jts.io.ParseException {
		WKTReader wktR = new WKTReader();
		Geometry geometry = wktR.read(wkt);
		// Parse WKT string to JTS Geometry object
            
            // Create a GML writer
            GMLWriter gmlWriter = new GMLWriter();
            
            // Set the output CRS (Coordinate Reference System)
            gmlWriter.setSrsName("http://www.opengis.net/def/crs/OGC/1.3/CRS84");
            
            // Convert JTS Geometry object to GML string
            String gml = gmlWriter.write(geometry);
            
            //System.out.println("GML output:");
            //System.out.println(gml);
			return gml;
	
	  }

	/**
	 * Loads a narrative and triplifies it according to our ontology model
	 * @param model The ontology model expressed through OWL API
	 * @param narrative A Narrative object containing the data to be triplified
	 * @return void
	 */
	public void populateOntology(OWLOntology model, Narrative narrative) {
		
		// CIDOC CRM event types
		eventTypes.put("creation",     Vocabulary.narra + Vocabulary.narraNames.Creation.toString());
		eventTypes.put("birth",        Vocabulary.narra + Vocabulary.narraNames.Birth.toString());
		eventTypes.put("death",        Vocabulary.narra + Vocabulary.narraNames.Death.toString());
		eventTypes.put("joining",      Vocabulary.narra + Vocabulary.narraNames.Joining.toString());
		eventTypes.put("leaving",      Vocabulary.narra + Vocabulary.narraNames.Leaving.toString());
		// Wikidata event types
		eventTypes.put("baptism",      Vocabulary.wikidata + "Q35856");
		eventTypes.put("battle",       Vocabulary.wikidata + "Q178561");
		eventTypes.put("crowning",     Vocabulary.wikidata + "Q209715");
		eventTypes.put("divorce",      Vocabulary.wikidata + "Q93190");
		eventTypes.put("education",    Vocabulary.wikidata + "Q8434");
		eventTypes.put("election",     Vocabulary.wikidata + "Q40231");
		eventTypes.put("exile",        Vocabulary.wikidata + "Q188863");
		eventTypes.put("expedition",   Vocabulary.wikidata + "Q2401485");
		eventTypes.put("lawmaking",    Vocabulary.wikidata + "Q1725430");
		eventTypes.put("lecture",      Vocabulary.wikidata + "Q603773");
		eventTypes.put("legation",     Vocabulary.wikidata + "Q2737008");
		eventTypes.put("marriage",     Vocabulary.wikidata + "Q8445");
		eventTypes.put("meeting",      Vocabulary.wikidata + "Q2761147");
		eventTypes.put("residence",    Vocabulary.wikidata + "Q2359691");
		eventTypes.put("sentence",     Vocabulary.wikidata + "Q1763090");
		eventTypes.put("travel",       Vocabulary.wikidata + "Q61509");
		eventTypes.put("war",          Vocabulary.wikidata + "Q198");
		eventTypes.put("history", 	   Vocabulary.wikidata + "Q309");
		eventTypes.put("nature",	   Vocabulary.wikidata + "Q7860");

		//Q309 history  Q7860 nature  Q646107 value chain???   //iri generico per nonHaClasse? (tipo � obbligatoria)
		//eventTypes.put("history", Vocabulary.wikidata + "Q309");
		//eventTypes.put("nature", Vocabulary.wikidata + "Q7860");
		//eventTypes.put("value_chain", Vocabulary.wikidata + "Q646107");
		
		
		// Set ID of first textual fragment
		int fragmentID = 1;
		
		// Event type URI will be set later
		String eventTypeURI = null;
		final String DCTypeText = "http://purl.org/dc/dcmitype/Text";
		try {
			//log.info("ENTER OWLOntologyPopulator");
			
			// Load list of events
			ArrayList<Event> eventList = narrative.getEvents();
			
			// Load list of linked resources
			HashMap<String, Entity> wikiResources = narrative.getEntities();

			int countIdLAU = 0;
			
			
			
			// Generate random ID for narrator -- FIXME should be set by the tool
			//UUID narratorID = UUID.randomUUID();
			
			// Load ID from narrative object
			String narraID = narrative.getId();
						
			// Create resources for narrator, narrative, and creation event
			String resource_Narrator = Vocabulary.base + "narrator/" + narrative.getAuthor().replace(" ", "_");			
			String resource_Narrative = Vocabulary.base + "narrative/" + narraID;
			String resource_NarrativeType =  Vocabulary.narra + Vocabulary.narraNames.Narrative;
			String resource_NarrativeCreationEvent = Vocabulary.base + "narrative/" + narraID + "/creation";
			
			// Add narrative name and author to the model
			addDataPropertyAssertion(model, resource_Narrative, RDFS.label.toString(), narrative.getName(), null);
			addDataPropertyAssertion(model, resource_Narrator, RDFS.label.toString(), narrative.getAuthor(), null);
						
			// Add relations between creation event, narrator and narrative 
			addObjectPropertyAssertion(model, resource_NarrativeCreationEvent, Vocabulary.narra + Vocabulary.narraNames.carried_out_by.toString(), resource_Narrator,
					new String[]{Vocabulary.narra + Vocabulary.narraNames.Event.toString(), Vocabulary.narra + Vocabulary.narraNames.Actor.toString()});
			addObjectPropertyAssertion(model, resource_NarrativeCreationEvent, Vocabulary.narra + Vocabulary.narraNames.has_created.toString(), resource_Narrative, 
					new String[]{null, resource_NarrativeType});
			
			// Add relation between narrative and visualization (storymap/timeline) link
			addObjectPropertyAssertion(model, resource_Narrative, Vocabulary.narra + Vocabulary.narraNames.has_preferred_identifier.toString(), "https://tool.dlnarratives.eu/storymaps/" + narrative.getAuthor() + "/" + narraID + "/",
			//addObjectPropertyAssertion(model, resource_Narrative, Vocabulary.ecrm+Vocabulary.ecrmNames.P48_has_preferred_identifier.toString(), "https://tool.dlnarratives.eu/verticalstorymap3D/violtwin.php?user=adminNarra.1&id=" + narraID ,
					new String[]{null, null});
			
			ArrayList<String> iriEventsList = new ArrayList<String>();

			// For each event in list...
			for (int j = 0; j < eventList.size(); j++) {

				// Load event and URI
				Event event = eventList.get(j);
				String eventURI = Vocabulary.base + "narrative/" + narraID + "/" + event.getUri();
				//add iriEvent to the list to loop in the list and assign property "eventBefor"
				iriEventsList.add(eventURI);
				
				addObjectPropertyAssertion(model, eventURI, 
						Vocabulary.narra + Vocabulary.narraNames.partOfNarrative.toString(), 
						resource_Narrative, new String[]{Vocabulary.narra + Vocabulary.narraNames.Event.toString(),Vocabulary.narra + Vocabulary.narraNames.Narrative.toString()});
				
								
				// Log event number and URI
				log.debug("Event " + j + ": " + eventURI);

				// Load event properties
				String eventTitle = event.getEventTitle();
				String eventType = event.getEventType();
				String startDate = event.getStartDate();
				String endDate = event.getEndDate();
				ArrayList<String> digObjsURI = event.getDigitalObjects();
				String eventNotes = event.getEventNotes();
				String eventLatitude= convertDecimal(event.getLatitude());
				String eventLongitude= convertDecimal(event.getLongitude());
				String eventMedia= event.getEventMedia();
				String eventDescription= event.getDescription();
				String eventPolygon = event.getPolygon();
				String eventId3DModel= event.getId3DModel();
				String eventAnnotation3DModel= event.getAnnotation3DModel();
				

				
				if(!startDate.isEmpty()) {
					// Fix dates for datatype XSD:date
					if (startDate.replace("-","").length() < 5) startDate += "-01-01";
					if (endDate.replace("-","").length() < 5) endDate += "-12-31";
					
					if (! startDate.startsWith("-")) {
						if (startDate.split("-")[0].length() == 2) {
							startDate = "00" + startDate;
						}
						else if (startDate.split("-")[0].length() == 3) {
							startDate = "0" + startDate;
						}
					} else {
						startDate = startDate.substring(1);
						if (startDate.split("-")[0].length() == 2) {
							startDate = "00" + startDate;
						}
						else if (startDate.split("-")[0].length() == 3) {
							startDate = "0" + startDate;
						}
						startDate = "-" + startDate;
					}
					
					if (! endDate.startsWith("-")) {
						if (endDate.split("-")[0].length() == 2) {
							endDate = "00" + endDate;
						}
						else if (endDate.split("-")[0].length() == 3) {
							endDate = "0" + endDate;
						}
					} else {
						endDate = endDate.substring(1);
						if (endDate.split("-")[0].length() == 2) {
							endDate = "00" + endDate;
						}
						else if (endDate.split("-")[0].length() == 3) {
							endDate = "0" + endDate;
						}
						endDate = "-" + endDate;
					}
				} 
				
				// Add eventMedia to Model
				if(!eventMedia.isEmpty()) {
					
					// if there are many media (a string comma separated)
					if(eventMedia.contains(",")) {
						
						String[] arrayOfMedia = eventMedia.split(",");
						
			            for (String element : arrayOfMedia) {
			            	
							addObjectPropertyAssertion(model, element, 
									Vocabulary.narra + Vocabulary.narraNames.refers_to.toString(), 
									eventURI, new String[]{Vocabulary.narra + Vocabulary.narraNames.Information_Object.toString(),eventTypeURI});
			            
			            }
					
			        //if there is only a media (a string)
					} else {
					
						addObjectPropertyAssertion(model, eventMedia, 
								Vocabulary.narra + Vocabulary.narraNames.refers_to.toString(), 
								eventURI, new String[]{Vocabulary.narra + Vocabulary.narraNames.Information_Object.toString(),eventTypeURI});
					}
				}
				
				// Add 3D Model to Model
				if(!eventId3DModel.isEmpty()) {
					addObjectPropertyAssertion(model, Vocabulary.base  + "3DModel/" + eventId3DModel + "_" + eventAnnotation3DModel, 
							Vocabulary.narra + Vocabulary.narraNames.refers_to.toString(), 
							eventURI, new String[]{Vocabulary.narra + Vocabulary.narraNames.Visual_Item.toString(),eventTypeURI});
				}
				
				// add latitude and longitude to Model
				if(!eventLatitude.isEmpty() && !eventLongitude.isEmpty()) {
					
					// add the triple E5 Event, P7_took_place_at, SP2 Phenomenal Place
					addObjectPropertyAssertion(model, eventURI, Vocabulary.narra + Vocabulary.narraNames.took_place_at.toString(),
					Vocabulary.base  + "phenomenal_place/" + eventLatitude + "_" + eventLongitude, 
					new String[]{Vocabulary.narra + Vocabulary.narraNames.Event.toString(),Vocabulary.narra + Vocabulary.narraNames.Phenomenal_Place.toString()});
			
					// add the triple SP5_Geometric_Place_Expression, Q9_is_expressed_in_terms_of, SP4_Spatial_Coordinate_Reference_System
					addObjectPropertyAssertion(model, Vocabulary.base  + "place/" + eventLatitude + "_" + eventLongitude, 
							Vocabulary.narra + Vocabulary.narraNames.is_expressed_in_terms_of.toString(),
							"http://www.opengis.net/def/crs/OGC/1.3/CRS84", 
							new String[]{Vocabulary.narra + Vocabulary.narraNames.Geometric_Place_Expression.toString(),Vocabulary.narra + Vocabulary.narraNames.Spatial_Coordinate_Reference_System.toString()});
					
					// add the triple SP5_Geometric_Place_Expression, asWKT, WktLiteral
					addDataPropertyAssertion(model, Vocabulary.base  + "place/" + eventLatitude + "_" + eventLongitude, 
						Vocabulary.narra + Vocabulary.narraNames.asWKT.toString(), 
						"<http://www.opengis.net/def/crs/OGC/1.3/CRS84> "+"POINT(" + eventLongitude + " " + eventLatitude + ")" , 
						new String[]{Vocabulary.narra + Vocabulary.narraNames.Geometric_Place_Expression.toString(), "http://www.opengis.net/ont/geosparql#wktLiteral"});
					
					// add the triple SP5_Geometric_Place_Expression, asGML, GMLLiteral
					addDataPropertyAssertion(model, Vocabulary.base  + "place/" + eventLatitude + "_" + eventLongitude, 
						Vocabulary.narra + Vocabulary.narraNames.asGML.toString(), 
						WKTToGML("POINT(" + eventLongitude + " " + eventLatitude + ")") , 
						new String[]{Vocabulary.narra + Vocabulary.narraNames.Geometric_Place_Expression.toString(), "http://www.opengis.net/ont/geosparql#gmlLiteral"});

						
					// add the triple SP4 Spatial Coordinate Reference System, Q7 describes, SP3 Reference Space
					addObjectPropertyAssertion(model, "http://www.opengis.net/def/crs/OGC/1.3/CRS84",  
						Vocabulary.narra + Vocabulary.narraNames.describes.toString(),
						Vocabulary.wikidata + "Q17295", 
						new String[]{Vocabulary.narra + Vocabulary.narraNames.Spatial_Coordinate_Reference_System.toString(),Vocabulary.narra + Vocabulary.narraNames.Reference_Space.toString()});
					
					// add the triple SP2 Phenomenal Place, has geometry, SP5_Geometric_Place_Expression
					addObjectPropertyAssertion(model, Vocabulary.base  + "phenomenal_place/" + eventLatitude + "_" + eventLongitude, 
						Vocabulary.narra + Vocabulary.narraNames.hasGeometry.toString(),
						Vocabulary.base + "place/" +  eventLatitude + "_" + eventLongitude,
						new String[]{Vocabulary.narra + Vocabulary.narraNames.Phenomenal_Place.toString(),Vocabulary.narra + Vocabulary.narraNames.Geometric_Place_Expression.toString()});

					// add the triple SP2 Phenomenal Place, has default geometry, SP5_Geometric_Place_Expression
					addObjectPropertyAssertion(model, Vocabulary.base  + "phenomenal_place/" + eventLatitude + "_" + eventLongitude, 
						Vocabulary.narra + Vocabulary.narraNames.hasDefaultGeometry.toString(),
						Vocabulary.base + "place/" +  eventLatitude + "_" + eventLongitude,
						new String[]{Vocabulary.narra + Vocabulary.narraNames.Phenomenal_Place.toString(),Vocabulary.narra + Vocabulary.narraNames.Geometric_Place_Expression.toString()});

				}
				
				

				// Load causal and mereological relations attached to event
				ArrayList<String> eventCausedBy = event.getCausedBy();
				ArrayList<String> eventPartOf = event.getPartOf();
				
				// Load propositions attached to event
				ArrayList<Proposition> propositionList = event.getPropositionList();
				

				// If event type is known, load it from event type list
				if (eventTypes.containsKey(eventType)) {
					eventTypeURI = eventTypes.get(eventType);
				} else {
					log.debug("Missing URI for event type: " + eventType);
					eventTypeURI = Vocabulary.base + "event-type/" + eventType.replace(" ", "_");
				}
				
				String eventTitleURI = Vocabulary.base + "appellation/" + eventTitle.replace(" ", "_");
				// Log event title and type
				log.debug("Event title: " + eventTitle);
				log.debug("Event type: " + eventTypeURI);
				
				// Event type must be a subclass of ecrm:E5_Event
				OWLSubClassOfAxiom subClassAxiom = dataFactory.getOWLSubClassOfAxiom(dataFactory.getOWLClass(eventTypeURI),
						dataFactory.getOWLClass(Vocabulary.narra + Vocabulary.narraNames.Event.toString()));
				AddAxiom addAxiomChange = new AddAxiom(model, subClassAxiom);
				model.applyChange(addAxiomChange);

				// Add mereological relations to the model (invert part of == consists of) and create resources for event, event title, event type; 
				// and create literal for event title and for start/end dates
				for (int i = 0; i < eventPartOf.size(); i++) {
					addObjectPropertyAssertion(model, Vocabulary.base + "narrative/" + narraID + "/event/" + eventPartOf.get(i), 
							Vocabulary.narra + Vocabulary.narraNames.consists_of.toString(), 
							eventURI, new String[]{null,eventTypeURI});
				}
				
				// Add causal relations to model
				for (int i = 0; i < eventCausedBy.size(); i++) {
					addObjectPropertyAssertion(model, eventURI, 
							Vocabulary.narra + Vocabulary.narraNames.causallyDependsOn.toString(), 
							Vocabulary.base + "narrative/" + narraID + "/event/" + eventCausedBy.get(i), new String[]{eventTypeURI,null});
				}

				// Add digital object and link it to event
				if (digObjsURI != null && digObjsURI.size() > 0) {
					for (String digObjURI: digObjsURI) {
						log.debug(digObjURI);
						addObjectPropertyAssertion(model, digObjURI, 
								Vocabulary.narra + Vocabulary.narraNames.refers_to.toString(), 
								eventURI, new String[]{Vocabulary.narra + Vocabulary.narraNames.Information_Object.toString(),eventTypeURI});	
					}
				}

				// Add event notes and link them to event
				if (eventNotes != null && !eventNotes.equals("")) {
					//log.info(eventNotes);
					addDataStringPropertyAssertions(model, eventURI, Vocabulary.narra + Vocabulary.narraNames.has_note.toString(), eventNotes, 
							eventTypeURI);
				}
				
				if (eventDescription != null && !eventDescription.equals("")) {
					//log.info(eventDescription);
					addDataPropertyAssertion(model, eventURI, Vocabulary.narra + Vocabulary.narraNames.hasDescription.toString(), eventDescription, new String[]{null, null});
				}

				if(!startDate.isEmpty()) {
				
					String timeSpan = Vocabulary.base + "time/" + startDate + "-" + endDate;
					String timeSpanType = Vocabulary.narra + Vocabulary.narraNames.Time_Span;
					String instantType = Vocabulary.narra + Vocabulary.narraNames.Instant;
					
					// Create beginning/end instants for time span, and add relations between time instants and date literals
					addDataPropertyAssertion(model, Vocabulary.base + "time/" + startDate, Vocabulary.narra + Vocabulary.narraNames.inXSDDate.toString(), 
					startDate, new String[]{instantType, XSDVocabulary.DATE.getIRI().toString()});
					addDataPropertyAssertion(model, Vocabulary.base + "time/" + endDate, Vocabulary.narra + Vocabulary.narraNames.inXSDDate.toString(), 
					endDate, new String[]{instantType, XSDVocabulary.DATE.getIRI().toString()});
	
					// Add relations between time span and its beginning/end instants
					addObjectPropertyAssertion(model, timeSpan, Vocabulary.narra + Vocabulary.narraNames.timeSpanStartedBy.toString(), 
					Vocabulary.base + "time/" + startDate, new String[]{timeSpanType, instantType});
					addObjectPropertyAssertion(model, timeSpan, Vocabulary.narra + Vocabulary.narraNames.timeSpanFinishedBy.toString(), 
					Vocabulary.base + "time/" + endDate, new String[]{timeSpanType, instantType});
					
				
				
					// Add relations between event and time span
					addObjectPropertyAssertion(model, eventURI, Vocabulary.narra + Vocabulary.narraNames.has_time_span.toString(), 
							timeSpan, new String[]{eventTypeURI, timeSpanType});
				
				}

				// Add relations between event, title resource and title literal
				addObjectPropertyAssertion(model, eventURI, Vocabulary.narra + Vocabulary.narraNames.is_identified_by.toString(), 
						eventTitleURI, new String[]{eventTypeURI, Vocabulary.narra + Vocabulary.narraNames.Appellation});
				addDataPropertyAssertion(model, eventTitleURI, Vocabulary.narra + Vocabulary.narraNames.has_note.toString(), 
						eventTitle, new String[]{null, XSDVocabulary.STRING.getIRI().toString()});
				
				// Add type ecrm:E5_Event explicitly
				addObjectPropertyAssertion(model, eventURI, RDF.TYPE.toString(), Vocabulary.narra + Vocabulary.narraNames.Event.toString(), new String[]{null,null});

				try {
					// For each proposition...
					for (int i = 0; i < propositionList.size(); i++) {
						
						

						// Log proposition number
						log.debug("PROPOSIZIONE " + i);
						//System.out.println(eventTitle);
						//System.out.println("PROPOSIZIONE " + i);

						// Load proposition from list
						Proposition proposition = propositionList.get(i);

						// Set URIs for proposition resources
						String propositionURI = eventURI + "/proposition/" + (i + 1);
						String propositionSetURI = eventURI + "/proposition-set/" + (i + 1);
						String propositionBeliefURI = eventURI + "/belief/" + (i + 1);

						// Log URIs for proposition resources
						log.debug(propositionURI);
						log.debug(propositionSetURI);
						log.debug(propositionBeliefURI);
						log.debug(proposition.getResourceID());
						
						
						//System.out.println("Risorsa ID " + i);
						
						String resourceID = proposition.getResourceID();
						//System.out.println(resourceID);
						
						String propositionNotes = proposition.getNotes();
						Entity wikiResource = wikiResources.get(resourceID);
						
						
						

						String resourceType = wikiResource.getType();

						if (propositionNotes != null) {
							addDataPropertyAssertion(model, propositionURI, Vocabulary.narra + Vocabulary.narraNames.has_note.toString(), 
									propositionNotes, new String[]{Vocabulary.narra + Vocabulary.narraNames.Proposition.toString(), XSDVocabulary.STRING.getIRI().toString()});
						}

						addObjectPropertyAssertion(model, propositionURI, Vocabulary.narra+Vocabulary.narraNames.propSubject.toString(), eventURI, new String[]{Vocabulary.narra + Vocabulary.narraNames.Proposition.toString(), eventTypeURI});
						addObjectPropertyAssertion(model, propositionSetURI, Vocabulary.narra + Vocabulary.narraNames.is_composed_of.toString(), propositionURI, 
								new String[]{Vocabulary.narra + Vocabulary.narraNames.Proposition_Set.toString(), Vocabulary.narra + Vocabulary.narraNames.Proposition.toString()});
						addObjectPropertyAssertion(model, propositionBeliefURI, Vocabulary.narra + Vocabulary.narraNames.that.toString(), propositionSetURI,
								new String[]{Vocabulary.narra + Vocabulary.narraNames.Belief.toString(), null});
							
						// The narrator needs to be linked to his/her belief
						addObjectPropertyAssertion(model, resource_Narrator, Vocabulary.narra+Vocabulary.narraNames.holdsBelief.toString(), propositionBeliefURI,
								new String[]{Vocabulary.narra + Vocabulary.narraNames.Belief.toString(), null});
						
						log.debug(resourceType);

						if (resourceType.equals("person") || resourceType.equals("organization")) {
							
							log.debug(wikiResource.getName());
							ActorWithRole personWithRole = (ActorWithRole) wikiResource;
							String actorAppellationURI = personWithRole.getPersonURI() + "/appellation";
							
							addDataPropertyAssertion(model, personWithRole.getURI(), RDFS.label.toString(), personWithRole.getName(), new String[]{null, null});
							addDataPropertyAssertion(model, personWithRole.getURI(), RDFS.comment.toString(), personWithRole.getDescription(), new String[]{null, null});
							
							addObjectPropertyAssertion(model, personWithRole.getURI(), Vocabulary.narra + Vocabulary.narraNames.is_identified_by.toString(), actorAppellationURI, new String[]{null, null});
							addDataPropertyAssertion(model, actorAppellationURI, Vocabulary.narra + Vocabulary.narraNames.has_note.toString(), personWithRole.getName(), 
									new String[]{Vocabulary.narra + Vocabulary.narraNames.Appellation.toString(), XSDVocabulary.STRING.getIRI().toString()});

							String role = personWithRole.getRole();
							addObjectPropertyAssertion(model, propositionURI, Vocabulary.narra + Vocabulary.narraNames.propPredicate.toString(), Vocabulary.narra+Vocabulary.narraNames.hadParticipant.toString(), 
									new String[]{Vocabulary.narra + Vocabulary.narraNames.Proposition.toString(), null});
							
							addObjectPropertyAssertion(model, eventURI, Vocabulary.narra+Vocabulary.narraNames.hasEntity.toString(), personWithRole.getURI(), new String[]{null, null});
							addObjectPropertyAssertion(model, personWithRole.getURI(), Vocabulary.narra+Vocabulary.narraNames.isEntityOf.toString(), eventURI, new String[]{null, null});
							//System.out.println(eventURI);
							//System.out.println(object.getURI());
							
							
							
							
							
							
							if (resourceType == "person") {
								addObjectPropertyAssertion(model, personWithRole.getURI(), RDF.TYPE.toString(), Vocabulary.narra + Vocabulary.narraNames.Person.toString(), new String[]{null,null});
							}
							
							addObjectPropertyAssertion(model, personWithRole.getURI(), Vocabulary.narra+Vocabulary.narraNames.hasSubject.toString(), personWithRole.getPersonURI(),
									new String[]{Vocabulary.narra + Vocabulary.narraNames.ActorWithRole.toString(), Vocabulary.narra + Vocabulary.narraNames.Actor});
							
							addObjectPropertyAssertion(model, propositionURI, Vocabulary.narra+Vocabulary.narraNames.propObject.toString(), personWithRole.getURI(), new String[]{Vocabulary.narra + Vocabulary.narraNames.Proposition.toString(), null});

							if (role != null) {
								addObjectPropertyAssertion(model, personWithRole.getURI(), Vocabulary.narra+Vocabulary.narraNames.hasRole.toString(), Vocabulary.base + "role/" + role.replace(" ", "_"), new String[]{null, null});
							}
						}

						else if (resourceType.equals("object")) {
							PhysicalObject object = (PhysicalObject) wikiResource;
							String objectAppellationURI = object.getURI() + "/appellation";
							
							addDataPropertyAssertion(model, object.getURI(), RDFS.label.toString(), object.getName(), new String[]{null, null});
							addDataPropertyAssertion(model, object.getURI(), RDFS.comment.toString(), object.getDescription(), new String[]{null, null});
							
							addObjectPropertyAssertion(model, object.getURI(), Vocabulary.narra + Vocabulary.narraNames.is_identified_by.toString(), objectAppellationURI, 
									new String[]{Vocabulary.narra + Vocabulary.narraNames.Physical_Object.toString(), null});
							addDataPropertyAssertion(model, objectAppellationURI, Vocabulary.narra + Vocabulary.narraNames.has_note.toString(), object.getName(), 
									new String[]{Vocabulary.narra + Vocabulary.narraNames.Appellation.toString(), XSDVocabulary.STRING.getIRI().toString()});
							addObjectPropertyAssertion(model, propositionURI, Vocabulary.narra+Vocabulary.narraNames.propPredicate.toString(), Vocabulary.narra + Vocabulary.narraNames.occurred_in_the_presence_of.toString(), 
									new String[]{Vocabulary.narra + Vocabulary.narraNames.Proposition.toString(), null});
							addObjectPropertyAssertion(model, propositionURI, Vocabulary.narra+Vocabulary.narraNames.propObject.toString(), object.getURI(), 
									new String[]{Vocabulary.narra + Vocabulary.narraNames.Proposition.toString(), Vocabulary.narra + Vocabulary.narraNames.Physical_Object.toString()});
							
							
							
							addObjectPropertyAssertion(model, eventURI, Vocabulary.narra+Vocabulary.narraNames.hasEntity.toString(), object.getURI(), new String[]{null, null});
							addObjectPropertyAssertion(model, object.getURI(), Vocabulary.narra+Vocabulary.narraNames.isEntityOf.toString(), eventURI, new String[]{null, null});
							//System.out.println(eventURI);
							//System.out.println(object.getURI());
						}

						else if (resourceType.equals("concept")) {
							Concept object = (Concept) wikiResource;
							String objectAppellationURI = object.getURI() + "/appellation";
							
							addDataPropertyAssertion(model, object.getURI(), RDFS.label.toString(), object.getName(), new String[]{null, null});
							addDataPropertyAssertion(model, object.getURI(), RDFS.comment.toString(), object.getDescription(), new String[]{null, null});

							addObjectPropertyAssertion(model, object.getURI(), Vocabulary.narra + Vocabulary.narraNames.is_identified_by.toString(), objectAppellationURI, 
									new String[]{Vocabulary.narra + Vocabulary.narraNames.Propositional_Object.toString(), null});
							addDataPropertyAssertion(model, objectAppellationURI, Vocabulary.narra + Vocabulary.narraNames.has_note.toString(), object.getName(), 
									new String[]{Vocabulary.narra + Vocabulary.narraNames.Appellation.toString(), XSDVocabulary.STRING.getIRI().toString()});
							addObjectPropertyAssertion(model, propositionURI, Vocabulary.narra+Vocabulary.narraNames.propPredicate.toString(), Vocabulary.narra + Vocabulary.narraNames.occurred_in_the_presence_of.toString(), 
									new String[]{Vocabulary.narra + Vocabulary.narraNames.Proposition.toString(), null});
							addObjectPropertyAssertion(model, propositionURI, Vocabulary.narra+Vocabulary.narraNames.propObject.toString(), object.getURI(), 
									new String[]{Vocabulary.narra + Vocabulary.narraNames.Proposition.toString(), Vocabulary.narra + Vocabulary.narraNames.Propositional_Object.toString()});
						
						
						
							addObjectPropertyAssertion(model, eventURI, Vocabulary.narra+Vocabulary.narraNames.hasEntity.toString(), object.getURI(), new String[]{null, null});
							addObjectPropertyAssertion(model, object.getURI(), Vocabulary.narra+Vocabulary.narraNames.isEntityOf.toString(), eventURI, new String[]{null, null});
							//System.out.println(eventURI);
							//System.out.println(object.getURI());
						
						}

						else if (resourceType.equals("work")) {
							Work object = (Work) wikiResource;
							String objectAppellationURI = object.getURI() + "/appellation";
							
							addDataPropertyAssertion(model, object.getURI(), RDFS.label.toString(), object.getTitle(), new String[]{null, null});
							addDataPropertyAssertion(model, object.getURI(), RDFS.comment.toString(), object.getDescription(), new String[]{null, null});

							addObjectPropertyAssertion(model, object.getURI(), Vocabulary.narra + Vocabulary.narraNames.is_identified_by.toString(), objectAppellationURI, 
									new String[]{Vocabulary.narra + Vocabulary.narraNames.Information_Object.toString(), null});
							addDataPropertyAssertion(model, objectAppellationURI, Vocabulary.narra + Vocabulary.narraNames.has_note.toString(), object.getTitle(), 
									new String[]{Vocabulary.narra + Vocabulary.narraNames.Appellation.toString(), XSDVocabulary.STRING.getIRI().toString()});
							addObjectPropertyAssertion(model, propositionURI, Vocabulary.narra+Vocabulary.narraNames.propPredicate.toString(), Vocabulary.narra + Vocabulary.narraNames.occurred_in_the_presence_of.toString(), 
									new String[]{Vocabulary.narra + Vocabulary.narraNames.Proposition.toString(), null});
							addObjectPropertyAssertion(model, propositionURI, Vocabulary.narra+Vocabulary.narraNames.propObject.toString(), object.getURI(), 
									new String[]{Vocabulary.narra + Vocabulary.narraNames.Proposition.toString(), Vocabulary.narra + Vocabulary.narraNames.Information_Object.toString()});
						
						
							addObjectPropertyAssertion(model, eventURI, Vocabulary.narra+Vocabulary.narraNames.hasEntity.toString(), object.getURI(), new String[]{null, null});
							addObjectPropertyAssertion(model, object.getURI(), Vocabulary.narra+Vocabulary.narraNames.isEntityOf.toString(), eventURI, new String[]{null, null});
							//System.out.println(eventURI);
							//System.out.println(object.getURI());
						
						}

						else if (resourceType.equals("place")) {
							Place place = (Place) wikiResource;
							String objectAppellationURI = place.getURI() + "/appellation";
							
							addDataPropertyAssertion(model, place.getURI(), RDFS.label.toString(), place.getName(), new String[]{null, null});
							addDataPropertyAssertion(model, place.getURI(), RDFS.comment.toString(), place.getDescription(), new String[]{null, null});

							addObjectPropertyAssertion(model, place.getURI(), Vocabulary.narra + Vocabulary.narraNames.is_identified_by.toString(), objectAppellationURI, 
									new String[]{Vocabulary.narra + Vocabulary.narraNames.Place.toString(), null});
							addDataPropertyAssertion(model, objectAppellationURI, Vocabulary.narra + Vocabulary.narraNames.has_note.toString(), place.getName(), 
									new String[]{Vocabulary.narra + Vocabulary.narraNames.Appellation.toString(), XSDVocabulary.STRING.getIRI().toString()});
							addObjectPropertyAssertion(model, propositionURI, Vocabulary.narra+Vocabulary.narraNames.propPredicate.toString(), Vocabulary.narra + Vocabulary.narraNames.took_place_at.toString(), 
									new String[]{Vocabulary.narra + Vocabulary.narraNames.Proposition.toString(), null});
							addObjectPropertyAssertion(model, propositionURI, Vocabulary.narra+Vocabulary.narraNames.propObject.toString(), place.getURI(), 
									new String[]{Vocabulary.narra + Vocabulary.narraNames.Proposition.toString(), Vocabulary.narra + Vocabulary.narraNames.Place.toString()});
						
						
						
							addObjectPropertyAssertion(model, eventURI, Vocabulary.narra+Vocabulary.narraNames.hasEntity.toString(), place.getURI(), new String[]{null, null});
							addObjectPropertyAssertion(model, place.getURI(), Vocabulary.narra+Vocabulary.narraNames.isEntityOf.toString(), eventURI, new String[]{null, null});
							//System.out.println(eventURI);
							//System.out.println(object.getURI());
						
						}
						
						else if (resourceType.equals("other")) {
							Other object = (Other) wikiResource;
							String objectAppellationURI = object.getURI() + "/appellation";
							
							addDataPropertyAssertion(model, object.getURI(), RDFS.label.toString(), object.getName(), new String[]{null, null});
							addDataPropertyAssertion(model, object.getURI(), RDFS.comment.toString(), object.getDescription(), new String[]{null, null});

							addObjectPropertyAssertion(model, object.getURI(), Vocabulary.narra + Vocabulary.narraNames.is_identified_by.toString(), objectAppellationURI, 
									new String[]{Vocabulary.narra + Vocabulary.narraNames.Propositional_Object.toString(), null});
							addDataPropertyAssertion(model, objectAppellationURI, Vocabulary.narra + Vocabulary.narraNames.has_note.toString(), object.getName(), 
									new String[]{Vocabulary.narra + Vocabulary.narraNames.Appellation.toString(), XSDVocabulary.STRING.getIRI().toString()});
							addObjectPropertyAssertion(model, propositionURI, Vocabulary.narra+Vocabulary.narraNames.propPredicate.toString(), Vocabulary.narra + Vocabulary.narraNames.occurred_in_the_presence_of.toString(), 
									new String[]{Vocabulary.narra + Vocabulary.narraNames.Proposition.toString(), null});
							addObjectPropertyAssertion(model, propositionURI, Vocabulary.narra+Vocabulary.narraNames.propObject.toString(), object.getURI(), 
									new String[]{Vocabulary.narra + Vocabulary.narraNames.Proposition.toString(), Vocabulary.narra + Vocabulary.narraNames.Propositional_Object.toString()});
						
						
							addObjectPropertyAssertion(model, eventURI, Vocabulary.narra+Vocabulary.narraNames.hasEntity.toString(), object.getURI(), new String[]{null, null});
							addObjectPropertyAssertion(model, object.getURI(), Vocabulary.narra+Vocabulary.narraNames.isEntityOf.toString(), eventURI, new String[]{null, null});
							//System.out.println(eventURI);
							//System.out.println(object.getURI());
						
						}

						
						// add samAs wikidata foreach entity
						addObjectPropertyAssertion(model, wikiResource.getURI(), OWL.SAMEAS.toString() , "http://www.wikidata.org/entity/"+ wikiResource.getURI().substring(wikiResource.getURI().lastIndexOf("/") + 1)  , new String[]{null, null});
						
						ArrayList<Reference> referenceList = proposition.getReferenceList();

						try {
							for (int k = 0; k < referenceList.size(); k++) {								
								Reference reference = referenceList.get(k);

								Work source = reference.getSource();

								if (source != null) {
									String sourceURI = source.getURI();
									String sourceAuthorAppellationURI = source.getAuthor().getURI() + "/appellation";
									String sourceAppellationURI = sourceURI + "/appellation";
									String sourceCreationEventURI = sourceURI + "/creation";

									addObjectPropertyAssertion(model, source.getAuthor().getURI(), Vocabulary.narra + Vocabulary.narraNames.is_identified_by.toString(), 
											sourceAuthorAppellationURI, new String[]{Vocabulary.narra + Vocabulary.narraNames.Actor.toString(), Vocabulary.narra + Vocabulary.narraNames.Appellation.toString()});
									
									addDataPropertyAssertion(model, sourceAuthorAppellationURI, Vocabulary.narra + Vocabulary.narraNames.has_note.toString(),
											source.getAuthor().getName(), new String[]{Vocabulary.narra + Vocabulary.narraNames.Appellation.toString(), XSDVocabulary.STRING.getIRI().toString()});
									addObjectPropertyAssertion(model, sourceURI, Vocabulary.narra + Vocabulary.narraNames.is_identified_by.toString(), sourceAppellationURI, 
											new String[]{Vocabulary.narra + Vocabulary.narraNames.Information_Object.toString(), Vocabulary.narra + Vocabulary.narraNames.Appellation.toString()});
									
									addDataPropertyAssertion(model, sourceAppellationURI, Vocabulary.narra + Vocabulary.narraNames.has_note.toString(), 
											source.getTitle(), new String[]{Vocabulary.narra + Vocabulary.narraNames.Appellation.toString(), XSDVocabulary.STRING.getIRI().toString()});
									
									addObjectPropertyAssertion(model, sourceCreationEventURI, Vocabulary.narra + Vocabulary.narraNames.has_created.toString(), sourceURI, 
											new String[]{Vocabulary.narra + Vocabulary.narraNames.Event.toString(), Vocabulary.narra + Vocabulary.narraNames.Information_Object.toString()});
									
									addObjectPropertyAssertion(model, sourceCreationEventURI, Vocabulary.narra + Vocabulary.narraNames.carried_out_by.toString(), 
											source.getAuthor().getURI(), new String[]{Vocabulary.narra + Vocabulary.narraNames.Event.toString(), Vocabulary.narra + Vocabulary.narraNames.Actor.toString()});
									// If source is primary...
									if (source.getSourceType() == "primary") {
									log.debug(sourceURI);
									String observationURI = propositionURI + "/observation/" + (k + 1);				
									String observedPropositionSetURI = propositionURI + "/observed-proposition-set/" + (k + 1);
									log.debug(observationURI);
									log.debug(observedPropositionSetURI);
									
									String premiseBeliefURI = eventURI + "/premise-belief/" + (i + 1);
									String inferenceMakingURI = eventURI + "/inference-making/" + (i + 1);
									addObjectPropertyAssertion(model, inferenceMakingURI, Vocabulary.narra + Vocabulary.narraNames.concluded_that.toString(), premiseBeliefURI,
											new String[]{Vocabulary.narra + Vocabulary.narraNames.Inference_Making.toString(), null});
									addObjectPropertyAssertion(model, premiseBeliefURI, Vocabulary.narra + Vocabulary.narraNames.was_premise_for.toString(), inferenceMakingURI,
											new String[]{Vocabulary.narra + Vocabulary.narraNames.Belief.toString(), null});
									addObjectPropertyAssertion(model, resource_Narrator, Vocabulary.narra+Vocabulary.narraNames.holdsBelief.toString(), premiseBeliefURI,
											new String[]{Vocabulary.narra + Vocabulary.narraNames.Belief.toString(), null});

									addObjectPropertyAssertion(model, premiseBeliefURI, Vocabulary.narra + Vocabulary.narraNames.that.toString(), observedPropositionSetURI, 
											new String[]{null, Vocabulary.narra + Vocabulary.narraNames.Proposition_Set.toString()});
									
									addObjectPropertyAssertion(model, observationURI, Vocabulary.narra + Vocabulary.narraNames.observed_value.toString(), observedPropositionSetURI, 
											new String[]{Vocabulary.narra + Vocabulary.narraNames.Observation.toString(), null});
									
									addObjectPropertyAssertion(model, observationURI, Vocabulary.narra + Vocabulary.narraNames.observed_value.toString(), sourceURI, new String[]{null, null});

									addObjectPropertyAssertion(model, observationURI, Vocabulary.narra + Vocabulary.narraNames.carried_out_by.toString(), resource_Narrator, new String[]{null, null});

									addObjectPropertyAssertion(model, propositionURI, Vocabulary.narra+Vocabulary.narraNames.hasSource.toString(), sourceURI, new String[]{null, null});

									}
									
									String textFragment = reference.getTextFragment();
									String referenceFragment = reference.getReferenceFragment();
									String textFragmentURI = sourceURI + "/text-fragment/" + fragmentID;
									
									if (textFragment != null) {
										addDataPropertyAssertion(model, textFragmentURI, Vocabulary.narra + Vocabulary.narraNames.chars.toString(), textFragment, 
												new String[]{Vocabulary.narra + Vocabulary.narraNames.Symbolic_Object.toString(), XSDVocabulary.STRING.getIRI().toString()});
										log.debug(sourceURI + "/text/" + fragmentID);

										addObjectPropertyAssertion(model, textFragmentURI, RDF.TYPE.toString(), Vocabulary.narra + Vocabulary.narraNames.ContentAsText.toString(), 
												new String[]{Vocabulary.narra + Vocabulary.narraNames.Symbolic_Object.toString(), null});
										addObjectPropertyAssertion(model, textFragmentURI, RDF.TYPE.toString(), DCTypeText,
												new String[]{Vocabulary.narra + Vocabulary.narraNames.Symbolic_Object.toString(), null});
										addDataPropertyAssertion(model, textFragmentURI, DC.FORMAT.toString(), "text/plain",
												new String[]{Vocabulary.narra + Vocabulary.narraNames.Symbolic_Object.toString(), XSDVocabulary.STRING.getIRI().toString()});										
										addObjectPropertyAssertion(model, sourceURI, Vocabulary.narra + Vocabulary.narraNames.is_composed_of.toString(), textFragmentURI, new String[]{null, null});
										
										// If source is secondary...
										if (source.getSourceType() == "secondary") {
											
											// Add relation between text fragment and its corresponding proposition
											addObjectPropertyAssertion(model, textFragmentURI, Vocabulary.narra + Vocabulary.narraNames.isTextFragmentOf.toString(), propositionURI, new String[]{null, null});
										}
										
										// If source is primary...
										else if (source.getSourceType() == "primary") {
											
											// Add relation between primary source and text fragment
											addObjectPropertyAssertion(model, propositionURI, Vocabulary.narra+Vocabulary.narraNames.hasTextFragment.toString(), 
													textFragmentURI, new String[]{null, null});
										}
										// Increment fragment ID
										fragmentID++;
									}

									if (referenceFragment != null) {
										String referenceFragmentURI = sourceURI + "/reference-fragment/" + fragmentID;
										log.debug(sourceURI + "/reference-fragment/" + fragmentID);
										addObjectPropertyAssertion(model, referenceFragmentURI, RDF.TYPE.toString(), Vocabulary.narra + Vocabulary.narraNames.ContentAsText.toString(), 
												new String[]{Vocabulary.narra + Vocabulary.narraNames.Symbolic_Object.toString(), null});
										addObjectPropertyAssertion(model, referenceFragmentURI, RDF.TYPE.toString(), DCTypeText, new String[]{null, null});
										addDataPropertyAssertion(model, referenceFragmentURI, DC.FORMAT.toString(), "text/plain", new String[]{null, XSDVocabulary.STRING.getIRI().toString()});
										if (textFragment != null) {
											addObjectPropertyAssertion(model, textFragmentURI, Vocabulary.narra + Vocabulary.narraNames.hasReference.toString(), referenceFragmentURI, new String[]{null, null});
										}
										addDataPropertyAssertion(model, referenceFragmentURI, Vocabulary.narra + Vocabulary.narraNames.chars.toString(), referenceFragment, 
												new String[]{null, XSDVocabulary.STRING.getIRI().toString()});
										// Increment fragment ID
										fragmentID++;
									}								
								}

							}
						}
						// Catch exceptions in reference creation cycle
						catch (Exception e) {
							log.error("EXCEPTION - Creating references: " + e);
							log.error(getStackTrace(e));
						}
					}
					
					
					// create triple P129_is_about between country and narrative if narrative has a country
					if(narrative.getCountry() != null) {
						
						String idWikiNarrativeCountry= narrative.getCountry().get("id").getAsString().substring(narrative.getCountry().get("id").getAsString().lastIndexOf("/") + 1);
						
						Entity wikiResource = wikiResources.get(idWikiNarrativeCountry);
						Place place = (Place) wikiResource;
						String objectAppellationURI = place.getURI() + "/appellation";
						
						addDataPropertyAssertion(model, place.getURI(), RDFS.label.toString(), place.getName(), new String[]{null, null});
						addDataPropertyAssertion(model, place.getURI(), RDFS.comment.toString(), place.getDescription(), new String[]{null, null});

						addObjectPropertyAssertion(model, place.getURI(), Vocabulary.narra + Vocabulary.narraNames.is_identified_by.toString(), objectAppellationURI, 
								new String[]{Vocabulary.narra + Vocabulary.narraNames.Place.toString(), null});
						addDataPropertyAssertion(model, objectAppellationURI, Vocabulary.narra + Vocabulary.narraNames.has_note.toString(), place.getName(), 
								new String[]{Vocabulary.narra + Vocabulary.narraNames.Appellation.toString(), XSDVocabulary.STRING.getIRI().toString()});
						

						
						addObjectPropertyAssertion(model, resource_Narrative,Vocabulary.narra+Vocabulary.narraNames.isAboutCountry.toString(), wikiResource.getURI(),
								new String[]{null, null});	
						
						//NICO ADD sostituito Vocabulary.ecrm+Vocabulary.ecrmNames.P129_is_about.toString() con 
						// Vocabulary.narra+Vocabulary.narraNames.isAboutCountry.toString()
						
						//add sameAs wikidata to place
						addObjectPropertyAssertion(model, place.getURI(), OWL.SAMEAS.toString() , "http://www.wikidata.org/entity/"+ place.getURI().substring(place.getURI().lastIndexOf("/") + 1)  , new String[]{null, null});
						
						
						//System.out.println("Creata entità per la nazione della VC e collegata all'IRI della narrazione");
						//System.out.println("id nazione: " + narrative.getNationWiki().substring(narrative.getNationWiki().lastIndexOf("/") + 1));
						//System.out.println("nome: " + place.getName());
						
					}

					//NICO ADD START 
					if(!eventPolygon.isEmpty()) {
							String IRI_polygon = "";
							String IRI_polygon_geometry = "";
							if(LAUS.get(eventPolygon)!=null){
								IRI_polygon = Vocabulary.base + "place/" + LAUS.get(eventPolygon);
								IRI_polygon_geometry = Vocabulary.base + "geometry/" + LAUS.get(eventPolygon);
							} else{
								IRI_polygon = Vocabulary.base + "place/" + countIdLAU;
								IRI_polygon_geometry = Vocabulary.base + "geometry/" + countIdLAU;
								LAUS.put(eventPolygon, countIdLAU);
								countIdLAU++;
							}
						// add the triple E5 Event, P7_took_place_at, SP2 Phenomenal Place
						addObjectPropertyAssertion(model, eventURI, Vocabulary.narra + Vocabulary.narraNames.took_place_at.toString(),
						IRI_polygon, 
						new String[]{Vocabulary.narra + Vocabulary.narraNames.Event.toString(),Vocabulary.narra + Vocabulary.narraNames.Phenomenal_Place.toString()});

						// add the triple SP5_Geometric_Place_Expression, Q9_is_expressed_in_terms_of, SP4_Spatial_Coordinate_Reference_System
						addObjectPropertyAssertion(model, IRI_polygon_geometry, 
								Vocabulary.narra + Vocabulary.narraNames.is_expressed_in_terms_of.toString(),
								"http://www.opengis.net/def/crs/OGC/1.3/CRS84", 
								new String[]{Vocabulary.narra + Vocabulary.narraNames.Geometric_Place_Expression.toString(),Vocabulary.narra + Vocabulary.narraNames.Spatial_Coordinate_Reference_System.toString()});
						
						// add the triple SP5_Geometric_Place_Expression, asWKT, WktLiteral
						addDataPropertyAssertion(model, IRI_polygon_geometry, 
							Vocabulary.narra + Vocabulary.narraNames.asWKT.toString(), 
							"<http://www.opengis.net/def/crs/OGC/1.3/CRS84> "+ eventPolygon, 
							new String[]{Vocabulary.narra + Vocabulary.narraNames.Geometric_Place_Expression.toString(), "http://www.opengis.net/ont/geosparql#wktLiteral"});

						// add the triple SP5_Geometric_Place_Expression, asGML, gmlLiteral
						addDataPropertyAssertion(model, IRI_polygon_geometry, 
							Vocabulary.narra + Vocabulary.narraNames.asGML.toString(), 
							WKTToGML(eventPolygon), 
							new String[]{Vocabulary.narra + Vocabulary.narraNames.Geometric_Place_Expression.toString(), "http://www.opengis.net/ont/geosparql#gmlLiteral"});

						// add the triple SP4 Spatial Coordinate Reference System, Q7 describes, SP3 Reference Space
						addObjectPropertyAssertion(model, "http://www.opengis.net/def/crs/OGC/1.3/CRS84",  
							Vocabulary.narra + Vocabulary.narraNames.describes.toString(),
							Vocabulary.wikidata + "Q17295", 
							new String[]{Vocabulary.narra + Vocabulary.narraNames.Spatial_Coordinate_Reference_System.toString(),Vocabulary.narra + Vocabulary.narraNames.Reference_Space.toString()});
						
						// add the triple SP2 Phenomenal Place, has geometry, SP5_Geometric_Place_Expression
						addObjectPropertyAssertion(model, IRI_polygon, 
							Vocabulary.narra + Vocabulary.narraNames.hasGeometry.toString(),
							IRI_polygon_geometry,
							new String[]{Vocabulary.narra + Vocabulary.narraNames.Phenomenal_Place.toString(),Vocabulary.narra + Vocabulary.narraNames.Geometric_Place_Expression.toString()});

						// add the triple SP2 Phenomenal Place, has default geometry, SP5_Geometric_Place_Expression
						addObjectPropertyAssertion(model, IRI_polygon, 
							Vocabulary.narra + Vocabulary.narraNames.hasDefaultGeometry.toString(),
							IRI_polygon_geometry,
							new String[]{Vocabulary.narra + Vocabulary.narraNames.Phenomenal_Place.toString(),Vocabulary.narra + Vocabulary.narraNames.Geometric_Place_Expression.toString()});


					}
				

					
					if(narrative.getPlace() != null) {
						String IRI_LAUPlace = "";
						String IRI_LAUGeometry = "";
						if(LAUS.get(narrative.getPlace())!=null){
							IRI_LAUPlace = Vocabulary.base + "place/" + LAUS.get(narrative.getPlace());
							IRI_LAUGeometry = Vocabulary.base + "geometry/" + LAUS.get(narrative.getPlace());
						} else{
							IRI_LAUPlace = Vocabulary.base + "place/" + countIdLAU;
							IRI_LAUGeometry = Vocabulary.base + "geometry/" + countIdLAU;
							LAUS.put(narrative.getPlace(), countIdLAU);
							countIdLAU++;
						}
						

						addObjectPropertyAssertion(model, resource_Narrative,Vocabulary.narra+Vocabulary.narraNames.isAboutLAU.toString(), IRI_LAUPlace, new String[]{Vocabulary.narraNames.Narrative.toString(),  Vocabulary.narra + Vocabulary.narraNames.Phenomenal_Place.toString()});
						
						// add the triple SP5_Geometric_Place_Expression, Q9_is_expressed_in_terms_of, SP4_Spatial_Coordinate_Reference_System
						addObjectPropertyAssertion(model, IRI_LAUGeometry, 
								Vocabulary.narra + Vocabulary.narraNames.is_expressed_in_terms_of.toString(),
								"http://www.opengis.net/def/crs/OGC/1.3/CRS84", 
								new String[]{Vocabulary.narra + Vocabulary.narraNames.Geometric_Place_Expression.toString(),Vocabulary.narra + Vocabulary.narraNames.Spatial_Coordinate_Reference_System.toString()});
						
						// add the triple SP5_Geometric_Place_Expression, asWKT, WktLiteral
						addDataPropertyAssertion(model, IRI_LAUGeometry, 
							Vocabulary.narra + Vocabulary.narraNames.asWKT.toString(), 
							"<http://www.opengis.net/def/crs/OGC/1.3/CRS84> "+ narrative.getPlace(), 
							new String[]{Vocabulary.narra + Vocabulary.narraNames.Geometric_Place_Expression.toString(), "http://www.opengis.net/ont/geosparql#wktLiteral"});
						
						// add the triple SP5_Geometric_Place_Expression, asGML, gmlLiteral
						addDataPropertyAssertion(model, IRI_LAUGeometry, 
						Vocabulary.narra + Vocabulary.narraNames.asGML.toString(), 
						WKTToGML(narrative.getPlace()), 
						new String[]{Vocabulary.narra + Vocabulary.narraNames.Geometric_Place_Expression.toString(), "http://www.opengis.net/ont/geosparql#gmlLiteral"});
						
							
						// add the triple SP4 Spatial Coordinate Reference System, Q7 describes, SP3 Reference Space
						addObjectPropertyAssertion(model, "http://www.opengis.net/def/crs/OGC/1.3/CRS84",  
							Vocabulary.narra + Vocabulary.narraNames.describes.toString(),
							Vocabulary.wikidata + "Q17295", 
							new String[]{Vocabulary.narra + Vocabulary.narraNames.Spatial_Coordinate_Reference_System.toString(),Vocabulary.narra + Vocabulary.narraNames.Reference_Space.toString()});
						
						// add the triple SP2 Phenomenal Place, has geometry, SP5_Geometric_Place_Expression
						addObjectPropertyAssertion(model, IRI_LAUPlace, 
							Vocabulary.narra + Vocabulary.narraNames.hasGeometry.toString(),
							IRI_LAUGeometry,
							new String[]{Vocabulary.narra + Vocabulary.narraNames.Phenomenal_Place.toString(),Vocabulary.narra + Vocabulary.narraNames.Geometric_Place_Expression.toString()});

						// add the triple SP2 Phenomenal Place, has default geometry, SP5_Geometric_Place_Expression
						addObjectPropertyAssertion(model, IRI_LAUPlace, 
							Vocabulary.narra + Vocabulary.narraNames.hasDefaultGeometry.toString(),
							IRI_LAUGeometry,
							new String[]{Vocabulary.narra + Vocabulary.narraNames.Phenomenal_Place.toString(),Vocabulary.narra + Vocabulary.narraNames.Geometric_Place_Expression.toString()});

				

					}
					
					
				}
				// Catch exceptions in proposition creation cycle
				catch (Exception e) {
					log.error("EXCEPTION - Creating propositions: " + e);
					log.error(getStackTrace(e));
				}
			}
			
			// loop event IRIs to triplify the order
			for (int j = 0; j < iriEventsList.size() -1; j++) {
				
				//CAMBIARE PROPRIETA
				//System.out.println(iriEventsList.get(j));
				addObjectPropertyAssertion(model, iriEventsList.get(j), 
						Vocabulary.narra + Vocabulary.narraNames.isPresentedBefore.toString(), 
						iriEventsList.get(j+1), new String[]{null,null});					
			}
			
		}
		// Catch exception in event creation cycle
		catch (Exception e) {
			log.error("EXCEPTION - Creating events: " + e);
			log.error(getStackTrace(e));
		}
	}//end method
	
	public static String getStackTrace(final Throwable throwable) {
	     final StringWriter sw = new StringWriter();
	     final PrintWriter pw = new PrintWriter(sw, true);
	     throwable.printStackTrace(pw);
	     return sw.getBuffer().toString();
	}
	
	/**
	 * @throws Exception
	 */
	public void addObjectPropertyAssertion(OWLOntology model, String subjectIRI, String predicateIRI, String objectIRI, String[] type) throws Exception {
		OWLIndividual subject = dataFactory.getOWLNamedIndividual(IRI.create(subjectIRI));
		OWLIndividual object = dataFactory.getOWLNamedIndividual(IRI.create(objectIRI));
		if(type!=null){
			if(type[0]!=null&&!"".equals(type[0])){
				OWLClass entityType = dataFactory.getOWLClass(IRI.create(type[0]));
				OWLClassAssertionAxiom axiom = dataFactory.getOWLClassAssertionAxiom(entityType, subject);
				model.addAxiom(axiom);			
			}
			if(type[1]!=null&&!"".equals(type[1])){
				OWLClass entityType = dataFactory.getOWLClass(IRI.create(type[1]));
				OWLClassAssertionAxiom axiom = dataFactory.getOWLClassAssertionAxiom(entityType, object);
				model.addAxiom(axiom);			
			}
		}
		OWLObjectProperty predicate = dataFactory.getOWLObjectProperty(IRI.create(predicateIRI));
		// Now create the assertion (triple)
		OWLObjectPropertyAssertionAxiom assertion = dataFactory.getOWLObjectPropertyAssertionAxiom(predicate, subject, object);
		AddAxiom addAxiomChange = new AddAxiom(model, assertion);
		model.applyChange(addAxiomChange);
	}//end method
	/**
	 * @throws Exception
	 */
	public void addDataPropertyAssertion(OWLOntology model, String subjectIRI, String predicateIRI, String dataLiteral, String[] type) throws Exception {
		OWLIndividual subject = dataFactory.getOWLNamedIndividual(IRI.create(subjectIRI));
		OWLLiteral object = dataFactory.getOWLLiteral(dataLiteral);
		if(type!=null){
			if(type[0]!=null&&!"".equals(type[0])){
				OWLClass entityType = dataFactory.getOWLClass(IRI.create(type[0]));
				OWLClassAssertionAxiom axiom = dataFactory.getOWLClassAssertionAxiom(entityType, subject);
				model.addAxiom(axiom);			
			}
			if(type[1]!=null&&!"".equals(type[1])){
				OWLDatatype dataType = dataFactory.getOWLDatatype(IRI.create(type[1]));
				object = dataFactory.getOWLLiteral(dataLiteral, dataType);
			}
		}
		OWLDataProperty predicate = dataFactory.getOWLDataProperty(IRI.create(predicateIRI));
		// Now create the assertion (triple)
		OWLDataPropertyAssertionAxiom assertion = dataFactory.getOWLDataPropertyAssertionAxiom(predicate, subject, object);
		AddAxiom addAxiomChange = new AddAxiom(model, assertion);
		model.applyChange(addAxiomChange);
	}//end method
	/**
	 * @throws Exception
	 */
	public void addDataStringPropertyAssertions(OWLOntology model, String subjectIRI, String predicateIRI, String dataLiteral, String type) throws Exception {
		OWLIndividual subject = dataFactory.getOWLNamedIndividual(IRI.create(subjectIRI));
		if(type!=null){
			if(type!=null&&!"".equals(type)){
				OWLClass entityType = dataFactory.getOWLClass(IRI.create(type));
				OWLClassAssertionAxiom axiom = dataFactory.getOWLClassAssertionAxiom(entityType, subject);
				model.addAxiom(axiom);			
			}
		}
		OWLDataProperty predicate = dataFactory.getOWLDataProperty(IRI.create(predicateIRI));
		// Now create the assertion (triple)
		OWLDataPropertyAssertionAxiom assertion = dataFactory.getOWLDataPropertyAssertionAxiom(predicate, subject, dataLiteral);
		AddAxiom addAxiomChange = new AddAxiom(model, assertion);
		model.applyChange(addAxiomChange);
	}//end method	
	
}