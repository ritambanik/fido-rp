package poc.samsung.fido.rp.contoller;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.samsung.sds.fido.uaf.message.transport.context.RpContext;
import com.samsung.sds.fido.uaf.server.sdk.http.HttpResponse;
import com.samsung.sds.fido.uaf.server.sdk.operation.OperationType;
import com.samsung.sds.fido.uaf.server.sdk.operation.UafOperations;
import com.samsung.sds.fido.uaf.server.sdk.operation.UafRequest;
import com.samsung.sds.fido.uaf.server.sdk.operation.UafRequestResult;

import poc.samsung.fido.rp.configuration.Configurations;
import poc.samsung.fido.rp.domain.Customer;
import poc.samsung.fido.rp.repositories.CustomerRepository;
import poc.samsung.fido.rp.repositories.CustomerRepositoryCustom;
import poc.samsung.fido.rp.util.ApiConstants;
import poc.samsung.fido.rp.util.ApiRequest;
import poc.samsung.fido.rp.util.Utils;

@Controller
public class UafRequestController {


    private static final Logger LOGGER = LoggerFactory.getLogger(UafRequestController.class);

    private static final int HTTP_PAYMENT_REQUIRED = 402;
    
    @Autowired
    private CustomerRepositoryCustom customerRepository;

    @Inject
    @Named("configurations")
    private Configurations configuration;

    @RequestMapping(value = "/{rpId}/uaf/request", method = RequestMethod.POST)
    @ResponseBody
    public String postUafRequest(@PathVariable String rpId, @RequestBody String requestBody,
            HttpServletRequest request, HttpServletResponse servletResponse) {
        LOGGER.trace("action=postUafRequest, rp=\"{}\"", rpId);
        try {
            LOGGER.trace("rpId = {}", rpId);
            String apiKey = configuration.getApiKey(rpId);
            if (apiKey == null) {
                LOGGER.trace("action=postUafRequest, In valid RP = {}", rpId);
                servletResponse.setStatus(ApiConstants.HTTP_CODE_BAD_REQUEST);
                return "Invalid RP ID";
            }
            Utils.logRequest(LOGGER, request, requestBody, rpId, apiKey);
            String baseUri = configuration.getApiUri(rpId);
            UafRequest uafRequest = UafOperations
                    .newUafRequest(new ApiRequest(request, requestBody), baseUri, apiKey);
            if (null == uafRequest) {
                LOGGER.error("action=postUafRequest, UafOperations.newUafRequest is null");
                return Utils.getResult(UafOperations.BAD_REQUEST_RESPONSE, servletResponse);
            }
            RpContext context = null;
            String contextJson = uafRequest.getContext();
            if (null != contextJson) {
                try {
                    context = RpContext.fromJson(contextJson);
                } catch (IllegalArgumentException | IllegalStateException ex) {
                    LOGGER.error("action=postUafRequest, RpContext.fromJson exception");
                    return Utils.getResult(UafOperations.BAD_REQUEST_RESPONSE, servletResponse);
                }
            } else {
                context = RpContext.newBuilder().build();
            }

            // Set session ID
            String sessionId = UUID.randomUUID().toString();
            Utils.putContext(sessionId, context, configuration.getLifetimeMillis());
            OperationType op = uafRequest.getOperationType();
            HttpResponse response = null;
            try {
                switch (op) {
                    case REGISTRATION:
                        response = processRegistrationRequest(sessionId, uafRequest, context);
                        break;
                    case AUTHENTICATION:
                        LOGGER.error("Not yet supported");
                        throw new UnsupportedOperationException(op + " is not yet supported");
                    case DEREGISTRATION:
                    	LOGGER.error("Not yet supported");
                    	throw new UnsupportedOperationException(op + " is not yet supported");
                    default:
                        LOGGER.error("action=postUafRequest, Unsupported operation type: {}", op);
                        return Utils.getResult(UafOperations.BAD_REQUEST_RESPONSE, servletResponse);
                }
            } catch (ConnectException ex) {
                return Utils.getResult(UafOperations.HTTP_INTERNAL_SERVER_ERROR, servletResponse);
            }
            if (response.getStatusCode() == HTTP_PAYMENT_REQUIRED) {
                LOGGER.error("action=licenseFail, Invalid license : {}", "HTTP_PAYMENT_REQUIRED");
                return Utils.getResult(UafOperations.HTTP_PAYMENT_REQUIRED, servletResponse);
            }
            String result = Utils.getResult(response, servletResponse);
            servletResponse.setContentType(response.getContentType());
            List<Object> mList = new ArrayList<Object>();
            mList.add(uafRequest.getOperationType());
            mList.add(response.getBody());
            mList.add(result);
            LOGGER.trace(
                    "action=postUafRequest, operation=\"{}\", response=\"{}\", return=\"{}\"",
                    mList.toArray());
            return result;
        } catch (Exception ex) {
            LOGGER.error("action=postUafRequest, Exception thrown.", ex);
            servletResponse.setStatus(ApiConstants.HTTP_CODE_INTERNAL_SERVER_ERROR);
            return ex.getMessage();
        }
    }

    private HttpResponse processRegistrationRequest(String sessionId, UafRequest uafRequest,
            RpContext context) throws ConnectException {
        uafRequest.setServerData(sessionId).setUserId(context.getUserName())
                .setUserName(context.getUserName());
        UafRequestResult result = uafRequest.execute();
        if (result.isSuccess()) {
            result.setLifetimeMillis(configuration.getLifetimeMillis());
        }
        return result.getResponse();
    }
}
