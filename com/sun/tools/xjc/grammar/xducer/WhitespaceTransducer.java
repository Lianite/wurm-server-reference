// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar.xducer;

import com.sun.xml.bind.JAXBAssertionError;
import com.sun.codemodel.JExpression;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.msv.datatype.xsd.WhiteSpaceProcessor;
import com.sun.tools.xjc.generator.util.WhitespaceNormalizer;
import com.sun.codemodel.JCodeModel;

public class WhitespaceTransducer extends TransducerDecorator
{
    private final JCodeModel codeModel;
    private final WhitespaceNormalizer ws;
    
    private WhitespaceTransducer(final Transducer _core, final JCodeModel _codeModel, final WhitespaceNormalizer _ws) {
        super(_core);
        this.codeModel = _codeModel;
        this.ws = _ws;
    }
    
    public static Transducer create(final Transducer _core, final JCodeModel _codeModel, final WhitespaceNormalizer _ws) {
        if (_ws == WhitespaceNormalizer.PRESERVE) {
            return _core;
        }
        return (Transducer)new WhitespaceTransducer(_core, _codeModel, _ws);
    }
    
    public static Transducer create(final Transducer _core, final JCodeModel _codeModel, final WhiteSpaceProcessor wsf) {
        return create(_core, _codeModel, getNormalizer(wsf));
    }
    
    public static Transducer create(final Transducer _core, final JCodeModel _codeModel, final XSSimpleType t) {
        final XSFacet f = t.getFacet("whiteSpace");
        if (f == null) {
            return _core;
        }
        return create(_core, _codeModel, WhitespaceNormalizer.parse(f.getValue()));
    }
    
    public boolean isBuiltin() {
        return this.core.isBuiltin();
    }
    
    public JExpression generateDeserializer(final JExpression literal, final DeserializerContext context) {
        return super.generateDeserializer(this.ws.generate(this.codeModel, literal), context);
    }
    
    private static WhitespaceNormalizer getNormalizer(final WhiteSpaceProcessor proc) {
        if (proc == WhiteSpaceProcessor.theCollapse) {
            return WhitespaceNormalizer.COLLAPSE;
        }
        if (proc == WhiteSpaceProcessor.theReplace) {
            return WhitespaceNormalizer.REPLACE;
        }
        if (proc == WhiteSpaceProcessor.thePreserve) {
            return WhitespaceNormalizer.PRESERVE;
        }
        throw new JAXBAssertionError();
    }
}
