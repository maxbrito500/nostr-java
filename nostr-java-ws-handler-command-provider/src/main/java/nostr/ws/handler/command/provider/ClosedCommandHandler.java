package nostr.ws.handler.command.provider;

import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import nostr.base.Command;
import nostr.base.PrivateKey;
import nostr.base.annotation.DefaultHandler;
import nostr.client.Client;
import nostr.context.CommandContext;
import nostr.context.impl.DefaultCommandContext;
import nostr.event.impl.CanonicalAuthenticationEvent;
import nostr.event.json.codec.BaseMessageEncoder;
import nostr.event.message.CanonicalAuthenticationMessage;
import nostr.event.message.ClosedMessage;
import nostr.id.Identity;
import nostr.ws.handler.command.CommandHandler;

import java.util.logging.Level;

@DefaultHandler(command = Command.CLOSED)
@NoArgsConstructor
@Log
public class ClosedCommandHandler implements CommandHandler {

    @Override
    public void handle(CommandContext context) {

        log.info("onClosed event {0}" + context);

        if (context instanceof DefaultCommandContext defaultCommandContext) {
            var message = defaultCommandContext.getMessage();

            if (message instanceof ClosedMessage closedMessage) {
                if (closedMessage.getMessage().startsWith("auth-required:")) {
                    log.log(Level.WARNING, "Authentication required on relay {0}", defaultCommandContext.getRelay());

                    var privateKey = defaultCommandContext.getPrivateKey();
                    var identity = Identity.getInstance(new PrivateKey(privateKey));
                    var publicKey = identity.getPublicKey();
                    var challenge = defaultCommandContext.getChallenge();
                    var relay = defaultCommandContext.getRelay();

                    // Create the event
                    var canonicalAuthenticationEvent = new CanonicalAuthenticationEvent(publicKey, challenge, relay);

                    // Sign the event
                    identity.sign(canonicalAuthenticationEvent);

                    var client = Client.getInstance(); // No need to pass the request context here. The client will use the default one
                    var canonicalAuthenticationMessage = new CanonicalAuthenticationMessage(canonicalAuthenticationEvent);
                    var encoder = new BaseMessageEncoder(canonicalAuthenticationMessage);
                    var encodedMessage = encoder.encode();
                    log.log(Level.INFO, "Sending authentication event {0} to the relay {1}", new Object[]{encodedMessage, relay});

                    // Publish the event to the relay
                    client.send(canonicalAuthenticationMessage, relay);
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid context type");
        }
    }
}