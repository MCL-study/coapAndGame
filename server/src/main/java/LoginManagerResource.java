import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;

import static org.eclipse.californium.core.coap.CoAP.ResponseCode.CHANGED;

/**
 * Created by myks7 on 2016-03-15.
 */
public class LoginManagerResource extends CoapResource {
    private UserManager userManager;
    public LoginManagerResource(String name,UserManager userManager){
        super(name);
        this.userManager = userManager;
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        UserData user =  userManager.createUser();
        Integer integer = user.getId();
        exchange.respond(integer.toString());

    }
}
