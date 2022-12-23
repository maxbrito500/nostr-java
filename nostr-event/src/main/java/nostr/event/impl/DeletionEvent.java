
package nostr.event.impl;

import nostr.event.Kind;
import nostr.base.PublicKey;
import nostr.event.list.TagList;
import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.tcheeric.nostr.base.annotation.NIPSupport;
import nostr.util.NostrException;

/**
 *
 * @author squirrel
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NIPSupport(value=9, description="Event Deletion")
public class DeletionEvent extends GenericEvent {

    public DeletionEvent(PublicKey pubKey, TagList tagList, String content) throws NoSuchAlgorithmException, IntrospectionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, NostrException {
        
        super(pubKey, Kind.DELETION, tagList, content);        
    }

    public DeletionEvent(PublicKey pubKey, TagList tagList) throws NoSuchAlgorithmException, IntrospectionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, NostrException {
        
        this(pubKey, tagList, "Deletion request");
    }
}