package uk.ac.lkl.server;

import javax.servlet.ServletRequest;

public class MoPiXHistoryServiceImpl extends HistoryServiceImpl {
    
    @Override
    protected String getHistoryFromServer(String sessionGuid, String userGuid, ServletRequest servletRequest) {
	return null;
    }

}
