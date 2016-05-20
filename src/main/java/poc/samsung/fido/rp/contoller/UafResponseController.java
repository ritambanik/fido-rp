package poc.samsung.fido.rp.contoller;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.samsung.sds.fido.uaf.message.transport.context.RpContext;
import com.samsung.sds.fido.uaf.server.sdk.http.HttpResponse;
import com.samsung.sds.fido.uaf.server.sdk.operation.OperationType;
import com.samsung.sds.fido.uaf.server.sdk.operation.UafOperations;
import com.samsung.sds.fido.uaf.server.sdk.operation.UafResponse;
import com.samsung.sds.fido.uaf.server.sdk.operation.UafResponseResult;

import poc.samsung.fido.rp.configuration.Configurations;
import poc.samsung.fido.rp.util.ApiConstants;
import poc.samsung.fido.rp.util.ApiRequest;
import poc.samsung.fido.rp.util.Utils;


@Controller
public class UafResponseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UafResponseController.class);

    @Inject
    @Named("configurations")
    private Configurations configuration;
    

    @RequestMapping(value = "/{rpId}/uaf/response", method = RequestMethod.POST)
    @ResponseBody
    public String postUafResponse(@PathVariable String rpId, @RequestBody String requestBody, HttpServletRequest request, HttpServletResponse servletResponse) {
        LOGGER.trace("action=postUafResponse, rp=\"{}\"", rpId);

        try {
        	LOGGER.trace("rpId = {}", rpId);
            String apiKey = configuration.getApiKey(rpId);
            if (apiKey == null) {
                LOGGER.trace("action=postUafResponse, In valid RP = {}", rpId);

                servletResponse.setStatus(ApiConstants.HTTP_CODE_BAD_REQUEST);
                return "Invalid RP ID";
            }
            Utils.logRequest(LOGGER, request, requestBody, rpId, apiKey);
            String baseUri = configuration.getApiUri(rpId);
            UafResponse uafResponse = UafOperations.newUafResponse(
                    new ApiRequest(request, requestBody), baseUri, apiKey);
            if (null == uafResponse) {
                LOGGER.error("action=postUafResponse, UafOperations.newUafRequest is null");
                return Utils.getResult(UafOperations.BAD_REQUEST_RESPONSE, servletResponse);
            }
            RpContext context = null;
            String contextJson = uafResponse.getContext();
            if (null != contextJson) {
                try {
                    context = RpContext.fromJson(contextJson);
                } catch (IllegalArgumentException | IllegalStateException ex) {
                    LOGGER.error("action=postUafResponse, RpContext.fromJson exception");
                    return Utils.getResult(UafOperations.BAD_REQUEST_RESPONSE, servletResponse);
                }
            } else {
                context = RpContext.newBuilder().build();
            }
            OperationType op = uafResponse.getOperationType();
            HttpResponse response;
            try {
                switch (op) {
                    case REGISTRATION:
                        response = processRegistrationResponse(uafResponse, context);
                        break;
                    case AUTHENTICATION:
                    	LOGGER.error("Not yet supported");
                        throw new UnsupportedOperationException(op + " is not yet supported");
                    default:
                        LOGGER.error("action=postUafResponse, Unsupported operation type: {}", op);

                        return Utils.getResult(UafOperations.BAD_REQUEST_RESPONSE, servletResponse);
                }
            } catch (ConnectException ex) {
                // RP can optionally execute a new UafRequest with a fail-over baseUri
                return Utils.getResult(UafOperations.HTTP_INTERNAL_SERVER_ERROR, servletResponse);
            }
            String result = Utils.getResult(response, servletResponse);
            servletResponse.setContentType(response.getContentType());
            List<Object> mList = new ArrayList<Object>();
            mList.add(uafResponse.getOperationType());
            mList.add(response.getBody());
            mList.add(result);
            LOGGER.trace(
            		"action=postUafRequest, operation=\"{}\", response=\"{}\", return=\"{}\"",
            		mList.toArray());
            return result;
        } catch (Exception ex) {
            LOGGER.error("action=postUafResponse, Exception thrown.", ex);

            servletResponse.setStatus(ApiConstants.HTTP_CODE_INTERNAL_SERVER_ERROR);
            return ApiConstants.EMPTY_BODY;
        }
    }

    private HttpResponse processRegistrationResponse(UafResponse uafResponse,
            RpContext context) throws ConnectException {
        UafResponseResult result = uafResponse.execute();
        if (result.isSuccess()) {
//            String sessionId = result.getServerData();
//            RpContext cachedContext = Utils.getContext(sessionId);
//
//            // TODO: handle expired context case
//
//            // TODO: persist tuples of userId, deviceId, and registrationIds
//            String deviceId = cachedContext.getDeviceId();
//            if (null != deviceId && !deviceId.isEmpty()) {
//                String userId = result.getUserId();
//                List<String> regIds = result.getRegistrationIds();
//
//                String searchKey = Utils.getSearchKey(userId, deviceId);
//                Utils.putDeviceInfo(searchKey, regIds);
//            }
        }
        return result.getResponse();
    }

}