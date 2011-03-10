package org.talend.esb.sam.agent.interceptor.transport;

import java.util.logging.Logger;

import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.service.model.EndpointInfo;
import org.talend.esb.sam.agent.interceptor.FlowIdProcessor;
import org.talend.esb.sam.agent.interceptor.MessageContextCodec;


public class FlowIdTransportCodec extends AbstractPhaseInterceptor<Message> {
	
	protected static Logger logger = Logger.getLogger(FlowIdTransportCodec.class.getName());
	
	private FlowIdTransportCodec() {
		super(Phase.PRE_PROTOCOL);
	}
	
	public FlowIdTransportCodec(String phase) {
		super(phase);
		//super(Phase.USER_PROTOCOL); //outgoing
		//super(Phase.READ); //incoming
	}
	
	
	public void handleMessage(Message message) throws Fault {
		logger.finest("FlowIdTransportCodec Interceptor called. isOutbound: " + MessageUtils.isOutbound(message) + ", isRequestor: " + MessageUtils.isRequestor(message));
		
		MessageContextCodec codec = null;
		Exchange ex = message.getExchange();
		Endpoint endpoint = ex.get(Endpoint.class);
		EndpointInfo ei = endpoint.getEndpointInfo();
		
		String uri = ei.getAddress();
		if (uri != null && ( uri.contains("http://") || uri.contains("https://") )) {
			logger.fine("Http transport found");
			codec = new HttpMessageContextCodec();
		}
		//else if (uriPrefixes != null && uriPrefixes.contains("jms://")) {
			
		//}
		else {
			logger.warning("Transport '" + uri + "' not supported");
			return;
		}
			
		FlowIdProcessor processor = new FlowIdProcessor(codec);
		processor.processMessage(message);
	}

	
}