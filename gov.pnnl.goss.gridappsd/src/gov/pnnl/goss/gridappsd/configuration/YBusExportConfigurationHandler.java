/*******************************************************************************
 * Copyright  2017, Battelle Memorial Institute All rights reserved.
 * Battelle Memorial Institute (hereinafter Battelle) hereby grants permission to any person or entity 
 * lawfully obtaining a copy of this software and associated documentation files (hereinafter the 
 * Software) to redistribute and use the Software in source and binary forms, with or without modification. 
 * Such person or entity may use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of 
 * the Software, and may permit others to do so, subject to the following conditions:
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the 
 * following disclaimers.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and 
 * the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Other than as used herein, neither the name Battelle Memorial Institute or Battelle may be used in any 
 * form whatsoever without the express written consent of Battelle.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * BATTELLE OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED 
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * General disclaimer for use with OSS licenses
 * 
 * This material was prepared as an account of work sponsored by an agency of the United States Government. 
 * Neither the United States Government nor the United States Department of Energy, nor Battelle, nor any 
 * of their employees, nor any jurisdiction or organization that has cooperated in the development of these 
 * materials, makes any warranty, express or implied, or assumes any legal liability or responsibility for 
 * the accuracy, completeness, or usefulness or any information, apparatus, product, software, or process 
 * disclosed, or represents that its use would not infringe privately owned rights.
 * 
 * Reference herein to any specific commercial product, process, or service by trade name, trademark, manufacturer, 
 * or otherwise does not necessarily constitute or imply its endorsement, recommendation, or favoring by the United 
 * States Government or any agency thereof, or Battelle Memorial Institute. The views and opinions of authors expressed 
 * herein do not necessarily state or reflect those of the United States Government or any agency thereof.
 * 
 * PACIFIC NORTHWEST NATIONAL LABORATORY operated by BATTELLE for the 
 * UNITED STATES DEPARTMENT OF ENERGY under Contract DE-AC05-76RL01830
 ******************************************************************************/ 
package gov.pnnl.goss.gridappsd.configuration;

import gov.pnnl.goss.cim2glm.CIMImporter;
import gov.pnnl.goss.cim2glm.queryhandler.QueryHandler;
import gov.pnnl.goss.gridappsd.api.ConfigurationHandler;
import gov.pnnl.goss.gridappsd.api.ConfigurationManager;
import gov.pnnl.goss.gridappsd.api.DataManager;
import gov.pnnl.goss.gridappsd.api.LogManager;
import gov.pnnl.goss.gridappsd.api.SimulationManager;
import gov.pnnl.goss.gridappsd.data.handlers.BlazegraphQueryHandler;
import gov.pnnl.goss.gridappsd.dto.LogMessage;
import gov.pnnl.goss.gridappsd.dto.LogMessage.LogLevel;
import gov.pnnl.goss.gridappsd.dto.LogMessage.ProcessStatus;
import gov.pnnl.goss.gridappsd.dto.RequestSimulation;
import gov.pnnl.goss.gridappsd.dto.SimulationContext;
import gov.pnnl.goss.gridappsd.utils.GridAppsDConstants;

import java.io.PrintWriter;
import java.util.Date;
import java.util.Properties;

import org.apache.felix.dm.annotation.api.Component;
import org.apache.felix.dm.annotation.api.ServiceDependency;
import org.apache.felix.dm.annotation.api.Start;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pnnl.goss.core.Client;


@Component
public class YBusExportConfigurationHandler implements ConfigurationHandler {//implements ConfigurationManager{

	private static Logger log = LoggerFactory.getLogger(YBusExportConfigurationHandler.class);
	Client client = null; 
	
	@ServiceDependency
	private volatile ConfigurationManager configManager;
	
	@ServiceDependency
	private volatile SimulationManager simulationManager;
	
	@ServiceDependency 
	private volatile LogManager logManager;
	
	public static final String TYPENAME = "YBus Export";
	public static final String ZFRACTION = "z_fraction";
	public static final String IFRACTION = "i_fraction";
	public static final String PFRACTION = "p_fraction";
	public static final String SCHEDULENAME = "schedule_name";
	public static final String LOADSCALINGFACTOR = "load_scaling_factor";
	public static final String MODELID = "model_id";
	public static final String BUSCOORDS = "buscoords";
	public static final String GUIDS = "guids";
	
	public YBusExportConfigurationHandler() {
	}
	 
	public YBusExportConfigurationHandler(LogManager logManager, DataManager dataManager) {

	}
	
	
	@Start
	public void start(){
		if(configManager!=null) {
			configManager.registerConfigurationHandler(TYPENAME, this);
		}
		else { 
			//TODO send log message and exception
			log.warn("No Config manager avilable for "+getClass());
		}
	}

	@Override
	public void generateConfig(Properties parameters, PrintWriter out, String simulationId, String username) throws Exception {
		
		logManager.log(new LogMessage(this.getClass().getSimpleName(), 
				simulationId, new Date().getTime(), 
				"Generating Y Bus matrix for simulation Id : "+simulationId, 
				LogLevel.DEBUG, 
				ProcessStatus.RUNNING, 
				true), username, GridAppsDConstants.topic_platformLog);
		
		
		
		SimulationContext simcontext = simulationManager.getSimulationContextForId(simulationId);
		
		simcontext.getRequest().getSimulation_config().getModel_creation_config().
		
		
		
		
		
		String bgHost = configManager.getConfigurationProperty(GridAppsDConstants.BLAZEGRAPH_HOST_PATH);
		if(bgHost==null || bgHost.trim().length()==0){
			bgHost = BlazegraphQueryHandler.DEFAULT_ENDPOINT; 
		}
		
		String buscoords = GridAppsDConstants.getStringProperty(parameters, BUSCOORDS, null);
		if(buscoords==null || buscoords.trim().length()==0){
			//TODO need to figure out the correct default
		}
		String guids = GridAppsDConstants.getStringProperty(parameters, GUIDS, null);
		if(guids==null || guids.trim().length()==0){
			//TODO need to figure out the correct default
		}
		
		//TODO write a query handler that uses the built in powergrid model data manager that talks to blazegraph internally
		QueryHandler queryHandler = new BlazegraphQueryHandler(bgHost);
		queryHandler.addFeederSelection(modelId);
		
		CIMImporter cimImporter = new CIMImporter(); 
		PrintWriter outID = new PrintWriter("outid");
		
		cimImporter.generateDSSFile(queryHandler, out, outID, buscoords, guids, loadScale, bWantZip, zFraction, iFraction, pFraction);
		logRunning("Finished generating DSS Base configuration file.", processId, "", logManager);

	}
	
	
	
	
}
