--> Developer install readme for SOA Fusion Order Demo on Weblogic 10.3.4 / SOA Suite 11.1.1.4 <--

Mandatory build infrastructure:
- JDeveloper 11g + SOA 
- Ant 1.7
- JDK 6

--------- Getting started, Using FOD from inside JDeveloper (e.g. deploy via the project / app dialogs) ---------

1) Extract the OracleSOAFusionOrderDemo_Source_2.3_ps3.jar file containing the source into a directory
2) Open jdeveloper and choose "open application", now navigate to the directory where you extracted the source and open the CompositeServices\WebLogicFusionOrderDemo.jws workspace
3) Modify the build.properties in the bin project to reflect your environment (e.g. oracle.home / as well as other server side properties) as well as the bits you intend to deploy (e.g. using soa-only or adf, as well as using B2B composite) 
4) Run Ant, target "setupWorkspaceForJdeveloperUse" from within the bin project. 

This target modifies
 a) connections.xml - based on the setting of build.properties (adf/soa deployment)
 b) adf-config.xml - pointing either to a remote or local mds and (foreign.mds.connection)
 c) the deployment plan for the OrderBookingComposite
 d) the deployment plan for the B2BX12OrderGateway
 e) imports the common artifacts based on the newly created adf-config.xml from (b)

The deployment plan in the orderbooking project will replace the endpoints for 
  a) PartnerSupplierComposite
  b) CreditCardAuthorization webservice and
  c) ADFBC based StoreFrontService (or in the standalone case the OrderSDOComposite) during deployment.

Hence make sure that the plan ($orderbooking/bin/orderbooking_deployment_plan.xml) is attached when deploying

The deployment plan in the b2b project will replace the endpoints for 

  a) ADFBC based StoreFrontService (or in the standalone case the OrderSDOComposite) during deployment.
  b) BamOrderBookingComposite

Hence make sure that the plan ($b2b/bin/B2BX12OrderGateway_deployment_plan.xml) is attached when deploying

If you want to have the bam orderbooking seeding dataobjects you need to amend BamOrderBookingComposite/bin/sca-build.properties -
and run the corresponding ant tasks from $FOD_HOME/OrderBookingComposite/bin

======================================================================================================================
===========>  Ant based compilation / build and deployment, in/outside of Oracle JDeveloper <=========================

$FOD_HOME/bin/build.properties contains the master set of properties that need to be adjusted to your environment.
 a) Change oracle.home to point to your Oracle Home. E.g. c:\\beahome1\\jdeveloper or when on the server e.g. /beahome/AS11gR1SOA
 b) Change the server properties. They will be used for scripting and deployment (admin/managed.server.host/port as well as user / password and targets)
 c) change the mds section to reflect your environment. In case JDeveloper where you only want to work locally, leave the settings as is, otherwise amend the db settings section and amend the mds type to "db". Before you deploy - make sure you import the common shared artifacts though.
 d) in case you want to deploy either bam or b2b - enable those settings.

In $FOD_HOME/bin/build.xml the following targets can be used to deploy the demo 

validateFodConfigSettings --> validates the build.properties based configuration for server settings as well as mds

createMDSConnections --> based on mds settings in build.properties seeds the correct adf-config.xml

importCommonServiceArtifactsIntoMds --> imports the shared resources into the target MDS - based on the applications' adf-config.xml. The mds settings should be set to the RUNTIME mds db instance.

compile-build-all --> compiles and builds all soa components.
   after successfull completion, they can be found in $Project/deploy

compile-deploy-all --> compiles, builds and deploys ALL components to the defined server(s) in build.properties.

seedFodJmsResources --> seeds the JMS resources needed for the Fulfillment mediator

seedFodFileAdapterResources --> seeds the File adapter connection factory needed for the Fulfillment mediator

setupWorkspaceForJdeveloperUse --> setup the workspace to be used with jdeveloper based deplopment

server-setup-seed-deploy-test --> setup the application, compile its artifacts, deploy them and run the composite tests on the server

server-cleanup-all --> undeploy all artifacts from the server

