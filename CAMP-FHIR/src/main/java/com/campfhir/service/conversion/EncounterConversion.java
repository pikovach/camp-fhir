package com.campfhir.service.conversion;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Encounter.DiagnosisComponent;
import org.hl7.fhir.dstu3.model.Encounter.EncounterHospitalizationComponent;
import org.hl7.fhir.dstu3.model.Encounter.EncounterParticipantComponent;
import org.hl7.fhir.exceptions.FHIRException;

import com.campfhir.model.Encounter;

/**
*
* @author  James Champion
* @version 1.0
* @since   2019-02-08 
*/
public class EncounterConversion 
{
	public Bundle Encounters(List<Encounter> encounters) throws ParseException, FHIRException, IOException
	{		
		Bundle bundle = new Bundle();

		for (Encounter encounter : encounters) 
		{	
	
			org.hl7.fhir.dstu3.model.Encounter e = new org.hl7.fhir.dstu3.model.Encounter();	
			
			/******************** PNT_RACE **************************************************************************************
			 * ENC_IDENTIFIER maps to Encounter / identifier
			 ********************************************************************************************************************/
			e.setIdBase(encounter.getENC_IDENTIFIER());
			
			/******************** ENC_SUBJECT_REFERENCE *************************************************************************
			 * ENC_SUBJECT_REFERENCE maps to Encounter / subject / reference
			 ********************************************************************************************************************/
			Reference reference = new Reference().setReference(encounter.getENC_SUBJECT_REFERENCE());
			e.setSubject(reference);
			
			/******************** ENC_PARTICIPANT_INDIV_REF *********************************************************************
			 * ENC_PARTICIPANT_INDIV_REF maps to Encountner / participant / individual / reference
			 ********************************************************************************************************************/
			EncounterParticipantComponent epc = new EncounterParticipantComponent();
			Reference i = new Reference().setDisplay(encounter.getENC_PARTICIPANT_INDIV_REF());;
			epc.setIndividual(i);
			List<EncounterParticipantComponent> participant = new ArrayList<EncounterParticipantComponent>();
			participant.add(epc);
			e.setParticipant(participant);
			
			
			if(encounter.getENC_CLASS_CODE() != null)
			{
				Coding cl = new Coding();
				/******************** ENC_CLASS_CODE ****************************************************************************
				 * ENC_CLASS_CODE maps to Encounter / class / coding / code
				 ****************************************************************************************************************/
				cl.setCode(encounter.getENC_CLASS_CODE());
				
				/******************** ENC_CLASS_DISPLAY *************************************************************************
				 * ENC_CLASS_DISPLAY maps to Encounter / class / coding / display
				 ****************************************************************************************************************/
				cl.setDisplay(encounter.getENC_CLASS_DISPLAY());
				
				/******************** ENC_CLASS_SYST ****************************************************************************
				 * ENC_CLASS_SYST maps to Encounter / class / coding / system
				 ****************************************************************************************************************/
				cl.setSystem(encounter.getENC_CLASS_SYST());
				e.setClass_(cl);
			}
			
			Period period = new Period();			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
			
			/******************** ENC_PERIOD_START ******************************************************************************
			 * ENC_PERIOD_START maps to Encounter / period / start
			 ********************************************************************************************************************/
			if(encounter.getENC_PERIOD_START() != null)
			{
				Date date1;
				try 
				{
					date1 = sdf.parse(encounter.getENC_PERIOD_START());
					period.setStart(date1);
				} 
				catch (ParseException e1) 
				{
					e1.printStackTrace();
				}	
			}

			/******************** ENC_PERIOD_END ********************************************************************************
			 * ENC_PERIOD_END maps to Encounter / period / end
			 ********************************************************************************************************************/
			if(encounter.getENC_PERIOD_END() != null)
			{
				Date date2;
				try 
				{
					date2 = sdf.parse(encounter.getENC_PERIOD_END());
					period.setEnd(date2);
				} 
				catch (ParseException e1) 
				{
					e1.printStackTrace();
				}
			}
			
			try 
			{
				e.setHospitalization(setDischargeDisp(encounter));
			} 
			catch (FHIRException e1) 
			{
				e1.printStackTrace();
			}


			e.setPeriod(period);
			
			List<DiagnosisComponent> diagnosis = new ArrayList<DiagnosisComponent>();
			DiagnosisComponent dc = new DiagnosisComponent();
			
			CodeableConcept cc = new CodeableConcept();
			Coding cd = new Coding();
			
			/******************** ENC_DIAGN_ROLE_CODING_CODE ********************************************************************
			 *  ENC_DIAGN_ROLE_CODING_CODE maps to Encounter / diagnosis / role / coding / code
			 ********************************************************************************************************************/
			cd.setCode(encounter.getENC_DIAGN_ROLE_CODING_CODE());
			
			/******************** ENC_DIAGN_ROLE_SYST ***************************************************************************
			 *  ENC_DIAGN_ROLE_SYST maps to Encounter / diagnosis / role / coding / system
			 ********************************************************************************************************************/
			cd.setSystem(encounter.getENC_DIAGN_ROLE_SYST());
			cc.addCoding(cd);
			dc.setRole(cc);
			
			/******************** ENC_DIAGN_CON_REF *****************************************************************************
			 *  ENC_DIAGN_CON_REF maps to Encounter / diagnosis / condition / reference
			 ********************************************************************************************************************/
			Reference ref = new Reference();
			dc.setCondition(ref.setReference(encounter.getENC_DIAGN_CON_REF()));
			
			/******************** ENC_DIAGN_RANK ********************************************************************************
			 *  ENC_DIAGN_RANK  maps to Encounter / diagnosis / rank
			 ********************************************************************************************************************/
			if(encounter.getENC_DIAGN_RANK() != null)
			{
				dc.setRank(Integer.parseInt(encounter.getENC_DIAGN_RANK()));
				e.setDiagnosis(diagnosis);
			}
						
			bundle.addEntry()
			   .setFullUrl("https://www.hl7.org/fhir/encounter.html")
			   .setResource(e);
		}
		
		return bundle;
	}
	
