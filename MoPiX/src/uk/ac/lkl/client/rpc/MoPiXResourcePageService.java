package uk.ac.lkl.client.rpc;

public interface MoPiXResourcePageService extends ResourcePageService {
    
    public String[] saveXML(String xml, String sessionGuid, String userGuid);

}
