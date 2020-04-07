// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.decorator;

import org.xml.sax.Locator;
import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.State;
import com.sun.tools.xjc.reader.NameConverter;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;
import com.sun.tools.xjc.ErrorReceiver;
import com.sun.msv.reader.GrammarReader;
import com.sun.tools.xjc.reader.PackageManager;
import com.sun.tools.xjc.util.CodeModelClassFactory;

public class RoleBasedDecorator extends DecoratorImpl
{
    private final CodeModelClassFactory classFactory;
    private final Decorator defaultDecorator;
    private final PackageManager packageManager;
    
    public RoleBasedDecorator(final GrammarReader _reader, final ErrorReceiver _errorReceiver, final AnnotatedGrammar _grammar, final NameConverter _nc, final PackageManager pkgMan, final Decorator _defaultDecorator) {
        super(_reader, _grammar, _nc);
        this.defaultDecorator = _defaultDecorator;
        this.packageManager = pkgMan;
        this.classFactory = new CodeModelClassFactory(_errorReceiver);
    }
    
    public Expression decorate(final State state, final Expression exp) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aload_1         /* state */
        //     1: invokevirtual   com/sun/msv/reader/State.getStartTag:()Lcom/sun/msv/util/StartTagInfo;
        //     4: astore_3        /* tag */
        //     5: aload_0         /* this */
        //     6: aload_3         /* tag */
        //     7: ldc             "role"
        //     9: invokevirtual   com/sun/tools/xjc/reader/decorator/RoleBasedDecorator.getAttribute:(Lcom/sun/msv/util/StartTagInfo;Ljava/lang/String;)Ljava/lang/String;
        //    12: astore          role
        //    14: aload           role
        //    16: ifnonnull       40
        //    19: aload_0         /* this */
        //    20: getfield        com/sun/tools/xjc/reader/decorator/RoleBasedDecorator.defaultDecorator:Lcom/sun/tools/xjc/reader/decorator/Decorator;
        //    23: ifnull          38
        //    26: aload_0         /* this */
        //    27: getfield        com/sun/tools/xjc/reader/decorator/RoleBasedDecorator.defaultDecorator:Lcom/sun/tools/xjc/reader/decorator/Decorator;
        //    30: aload_1         /* state */
        //    31: aload_2         /* exp */
        //    32: invokeinterface com/sun/tools/xjc/reader/decorator/Decorator.decorate:(Lcom/sun/msv/reader/State;Lcom/sun/msv/grammar/Expression;)Lcom/sun/msv/grammar/Expression;
        //    37: astore_2        /* exp */
        //    38: aload_2         /* exp */
        //    39: areturn        
        //    40: aload           role
        //    42: invokevirtual   java/lang/String.intern:()Ljava/lang/String;
        //    45: astore          role
        //    47: aload           role
        //    49: ldc             "none"
        //    51: if_acmpne       56
        //    54: aload_2         /* exp */
        //    55: areturn        
        //    56: aload           role
        //    58: ldc             "superClass"
        //    60: if_acmpne       80
        //    63: new             Lcom/sun/tools/xjc/grammar/SuperClassItem;
        //    66: dup            
        //    67: aconst_null    
        //    68: aload_1         /* state */
        //    69: invokevirtual   com/sun/msv/reader/State.getLocation:()Lorg/xml/sax/Locator;
        //    72: invokespecial   com/sun/tools/xjc/grammar/SuperClassItem.<init>:(Lcom/sun/msv/grammar/Expression;Lorg/xml/sax/Locator;)V
        //    75: astore          roleExp
        //    77: goto            669
        //    80: aload           role
        //    82: ldc             "class"
        //    84: if_acmpne       138
        //    87: aload_0         /* this */
        //    88: getfield        com/sun/tools/xjc/reader/decorator/RoleBasedDecorator.grammar:Lcom/sun/tools/xjc/grammar/AnnotatedGrammar;
        //    91: aload_0         /* this */
        //    92: getfield        com/sun/tools/xjc/reader/decorator/RoleBasedDecorator.classFactory:Lcom/sun/tools/xjc/util/CodeModelClassFactory;
        //    95: aload_0         /* this */
        //    96: getfield        com/sun/tools/xjc/reader/decorator/RoleBasedDecorator.packageManager:Lcom/sun/tools/xjc/reader/PackageManager;
        //    99: invokeinterface com/sun/tools/xjc/reader/PackageManager.getCurrentPackage:()Lcom/sun/codemodel/JPackage;
        //   104: aload_0         /* this */
        //   105: aload_1         /* state */
        //   106: aload_2         /* exp */
        //   107: aload           role
        //   109: ldc             ""
        //   111: aload_1         /* state */
        //   112: invokevirtual   com/sun/msv/reader/State.getLocation:()Lorg/xml/sax/Locator;
        //   115: invokevirtual   com/sun/tools/xjc/reader/decorator/RoleBasedDecorator.decideName:(Lcom/sun/msv/reader/State;Lcom/sun/msv/grammar/Expression;Ljava/lang/String;Ljava/lang/String;Lorg/xml/sax/Locator;)Ljava/lang/String;
        //   118: aload_1         /* state */
        //   119: invokevirtual   com/sun/msv/reader/State.getLocation:()Lorg/xml/sax/Locator;
        //   122: invokevirtual   com/sun/tools/xjc/util/CodeModelClassFactory.createInterface:(Lcom/sun/codemodel/JClassContainer;Ljava/lang/String;Lorg/xml/sax/Locator;)Lcom/sun/codemodel/JDefinedClass;
        //   125: aconst_null    
        //   126: aload_1         /* state */
        //   127: invokevirtual   com/sun/msv/reader/State.getLocation:()Lorg/xml/sax/Locator;
        //   130: invokevirtual   com/sun/tools/xjc/grammar/AnnotatedGrammar.createClassItem:(Lcom/sun/codemodel/JDefinedClass;Lcom/sun/msv/grammar/Expression;Lorg/xml/sax/Locator;)Lcom/sun/tools/xjc/grammar/ClassItem;
        //   133: astore          roleExp
        //   135: goto            669
        //   138: aload           role
        //   140: ldc             "field"
        //   142: if_acmpne       336
        //   145: aload_0         /* this */
        //   146: aload_3         /* tag */
        //   147: ldc             "collection"
        //   149: invokevirtual   com/sun/tools/xjc/reader/decorator/RoleBasedDecorator.getAttribute:(Lcom/sun/msv/util/StartTagInfo;Ljava/lang/String;)Ljava/lang/String;
        //   152: astore          collection
        //   154: aload_0         /* this */
        //   155: aload_3         /* tag */
        //   156: ldc             "baseType"
        //   158: invokevirtual   com/sun/tools/xjc/reader/decorator/RoleBasedDecorator.getAttribute:(Lcom/sun/msv/util/StartTagInfo;Ljava/lang/String;)Ljava/lang/String;
        //   161: astore          typeAtt
        //   163: aload_0         /* this */
        //   164: aload_3         /* tag */
        //   165: ldc             "delegate"
        //   167: invokevirtual   com/sun/tools/xjc/reader/decorator/RoleBasedDecorator.getAttribute:(Lcom/sun/msv/util/StartTagInfo;Ljava/lang/String;)Ljava/lang/String;
        //   170: astore          delegation
        //   172: aconst_null    
        //   173: astore          type
        //   175: aload           typeAtt
        //   177: ifnull          211
        //   180: aload_0         /* this */
        //   181: getfield        com/sun/tools/xjc/reader/decorator/RoleBasedDecorator.codeModel:Lcom/sun/codemodel/JCodeModel;
        //   184: aload           typeAtt
        //   186: invokevirtual   com/sun/codemodel/JCodeModel.ref:(Ljava/lang/String;)Lcom/sun/codemodel/JClass;
        //   189: astore          type
        //   191: goto            211
        //   194: astore          e
        //   196: aload_0         /* this */
        //   197: ldc             "ClassNotFound"
        //   199: aload           typeAtt
        //   201: invokestatic    com/sun/tools/xjc/reader/decorator/Messages.format:(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/String;
        //   204: aload_1         /* state */
        //   205: invokevirtual   com/sun/msv/reader/State.getLocation:()Lorg/xml/sax/Locator;
        //   208: invokespecial   com/sun/tools/xjc/reader/decorator/RoleBasedDecorator.reportError:(Ljava/lang/String;Lorg/xml/sax/Locator;)V
        //   211: new             Lcom/sun/tools/xjc/grammar/FieldItem;
        //   214: dup            
        //   215: aload_0         /* this */
        //   216: aload_1         /* state */
        //   217: aload_2         /* exp */
        //   218: aload           role
        //   220: ldc             ""
        //   222: aload_1         /* state */
        //   223: invokevirtual   com/sun/msv/reader/State.getLocation:()Lorg/xml/sax/Locator;
        //   226: invokevirtual   com/sun/tools/xjc/reader/decorator/RoleBasedDecorator.decideName:(Lcom/sun/msv/reader/State;Lcom/sun/msv/grammar/Expression;Ljava/lang/String;Ljava/lang/String;Lorg/xml/sax/Locator;)Ljava/lang/String;
        //   229: aconst_null    
        //   230: aload           type
        //   232: aload_0         /* this */
        //   233: getfield        com/sun/tools/xjc/reader/decorator/RoleBasedDecorator.reader:Lcom/sun/msv/reader/GrammarReader;
        //   236: getfield        com/sun/msv/reader/GrammarReader.locator:Lorg/xml/sax/Locator;
        //   239: invokespecial   com/sun/tools/xjc/grammar/FieldItem.<init>:(Ljava/lang/String;Lcom/sun/msv/grammar/Expression;Lcom/sun/codemodel/JType;Lorg/xml/sax/Locator;)V
        //   242: astore          fi
        //   244: aload           fi
        //   246: astore          roleExp
        //   248: aload           delegation
        //   250: ifnull          269
        //   253: aload           delegation
        //   255: ldc             "true"
        //   257: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //   260: ifeq            269
        //   263: aload           fi
        //   265: iconst_1       
        //   266: invokevirtual   com/sun/tools/xjc/grammar/FieldItem.setDelegation:(Z)V
        //   269: aload           collection
        //   271: ifnull          333
        //   274: aload           collection
        //   276: ldc             "array"
        //   278: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //   281: ifeq            292
        //   284: aload           fi
        //   286: getstatic       com/sun/tools/xjc/generator/field/ArrayFieldRenderer.theFactory:Lcom/sun/tools/xjc/generator/field/FieldRendererFactory;
        //   289: putfield        com/sun/tools/xjc/grammar/FieldItem.realization:Lcom/sun/tools/xjc/generator/field/FieldRendererFactory;
        //   292: aload           collection
        //   294: ldc             "list"
        //   296: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //   299: ifeq            310
        //   302: aload           fi
        //   304: getstatic       com/sun/tools/xjc/generator/field/TypedListFieldRenderer.theFactory:Lcom/sun/tools/xjc/generator/field/FieldRendererFactory;
        //   307: putfield        com/sun/tools/xjc/grammar/FieldItem.realization:Lcom/sun/tools/xjc/generator/field/FieldRendererFactory;
        //   310: aload           fi
        //   312: getfield        com/sun/tools/xjc/grammar/FieldItem.realization:Lcom/sun/tools/xjc/generator/field/FieldRendererFactory;
        //   315: ifnonnull       333
        //   318: aload_0         /* this */
        //   319: ldc             "InvalidCollectionType"
        //   321: aload           collection
        //   323: invokestatic    com/sun/tools/xjc/reader/decorator/Messages.format:(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/String;
        //   326: aload_1         /* state */
        //   327: invokevirtual   com/sun/msv/reader/State.getLocation:()Lorg/xml/sax/Locator;
        //   330: invokespecial   com/sun/tools/xjc/reader/decorator/RoleBasedDecorator.reportError:(Ljava/lang/String;Lorg/xml/sax/Locator;)V
        //   333: goto            669
        //   336: aload           role
        //   338: ldc             "interface"
        //   340: if_acmpne       394
        //   343: aload_0         /* this */
        //   344: getfield        com/sun/tools/xjc/reader/decorator/RoleBasedDecorator.grammar:Lcom/sun/tools/xjc/grammar/AnnotatedGrammar;
        //   347: aload_0         /* this */
        //   348: getfield        com/sun/tools/xjc/reader/decorator/RoleBasedDecorator.classFactory:Lcom/sun/tools/xjc/util/CodeModelClassFactory;
        //   351: aload_0         /* this */
        //   352: getfield        com/sun/tools/xjc/reader/decorator/RoleBasedDecorator.packageManager:Lcom/sun/tools/xjc/reader/PackageManager;
        //   355: invokeinterface com/sun/tools/xjc/reader/PackageManager.getCurrentPackage:()Lcom/sun/codemodel/JPackage;
        //   360: aload_0         /* this */
        //   361: aload_1         /* state */
        //   362: aload_2         /* exp */
        //   363: aload           role
        //   365: ldc             ""
        //   367: aload_1         /* state */
        //   368: invokevirtual   com/sun/msv/reader/State.getLocation:()Lorg/xml/sax/Locator;
        //   371: invokevirtual   com/sun/tools/xjc/reader/decorator/RoleBasedDecorator.decideName:(Lcom/sun/msv/reader/State;Lcom/sun/msv/grammar/Expression;Ljava/lang/String;Ljava/lang/String;Lorg/xml/sax/Locator;)Ljava/lang/String;
        //   374: aload_1         /* state */
        //   375: invokevirtual   com/sun/msv/reader/State.getLocation:()Lorg/xml/sax/Locator;
        //   378: invokevirtual   com/sun/tools/xjc/util/CodeModelClassFactory.createInterface:(Lcom/sun/codemodel/JClassContainer;Ljava/lang/String;Lorg/xml/sax/Locator;)Lcom/sun/codemodel/JDefinedClass;
        //   381: aconst_null    
        //   382: aload_1         /* state */
        //   383: invokevirtual   com/sun/msv/reader/State.getLocation:()Lorg/xml/sax/Locator;
        //   386: invokevirtual   com/sun/tools/xjc/grammar/AnnotatedGrammar.createInterfaceItem:(Lcom/sun/codemodel/JClass;Lcom/sun/msv/grammar/Expression;Lorg/xml/sax/Locator;)Lcom/sun/tools/xjc/grammar/InterfaceItem;
        //   389: astore          roleExp
        //   391: goto            669
        //   394: aload           role
        //   396: ldc             "primitive"
        //   398: if_acmpne       559
        //   401: aload_0         /* this */
        //   402: aload_3         /* tag */
        //   403: ldc             "name"
        //   405: invokevirtual   com/sun/tools/xjc/reader/decorator/RoleBasedDecorator.getAttribute:(Lcom/sun/msv/util/StartTagInfo;Ljava/lang/String;)Ljava/lang/String;
        //   408: astore          name
        //   410: aload_0         /* this */
        //   411: aload_3         /* tag */
        //   412: ldc             "parseMethod"
        //   414: invokevirtual   com/sun/tools/xjc/reader/decorator/RoleBasedDecorator.getAttribute:(Lcom/sun/msv/util/StartTagInfo;Ljava/lang/String;)Ljava/lang/String;
        //   417: astore          parseMethod
        //   419: aload_0         /* this */
        //   420: aload_3         /* tag */
        //   421: ldc             "printMethod"
        //   423: invokevirtual   com/sun/tools/xjc/reader/decorator/RoleBasedDecorator.getAttribute:(Lcom/sun/msv/util/StartTagInfo;Ljava/lang/String;)Ljava/lang/String;
        //   426: astore          printMethod
        //   428: aload_0         /* this */
        //   429: aload_3         /* tag */
        //   430: ldc             "hasNsContext"
        //   432: ldc             "false"
        //   434: invokevirtual   com/sun/tools/xjc/reader/decorator/RoleBasedDecorator.getAttribute:(Lcom/sun/msv/util/StartTagInfo;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
        //   437: invokestatic    com/sun/msv/datatype/xsd/BooleanType.load:(Ljava/lang/String;)Ljava/lang/Boolean;
        //   440: invokevirtual   java/lang/Boolean.booleanValue:()Z
        //   443: istore          hasNsContext
        //   445: aload_0         /* this */
        //   446: getfield        com/sun/tools/xjc/reader/decorator/RoleBasedDecorator.grammar:Lcom/sun/tools/xjc/grammar/AnnotatedGrammar;
        //   449: new             Lcom/sun/tools/xjc/grammar/xducer/UserTransducer;
        //   452: dup            
        //   453: aload_0         /* this */
        //   454: getfield        com/sun/tools/xjc/reader/decorator/RoleBasedDecorator.codeModel:Lcom/sun/codemodel/JCodeModel;
        //   457: aload           name
        //   459: aload_0         /* this */
        //   460: getfield        com/sun/tools/xjc/reader/decorator/RoleBasedDecorator.reader:Lcom/sun/msv/reader/GrammarReader;
        //   463: getfield        com/sun/msv/reader/GrammarReader.controller:Lcom/sun/msv/reader/Controller;
        //   466: aload_1         /* state */
        //   467: invokevirtual   com/sun/msv/reader/State.getLocation:()Lorg/xml/sax/Locator;
        //   470: invokestatic    com/sun/tools/xjc/reader/TypeUtil.getType:(Lcom/sun/codemodel/JCodeModel;Ljava/lang/String;Lorg/xml/sax/ErrorHandler;Lorg/xml/sax/Locator;)Lcom/sun/codemodel/JType;
        //   473: aload           parseMethod
        //   475: ifnull          483
        //   478: aload           parseMethod
        //   480: goto            485
        //   483: ldc             "new"
        //   485: aload           printMethod
        //   487: ifnull          495
        //   490: aload           printMethod
        //   492: goto            497
        //   495: ldc             "toString"
        //   497: iload           hasNsContext
        //   499: invokespecial   com/sun/tools/xjc/grammar/xducer/UserTransducer.<init>:(Lcom/sun/codemodel/JType;Ljava/lang/String;Ljava/lang/String;Z)V
        //   502: getstatic       com/sun/msv/datatype/xsd/StringType.theInstance:Lcom/sun/msv/datatype/xsd/StringType;
        //   505: aconst_null    
        //   506: aload_1         /* state */
        //   507: invokevirtual   com/sun/msv/reader/State.getLocation:()Lorg/xml/sax/Locator;
        //   510: invokevirtual   com/sun/tools/xjc/grammar/AnnotatedGrammar.createPrimitiveItem:(Lcom/sun/tools/xjc/grammar/xducer/Transducer;Lcom/sun/msv/datatype/DatabindableDatatype;Lcom/sun/msv/grammar/Expression;Lorg/xml/sax/Locator;)Lcom/sun/tools/xjc/grammar/PrimitiveItem;
        //   513: astore          roleExp
        //   515: goto            556
        //   518: astore          e
        //   520: new             Lcom/sun/msv/grammar/OtherExp;
        //   523: dup            
        //   524: invokespecial   com/sun/msv/grammar/OtherExp.<init>:()V
        //   527: astore          roleExp
        //   529: goto            556
        //   532: astore          e
        //   534: aload_0         /* this */
        //   535: aload           e
        //   537: invokevirtual   java/lang/IllegalArgumentException.getMessage:()Ljava/lang/String;
        //   540: aload_1         /* state */
        //   541: invokevirtual   com/sun/msv/reader/State.getLocation:()Lorg/xml/sax/Locator;
        //   544: invokespecial   com/sun/tools/xjc/reader/decorator/RoleBasedDecorator.reportError:(Ljava/lang/String;Lorg/xml/sax/Locator;)V
        //   547: new             Lcom/sun/msv/grammar/OtherExp;
        //   550: dup            
        //   551: invokespecial   com/sun/msv/grammar/OtherExp.<init>:()V
        //   554: astore          roleExp
        //   556: goto            669
        //   559: aload           role
        //   561: ldc             "dom"
        //   563: if_acmpne       629
        //   566: aload_0         /* this */
        //   567: aload_3         /* tag */
        //   568: ldc             "type"
        //   570: invokevirtual   com/sun/tools/xjc/reader/decorator/RoleBasedDecorator.getAttribute:(Lcom/sun/msv/util/StartTagInfo;Ljava/lang/String;)Ljava/lang/String;
        //   573: astore          type
        //   575: aload           type
        //   577: ifnonnull       584
        //   580: ldc             "w3c"
        //   582: astore          type
        //   584: aload           type
        //   586: invokestatic    com/sun/tools/xjc/grammar/ext/DOMItemFactory.getInstance:(Ljava/lang/String;)Lcom/sun/tools/xjc/grammar/ext/DOMItemFactory;
        //   589: aload_2         /* exp */
        //   590: invokestatic    com/sun/tools/xjc/grammar/util/NameFinder.findElement:(Lcom/sun/msv/grammar/Expression;)Lcom/sun/msv/grammar/NameClass;
        //   593: aload_0         /* this */
        //   594: getfield        com/sun/tools/xjc/reader/decorator/RoleBasedDecorator.grammar:Lcom/sun/tools/xjc/grammar/AnnotatedGrammar;
        //   597: aload_1         /* state */
        //   598: invokevirtual   com/sun/msv/reader/State.getLocation:()Lorg/xml/sax/Locator;
        //   601: invokevirtual   com/sun/tools/xjc/grammar/ext/DOMItemFactory.create:(Lcom/sun/msv/grammar/NameClass;Lcom/sun/tools/xjc/grammar/AnnotatedGrammar;Lorg/xml/sax/Locator;)Lcom/sun/tools/xjc/grammar/ExternalItem;
        //   604: astore          roleExp
        //   606: goto            626
        //   609: astore          e
        //   611: aload_0         /* this */
        //   612: aload           e
        //   614: invokevirtual   com/sun/tools/xjc/grammar/ext/DOMItemFactory$UndefinedNameException.getMessage:()Ljava/lang/String;
        //   617: aload_1         /* state */
        //   618: invokevirtual   com/sun/msv/reader/State.getLocation:()Lorg/xml/sax/Locator;
        //   621: invokespecial   com/sun/tools/xjc/reader/decorator/RoleBasedDecorator.reportError:(Ljava/lang/String;Lorg/xml/sax/Locator;)V
        //   624: aload_2         /* exp */
        //   625: areturn        
        //   626: goto            669
        //   629: aload           role
        //   631: ldc             "ignore"
        //   633: if_acmpne       652
        //   636: new             Lcom/sun/tools/xjc/grammar/IgnoreItem;
        //   639: dup            
        //   640: aload_1         /* state */
        //   641: invokevirtual   com/sun/msv/reader/State.getLocation:()Lorg/xml/sax/Locator;
        //   644: invokespecial   com/sun/tools/xjc/grammar/IgnoreItem.<init>:(Lorg/xml/sax/Locator;)V
        //   647: astore          roleExp
        //   649: goto            669
        //   652: aload_0         /* this */
        //   653: ldc             "UndefinedRole"
        //   655: aload           role
        //   657: invokestatic    com/sun/tools/xjc/reader/decorator/Messages.format:(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/String;
        //   660: aload_1         /* state */
        //   661: invokevirtual   com/sun/msv/reader/State.getLocation:()Lorg/xml/sax/Locator;
        //   664: invokespecial   com/sun/tools/xjc/reader/decorator/RoleBasedDecorator.reportError:(Ljava/lang/String;Lorg/xml/sax/Locator;)V
        //   667: aload_2         /* exp */
        //   668: areturn        
        //   669: aload_0         /* this */
        //   670: getfield        com/sun/tools/xjc/reader/decorator/RoleBasedDecorator.reader:Lcom/sun/msv/reader/GrammarReader;
        //   673: aload           5
        //   675: invokevirtual   com/sun/msv/reader/GrammarReader.setDeclaredLocationOf:(Ljava/lang/Object;)V
        //   678: aload           5
        //   680: aload_2         /* exp */
        //   681: putfield        com/sun/msv/grammar/OtherExp.exp:Lcom/sun/msv/grammar/Expression;
        //   684: aload           5
        //   686: areturn        
        //    LocalVariableTable:
        //  Start  Length  Slot  Name          Signature
        //  -----  ------  ----  ------------  ---------------------------------------------------------------------
        //  77     3       5     roleExp       Lcom/sun/msv/grammar/OtherExp;
        //  135    3       5     roleExp       Lcom/sun/msv/grammar/OtherExp;
        //  196    15      10    e             Ljava/lang/ClassNotFoundException;
        //  154    179     6     collection    Ljava/lang/String;
        //  163    170     7     typeAtt       Ljava/lang/String;
        //  172    161     8     delegation    Ljava/lang/String;
        //  175    158     9     type          Lcom/sun/codemodel/JClass;
        //  244    89      10    fi            Lcom/sun/tools/xjc/grammar/FieldItem;
        //  248    88      5     roleExp       Lcom/sun/msv/grammar/OtherExp;
        //  391    3       5     roleExp       Lcom/sun/msv/grammar/OtherExp;
        //  515    3       5     roleExp       Lcom/sun/msv/grammar/OtherExp;
        //  520    9       10    e             Lorg/xml/sax/SAXException;
        //  529    3       5     roleExp       Lcom/sun/msv/grammar/OtherExp;
        //  534    22      10    e             Ljava/lang/IllegalArgumentException;
        //  410    146     6     name          Ljava/lang/String;
        //  419    137     7     parseMethod   Ljava/lang/String;
        //  428    128     8     printMethod   Ljava/lang/String;
        //  445    111     9     hasNsContext  Z
        //  556    3       5     roleExp       Lcom/sun/msv/grammar/OtherExp;
        //  606    3       5     roleExp       Lcom/sun/msv/grammar/OtherExp;
        //  611    15      7     e             Lcom/sun/tools/xjc/grammar/ext/DOMItemFactory$UndefinedNameException;
        //  575    51      6     type          Ljava/lang/String;
        //  626    3       5     roleExp       Lcom/sun/msv/grammar/OtherExp;
        //  649    3       5     roleExp       Lcom/sun/msv/grammar/OtherExp;
        //  0      687     0     this          Lcom/sun/tools/xjc/reader/decorator/RoleBasedDecorator;
        //  0      687     1     state         Lcom/sun/msv/reader/State;
        //  0      687     2     exp           Lcom/sun/msv/grammar/Expression;
        //  5      682     3     tag           Lcom/sun/msv/util/StartTagInfo;
        //  14     673     4     role          Ljava/lang/String;
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                                                 
        //  -----  -----  -----  -----  ---------------------------------------------------------------------
        //  180    191    194    211    Ljava/lang/ClassNotFoundException;
        //  445    515    518    532    Lorg/xml/sax/SAXException;
        //  445    515    532    556    Ljava/lang/IllegalArgumentException;
        //  584    606    609    626    Lcom/sun/tools/xjc/grammar/ext/DOMItemFactory$UndefinedNameException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        //     at java.util.ArrayList.rangeCheck(Unknown Source)
        //     at java.util.ArrayList.get(Unknown Source)
        //     at com.strobel.decompiler.ast.AstBuilder.convertLocalVariables(AstBuilder.java:3035)
        //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2445)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:108)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:210)
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
    
    private void reportError(final String msg, final Locator locator) {
        this.reader.controller.error(new Locator[] { locator }, msg, (Exception)null);
    }
}
