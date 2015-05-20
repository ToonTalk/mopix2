/**
 * 
 */
package uk.ac.lkl.server;

import uk.ac.lkl.client.rpc.MoPiXResourcePageService;
import uk.ac.lkl.server.persistent.ModelXML;

/**
 * @author Ken Kahn
 *
 */
public class MoPiXResourcePageServiceImpl extends ResourcePageServiceImpl 
	implements MoPiXResourcePageService {

    @Override
    protected String getResourceArchiveFileName() {
	return "mopix_static_resources_v2.zip";
    }

    @Override
    public String[] saveXML(String xml, String sessionGuid, String userGuid) {
	String result[] = new String[2];
	// 0 -- warnings or errors or null
	// 1 -- guid for loading the model
	String modelGuid = ServerUtils.generateGUIDString();
	ServerUtils.persist(new ModelXML(xml, sessionGuid, modelGuid));
	result[1] = modelGuid;
	return result;
    }
    
}


