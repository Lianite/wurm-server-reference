// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.validation.decoration;

import java.util.List;
import javafx.scene.Node;
import org.controlsfx.control.decoration.Decorator;
import javafx.scene.control.Control;
import java.util.Collection;
import org.controlsfx.validation.ValidationMessage;
import org.controlsfx.control.decoration.Decoration;

public abstract class AbstractValidationDecoration implements ValidationDecoration
{
    private static final String VALIDATION_DECORATION = "$org.controlsfx.decoration.vaidation$";
    
    private static boolean isValidationDecoration(final Decoration decoration) {
        return decoration != null && decoration.getProperties().get("$org.controlsfx.decoration.vaidation$") == Boolean.TRUE;
    }
    
    private static void setValidationDecoration(final Decoration decoration) {
        if (decoration != null) {
            decoration.getProperties().put("$org.controlsfx.decoration.vaidation$", Boolean.TRUE);
        }
    }
    
    protected abstract Collection<Decoration> createValidationDecorations(final ValidationMessage p0);
    
    protected abstract Collection<Decoration> createRequiredDecorations(final Control p0);
    
    @Override
    public void removeDecorations(final Control target) {
        final List<Decoration> decorations = (List<Decoration>)Decorator.getDecorations((Node)target);
        if (decorations != null) {
            for (final Decoration d : (Decoration[])Decorator.getDecorations((Node)target).toArray((Object[])new Decoration[0])) {
                if (isValidationDecoration(d)) {
                    Decorator.removeDecoration((Node)target, d);
                }
            }
        }
    }
    
    @Override
    public void applyValidationDecoration(final ValidationMessage message) {
        this.createValidationDecorations(message).stream().forEach(d -> this.decorate(message.getTarget(), d));
    }
    
    @Override
    public void applyRequiredDecoration(final Control target) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aload_1         /* target */
        //     1: invokestatic    invokestatic   !!! ERROR
        //     4: ifeq            29
        //     7: aload_0         /* this */
        //     8: aload_1         /* target */
        //     9: invokevirtual   org/controlsfx/validation/decoration/AbstractValidationDecoration.createRequiredDecorations:(Ljavafx/scene/control/Control;)Ljava/util/Collection;
        //    12: invokeinterface java/util/Collection.stream:()Ljava/util/stream/Stream;
        //    17: aload_0         /* this */
        //    18: aload_1         /* target */
        //    19: invokedynamic   accept:(Lorg/controlsfx/validation/decoration/AbstractValidationDecoration;Ljavafx/scene/control/Control;)Ljava/util/function/Consumer;
        //    24: invokeinterface java/util/stream/Stream.forEach:(Ljava/util/function/Consumer;)V
        //    29: return         
        //    LocalVariableTable:
        //  Start  Length  Slot  Name    Signature
        //  -----  ------  ----  ------  -------------------------------------------------------------------
        //  0      30      0     this    Lorg/controlsfx/validation/decoration/AbstractValidationDecoration;
        //  0      30      1     target  Ljavafx/scene/control/Control;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalArgumentException: Argument 'typeArguments' must not have any null elements.
        //     at com.strobel.core.VerifyArgument.noNullElementsAndNotEmpty(VerifyArgument.java:145)
        //     at com.strobel.assembler.metadata.CoreMetadataFactory$UnresolvedType.makeGenericType(CoreMetadataFactory.java:570)
        //     at com.strobel.assembler.metadata.CoreMetadataFactory.makeParameterizedType(CoreMetadataFactory.java:156)
        //     at com.strobel.assembler.metadata.signatures.Reifier.visitClassTypeSignature(Reifier.java:125)
        //     at com.strobel.assembler.metadata.signatures.ClassTypeSignature.accept(ClassTypeSignature.java:46)
        //     at com.strobel.assembler.metadata.MetadataParser.parseTypeSignature(MetadataParser.java:123)
        //     at com.strobel.assembler.ir.MetadataReader.readAttributeCore(MetadataReader.java:180)
        //     at com.strobel.assembler.metadata.ClassFileReader.readAttributeCore(ClassFileReader.java:261)
        //     at com.strobel.assembler.ir.MetadataReader.readAttribute(MetadataReader.java:50)
        //     at com.strobel.assembler.ir.MetadataReader.readAttributes(MetadataReader.java:40)
        //     at com.strobel.assembler.metadata.ClassFileReader.readAttributeCore(ClassFileReader.java:203)
        //     at com.strobel.assembler.ir.MetadataReader.inflateAttribute(MetadataReader.java:367)
        //     at com.strobel.assembler.ir.MetadataReader.inflateAttributes(MetadataReader.java:344)
        //     at com.strobel.assembler.metadata.ClassFileReader.defineMethods(ClassFileReader.java:979)
        //     at com.strobel.assembler.metadata.ClassFileReader.readClass(ClassFileReader.java:441)
        //     at com.strobel.assembler.metadata.ClassFileReader.readClass(ClassFileReader.java:366)
        //     at com.strobel.assembler.metadata.MetadataSystem.resolveType(MetadataSystem.java:124)
        //     at com.strobel.decompiler.NoRetryMetadataSystem.resolveType(DecompilerDriver.java:463)
        //     at com.strobel.assembler.metadata.MetadataSystem.resolveCore(MetadataSystem.java:76)
        //     at com.strobel.assembler.metadata.MetadataResolver.resolve(MetadataResolver.java:104)
        //     at com.strobel.assembler.metadata.CoreMetadataFactory$UnresolvedType.resolve(CoreMetadataFactory.java:589)
        //     at com.strobel.assembler.metadata.MetadataResolver.resolve(MetadataResolver.java:128)
        //     at com.strobel.assembler.metadata.CoreMetadataFactory$UnresolvedType.resolve(CoreMetadataFactory.java:599)
        //     at com.strobel.assembler.metadata.MethodReference.resolve(MethodReference.java:172)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2428)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:672)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:655)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferBinaryExpression(TypeAnalysis.java:2104)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1531)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:778)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1551)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:672)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:655)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:365)
        //     at com.strobel.decompiler.ast.TypeAnalysis.run(TypeAnalysis.java:96)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:109)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:214)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:757)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:655)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:532)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:499)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:141)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:130)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:105)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:317)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:238)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:123)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private void decorate(final Control target, final Decoration d) {
        setValidationDecoration(d);
        Decorator.addDecoration((Node)target, d);
    }
}