	public static  EncounterHospitalizationComponent setDischargeDisp(Encounter encounter) throws FHIRException
	{
		EncounterHospitalizationComponent dd = new EncounterHospitalizationComponent();

		CodeableConcept value = new CodeableConcept();
		CodeableConcept as = new CodeableConcept();
		Coding p = new Coding();
		Coding asp = new Coding();

		
		/******************** ENC_HOSP_DISDISP_CODING_SYST ******************************************************************
		 * ENC_HOSP_DISDISP_CODING_SYST maps to Encounter / hospitalization / dischargeDisposition / coding / system
		 ********************************************************************************************************************/
		if(encounter.getENC_HOSP_DISDISP_CODING_SYST() != null)
		{
			p.setSystem(encounter.getENC_HOSP_DISDISP_CODING_SYST());
		}
		

		/******************** ENC_HOSP_DISDISP_CODING_CODE ******************************************************************
		 * ENC_HOSP_DISDISP_CODING_CODE maps to Encounter / hospitalization / dischargeDisposition / coding / code
		 ********************************************************************************************************************/
		if(encounter.getENC_HOSP_DISDISP_CODING_CODE() != null)
		{
			p.setCode(encounter.getENC_HOSP_DISDISP_CODING_CODE());
		}
		
		/******************** ENC_HOSP_DISDISP_TEXT *************************************************************************
		 * ENC_HOSP_DISDISP_TEXT maps to Encounter / hospitalization / dischargeDisposition / text
		 * ******************************************************************************************************************/
		if(encounter.getENC_HOSP_DISDISP_TEXT() != null)
		{
			p.setDisplay(encounter.getENC_HOSP_DISDISP_TEXT());
			value.addCoding(p);
		}
		
		/******************** ENC_HOSP_ADMSRC_CODING_CODE ********************************************************************
		 * ENC_HOSP_ADMSRC_CODING_CODE maps to Encounter / hospitalization / admitSource / coding / code
		 *********************************************************************************************************************/
		if(encounter.getENC_HOSP_ADMSRC_CODING_CODE() != null)
		{
			p.setCode(encounter.getENC_HOSP_ADMSRC_CODING_CODE());
			as.addCoding(asp);
		}
		
		/******************** ENC_HOSP_ADMSRC_TEXT ***************************************************************************
		 * ENC_HOSP_ADMSRC_TEXT maps to Encounter / hospitalization / admitSource / text
		 *********************************************************************************************************************/
		if(encounter.getENC_HOSP_ADMSRC_TEXT() != null)
		{
			as.setText(encounter.getENC_HOSP_ADMSRC_TEXT());
			dd.setAdmitSource(as);
		}
		
		dd.setDischargeDisposition(value);
		
		return dd;	
	}
}