test-fod-composite-end2end --> runs various tests against the (bam)orderbooking composite and against the SDO one, using SDO api*s as well as APIs

build-ws-client --> creates the ws client proxy for testing

-----------------------------
> Important side information:

1) For the human task details project (OrderApprovalHumanTask): Depending on if you chose soa.only.deployment in the global build.properties, SOA or ADF backed service will be configured

2) For the JMS Adapter in the FulFillment mediator you need to create the following JMS entries via the weblogic console

	DemoSupplierTopic	Topic              	jms/DemoSupplierTopicUdd	SOASubDeployment	SOAJMSServer
	DemoSupplierTopicCF	Connection Factory	jms/DemoSupplierTopicCF	        SOASubDeployment	SOAJMSServer

   Also create a new connection factory in the jms adapter deployment

	JNDI Name: eis/Jms/TopicConnectionFactory

	AcknowledgeMode	                java.lang.String	AUTO_ACKNOWLEDGE
	ConnectionFactoryLocation	java.lang.String	jms/DemoSupplierTopicCF
	FactoryProperties	        java.lang.String	
	IsTopic	                        java.lang.Boolean	true
	IsTransacted	                java.lang.Boolean	false
	Password	                java.lang.String	weblogic
	Username	                java.lang.String	<weblogic password>

   Alternatively the $FOD_HOME/bin/common-sca-tools.xml contains a target named "seedFodJmsResources" which creates those. 

2b) For the FilleAdapter in the FulFillment mediator you need to create the following File adapter connection factory via the weblogic console
	
	JNDI Name: eis/file/FODFileAdapter

	controlDir	java.lang.String	/tmp/control <which can be set in bin/build.properties>
	inboundDataSource	java.lang.String	jdbc/SOADataSource
	outboundDataSource	java.lang.String	jdbc/SOADataSource
	outboundDataSourceLocal	java.lang.String	jdbc/SOALocalTxDataSource
	outboundLockTypeForWrite	java.lang.String	oracle

   Alternatively the $FOD_HOME/bin/common-sca-tools.xml contains a target named "seedFodFileAdapterResources" which creates those. 

3) For the file adapter, part of the FulFillment mediator, make sure that the  orderbooking.file.adapter.dir property in the main build properties points to a writable directory on.the server. Same for the rderbooking.file.adapter.control.dir which is used for control files during concurrent write (e.g. of multiple nodes)

4) Make sure BPM user "jstein" is seeded - as the task for order approval is assigned to him. If not the process will generate a recoverable fault and the task is assigned to weblogic user. The (re)assigned task can be found under "Adminstrative tasks" in the worklist. 

   The demo users can be seeded through $FOD/bin/build.xml's "seedDemoUsers" target  

5) In case you chose to use bam behind the orderbooking composite - make sure that sca-build.properties in BamOrderBookingComposite/bin point to your BAM server. To seed the dataobjects use the "seedBAMServerObjects" target. To change the eis/bam/rmi connection factory of the Bam adapter use the "seedBAMAdapterResources" target. Both is done if the demo is installed through "server-setup-seed-deploy-test" and use.bam is set to true

6) In case you chose to deploy the B2BX12OrderGateway - import and deploy the agreements in B2BX12OrderGateway/config to the B2B Server and enable the inbound listener channel. Alternatively the "importAndDeployB2BTradingAgreements" target in B2BX12OrderGateway/bin/build_sca_composite.xml will be called during demo installation.

Make sure that the inbound listener channel points to a usable directory, and create the 
orclsrvs_in directory in it. Also install the xEngine in case it was not seeded - this can be done by extracting the binaries into
$OH/soa/thirdparty/edifecs

7) Inside of the B2BX12OrderGateway's/bin/sca-build.properties set use.events to true, if the communication between the b2b composite and the orderbooking one should be based on events rather than the conventional push invocation method (via binding)

-----------------------
Latest certified labels (this means the code is CERTIFIED WITH THIS LABELSET *ONLY*!):

   PCBPEL_11.1.1.4.0_GENERIC_100814.1800.5164 (server/plugin) + corresponding jdeveloper

-----------------------
Open bugs:
