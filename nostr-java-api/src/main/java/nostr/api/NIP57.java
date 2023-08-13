/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nostr.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import nostr.api.factory.TagFactory;
import nostr.api.factory.impl.NIP42.RelaysTagFactory;
import nostr.base.ElementAttribute;
import nostr.base.PublicKey;
import nostr.base.Relay;
import nostr.event.BaseTag;
import nostr.event.impl.GenericEvent;
import nostr.event.impl.GenericTag;
import nostr.event.tag.EventTag;
import nostr.event.tag.PubKeyTag;
import nostr.id.Identity;
import nostr.util.NostrException;

/**
 *
 * @author eric
 */
public class NIP57 extends Nostr {

    /**
     * 
     * @param amount
     * @param lnurl
     * @param relay
     * @param recipient
     * @param eventTag
     * @param content
     * @return 
     */
    public static GenericEvent createZapEvent(Integer amount, String lnurl, Relay relay, PublicKey recipient, EventTag eventTag, String content) {
        var tags = new ArrayList<BaseTag>();
        tags.add(createLnurlTag(lnurl));
        tags.add(createAmountTag(amount));
        tags.add(new RelaysTagFactory(Relay.builder().uri(relay.getUri()).build()).create());
        tags.add(PubKeyTag.builder().publicKey(recipient).build());
        tags.add(eventTag);

        var sender = Identity.getInstance().getPublicKey();
        return new GenericEvent(sender, 9734, tags, content);
    }

    /**
     * 
     * @param zapEvent
     * @param bolt11
     * @param description
     * @param preimage
     * @param recipient
     * @param eventTag
     * @return 
     */
    public static GenericEvent createZapReceiptEvent(GenericEvent zapEvent, String bolt11, String description, String preimage, PublicKey recipient, EventTag eventTag) {
        var tags = new ArrayList<BaseTag>();
        tags.add(createBolt11Tag(bolt11));
        tags.add(createDescriptionTag(description));
        tags.add(createPreImageTag(preimage));
        tags.add(new PubKeyTag(recipient, null, null));
        tags.add(eventTag);

        try {
            var sender = Identity.getInstance().getPublicKey();
            return new GenericEvent(sender, 9735, tags, Nostr.Json.encode(zapEvent));
        } catch (NostrException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 
     * @param lnurl
     * @return 
     */
    public static GenericTag createLnurlTag(String lnurl) {
        return new TagFactory("lnurl", 57, lnurl).create();
    }

    /**
     * 
     * @param bolt11
     * @return 
     */
    public static GenericTag createBolt11Tag(String bolt11) {
        return new TagFactory("lnurl", 57, bolt11).create();
    }

    /**
     * 
     * @param preimage
     * @return 
     */
    public static GenericTag createPreImageTag(String preimage) {
        return new TagFactory("lnurl", 57, preimage).create();
    }

    /**
     * 
     * @param description
     * @return 
     */
    public static GenericTag createDescriptionTag(String description) {
        return new TagFactory("lnurl", 57, description).create();
    }

    /**
     * 
     * @param amount
     * @return 
     */
    public static GenericTag createAmountTag(Integer amount) {
        return new TagFactory("lnurl", 57, amount.toString()).create();
    }

    /**
     * 
     * @param receiver
     * @param relay
     * @param weight
     * @return 
     */
    public static GenericTag createZapTag(PublicKey receiver, Relay relay, Integer weight) {
            Set<ElementAttribute> attributes = new HashSet<>();
            var receiverAttr = new ElementAttribute("receiver", receiver.toString(), 57);
            var relayAttr = new ElementAttribute("relay", relay.getUri(), 57);
            if (weight != null) {
                var weightAttr = new ElementAttribute("weight", weight, 57);
                attributes.add(weightAttr);
            }
            
            attributes.add(receiverAttr);
            attributes.add(relayAttr);
            
            var result = new GenericTag("zap", 57, attributes);
            return result;
    }

    /**
     * 
     * @param receiver
     * @param relay
     * @return 
     */
    public static GenericTag createZapTag(PublicKey receiver, Relay relay) {
        return createZapTag(receiver, relay, null);
    }
}
