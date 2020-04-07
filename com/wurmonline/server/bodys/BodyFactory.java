// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.bodys;

import java.util.HashMap;
import com.wurmonline.shared.exceptions.WurmServerException;
import com.wurmonline.server.creatures.Creature;
import java.util.Map;

public final class BodyFactory
{
    private static final Map<Byte, BodyTemplate> bodyTemplates;
    
    public static Body getBody(final Creature creature, final byte typ, final short centimetersHigh, final short centimetersLong, final short centimetersWide) throws Exception {
        final BodyTemplate template = BodyFactory.bodyTemplates.get(typ);
        if (template != null) {
            return new Body(template, creature, centimetersHigh, centimetersLong, centimetersWide);
        }
        throw new WurmServerException("No such bodytype: " + Byte.toString(typ));
    }
    
    static {
        (bodyTemplates = new HashMap<Byte, BodyTemplate>()).put((byte)0, new BodyHuman());
        BodyFactory.bodyTemplates.put((byte)3, new BodyDog());
        BodyFactory.bodyTemplates.put((byte)1, new BodyHorse());
        BodyFactory.bodyTemplates.put((byte)4, new BodyEttin());
        BodyFactory.bodyTemplates.put((byte)5, new BodyCyclops());
        BodyFactory.bodyTemplates.put((byte)2, new BodyBear());
        BodyFactory.bodyTemplates.put((byte)6, new BodyDragon());
        BodyFactory.bodyTemplates.put((byte)7, new BodyBird());
        BodyFactory.bodyTemplates.put((byte)8, new BodySpider());
        BodyFactory.bodyTemplates.put((byte)9, new BodySnake());
    }
}
