// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.impl;

import java.util.UUID;
import java.util.Collections;
import java.util.ArrayList;
import javax.xml.datatype.Duration;
import javax.xml.namespace.NamespaceContext;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.io.UnsupportedEncodingException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.Result;
import java.io.Writer;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import com.sun.xml.bind.v2.util.DataSourceSource;
import javax.xml.transform.Source;
import javax.activation.DataSource;
import com.sun.istack.ByteArrayDataSource;
import javax.activation.DataHandler;
import com.sun.xml.bind.v2.runtime.output.Pcdata;
import javax.imageio.stream.ImageOutputStream;
import java.util.Iterator;
import java.awt.image.RenderedImage;
import javax.imageio.ImageWriter;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.helpers.ValidationEventImpl;
import com.sun.xml.bind.v2.util.ByteArrayOutputStreamEx;
import java.awt.Graphics;
import java.awt.image.ImageObserver;
import java.awt.MediaTracker;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.awt.Image;
import java.net.URISyntaxException;
import java.net.URI;
import java.net.MalformedURLException;
import com.sun.xml.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.xml.bind.v2.TODO;
import java.net.URL;
import com.sun.xml.bind.WhiteSpaceProcessor;
import java.io.File;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConstants;
import java.util.Calendar;
import com.sun.xml.bind.v2.runtime.Name;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import java.util.HashMap;
import javax.xml.bind.MarshalException;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import com.sun.xml.bind.DatatypeConverterImpl;
import com.sun.xml.bind.v2.runtime.unmarshaller.Base64Data;
import com.sun.xml.bind.api.AccessorException;
import com.sun.xml.bind.v2.runtime.XMLSerializer;
import javax.xml.namespace.QName;
import javax.xml.datatype.DatatypeFactory;
import java.util.List;
import java.util.Map;
import com.sun.xml.bind.v2.runtime.Transducer;
import com.sun.xml.bind.v2.model.runtime.RuntimeBuiltinLeafInfo;
import java.lang.reflect.Type;

public abstract class RuntimeBuiltinLeafInfoImpl<T> extends BuiltinLeafInfoImpl<Type, Class> implements RuntimeBuiltinLeafInfo, Transducer<T>
{
    public static final Map<Type, RuntimeBuiltinLeafInfoImpl<?>> LEAVES;
    public static final RuntimeBuiltinLeafInfoImpl<String> STRING;
    public static final List<RuntimeBuiltinLeafInfoImpl<?>> builtinBeanInfos;
    private static final DatatypeFactory datatypeFactory;
    private static final Map<QName, String> xmlGregorianCalendarFormatString;
    private static final Map<QName, Integer> xmlGregorianCalendarFieldRef;
    
    private RuntimeBuiltinLeafInfoImpl(final Class type, final QName... typeNames) {
        super(type, typeNames);
        RuntimeBuiltinLeafInfoImpl.LEAVES.put(type, this);
    }
    
    public final Class getClazz() {
        return this.getType();
    }
    
    public final Transducer getTransducer() {
        return this;
    }
    
    public boolean useNamespace() {
        return false;
    }
    
    public final boolean isDefault() {
        return true;
    }
    
    public void declareNamespace(final T o, final XMLSerializer w) throws AccessorException {
    }
    
    public QName getTypeName(final T instance) {
        return null;
    }
    
    private static byte[] decodeBase64(final CharSequence text) {
        if (text instanceof Base64Data) {
            final Base64Data base64Data = (Base64Data)text;
            return base64Data.getExact();
        }
        return DatatypeConverterImpl._parseBase64Binary(text.toString());
    }
    
    private static QName createXS(final String typeName) {
        return new QName("http://www.w3.org/2001/XMLSchema", typeName);
    }
    
    private static DatatypeFactory init() {
        try {
            return DatatypeFactory.newInstance();
        }
        catch (DatatypeConfigurationException e) {
            throw new Error(Messages.FAILED_TO_INITIALE_DATATYPE_FACTORY.format(new Object[0]), e);
        }
    }
    
    private static void checkXmlGregorianCalendarFieldRef(final QName type, final XMLGregorianCalendar cal) throws MarshalException {
        final StringBuffer buf = new StringBuffer();
        int bitField = RuntimeBuiltinLeafInfoImpl.xmlGregorianCalendarFieldRef.get(type);
        final int l = 1;
        int pos = 0;
        while (bitField != 0) {
            final int bit = bitField & 0x1;
            bitField >>>= 4;
            ++pos;
            if (bit == 1) {
                switch (pos) {
                    case 1: {
                        if (cal.getSecond() == Integer.MIN_VALUE) {
                            buf.append("  " + Messages.XMLGREGORIANCALENDAR_SEC);
                            continue;
                        }
                        continue;
                    }
                    case 2: {
                        if (cal.getMinute() == Integer.MIN_VALUE) {
                            buf.append("  " + Messages.XMLGREGORIANCALENDAR_MIN);
                            continue;
                        }
                        continue;
                    }
                    case 3: {
                        if (cal.getHour() == Integer.MIN_VALUE) {
                            buf.append("  " + Messages.XMLGREGORIANCALENDAR_HR);
                            continue;
                        }
                        continue;
                    }
                    case 4: {
                        if (cal.getDay() == Integer.MIN_VALUE) {
                            buf.append("  " + Messages.XMLGREGORIANCALENDAR_DAY);
                            continue;
                        }
                        continue;
                    }
                    case 5: {
                        if (cal.getMonth() == Integer.MIN_VALUE) {
                            buf.append("  " + Messages.XMLGREGORIANCALENDAR_MONTH);
                            continue;
                        }
                        continue;
                    }
                    case 6: {
                        if (cal.getYear() == Integer.MIN_VALUE) {
                            buf.append("  " + Messages.XMLGREGORIANCALENDAR_YEAR);
                            continue;
                        }
                        continue;
                    }
                }
            }
        }
        if (buf.length() > 0) {
            throw new MarshalException(Messages.XMLGREGORIANCALENDAR_INVALID.format(type.getLocalPart()) + buf.toString());
        }
    }
    
    static {
        LEAVES = new HashMap<Type, RuntimeBuiltinLeafInfoImpl<?>>();
        STRING = new StringImpl<String>(String.class, new QName[] { createXS("string"), createXS("normalizedString"), createXS("anyURI"), createXS("token"), createXS("language"), createXS("Name"), createXS("NCName"), createXS("NMTOKEN"), createXS("ENTITY") }) {
            public String parse(final CharSequence text) {
                return text.toString();
            }
            
            public String print(final String s) {
                return s;
            }
            
            public final void writeText(final XMLSerializer w, final String o, final String fieldName) throws IOException, SAXException, XMLStreamException {
                w.text(o, fieldName);
            }
            
            public final void writeLeafElement(final XMLSerializer w, final Name tagName, final String o, final String fieldName) throws IOException, SAXException, XMLStreamException {
                w.leafElement(tagName, o, fieldName);
            }
        };
        final RuntimeBuiltinLeafInfoImpl[] secondary = { new StringImpl<Character>(Character.class, new QName[] { createXS("unsignedShort") }) {
                public Character parse(final CharSequence text) {
                    return (char)DatatypeConverterImpl._parseInt(text);
                }
                
                public String print(final Character v) {
                    return Integer.toString(v);
                }
            }, new StringImpl<Calendar>(Calendar.class, new QName[] { DatatypeConstants.DATETIME }) {
                public Calendar parse(final CharSequence text) {
                    return DatatypeConverterImpl._parseDateTime(text.toString());
                }
                
                public String print(final Calendar v) {
                    return DatatypeConverterImpl._printDateTime(v);
                }
            }, new StringImpl<GregorianCalendar>(GregorianCalendar.class, new QName[] { DatatypeConstants.DATETIME }) {
                public GregorianCalendar parse(final CharSequence text) {
                    return DatatypeConverterImpl._parseDateTime(text.toString());
                }
                
                public String print(final GregorianCalendar v) {
                    return DatatypeConverterImpl._printDateTime(v);
                }
            }, new StringImpl<Date>(Date.class, new QName[] { DatatypeConstants.DATETIME }) {
                public Date parse(final CharSequence text) {
                    return DatatypeConverterImpl._parseDateTime(text.toString()).getTime();
                }
                
                public String print(final Date v) {
                    final GregorianCalendar cal = new GregorianCalendar(0, 0, 0);
                    cal.setTime(v);
                    return DatatypeConverterImpl._printDateTime(cal);
                }
            }, new StringImpl<File>(File.class, new QName[] { createXS("string") }) {
                public File parse(final CharSequence text) {
                    return new File(WhiteSpaceProcessor.trim(text).toString());
                }
                
                public String print(final File v) {
                    return v.getPath();
                }
            }, new StringImpl<URL>(URL.class, new QName[] { createXS("anyURI") }) {
                public URL parse(final CharSequence text) throws SAXException {
                    TODO.checkSpec("JSR222 Issue #42");
                    try {
                        return new URL(WhiteSpaceProcessor.trim(text).toString());
                    }
                    catch (MalformedURLException e) {
                        UnmarshallingContext.getInstance().handleError(e);
                        return null;
                    }
                }
                
                public String print(final URL v) {
                    return v.toExternalForm();
                }
            }, new StringImpl<URI>(URI.class, new QName[] { createXS("string") }) {
                public URI parse(final CharSequence text) throws SAXException {
                    try {
                        return new URI(text.toString());
                    }
                    catch (URISyntaxException e) {
                        UnmarshallingContext.getInstance().handleError(e);
                        return null;
                    }
                }
                
                public String print(final URI v) {
                    return v.toString();
                }
            }, new StringImpl<Class>(Class.class, new QName[] { createXS("string") }) {
                public Class parse(final CharSequence text) throws SAXException {
                    TODO.checkSpec("JSR222 Issue #42");
                    try {
                        final String name = WhiteSpaceProcessor.trim(text).toString();
                        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
                        if (cl != null) {
                            return cl.loadClass(name);
                        }
                        return Class.forName(name);
                    }
                    catch (ClassNotFoundException e) {
                        UnmarshallingContext.getInstance().handleError(e);
                        return null;
                    }
                }
                
                public String print(final Class v) {
                    return v.getName();
                }
            }, new PcdataImpl<Image>(Image.class, new QName[] { createXS("base64Binary") }) {
                public Image parse(final CharSequence text) throws SAXException {
                    try {
                        InputStream is;
                        if (text instanceof Base64Data) {
                            is = ((Base64Data)text).getInputStream();
                        }
                        else {
                            is = new ByteArrayInputStream(decodeBase64(text));
                        }
                        try {
                            return ImageIO.read(is);
                        }
                        finally {
                            is.close();
                        }
                    }
                    catch (IOException e) {
                        UnmarshallingContext.getInstance().handleError(e);
                        return null;
                    }
                }
                
                private BufferedImage convertToBufferedImage(final Image image) throws IOException {
                    if (image instanceof BufferedImage) {
                        return (BufferedImage)image;
                    }
                    final MediaTracker tracker = new MediaTracker(new Component() {});
                    tracker.addImage(image, 0);
                    try {
                        tracker.waitForAll();
                    }
                    catch (InterruptedException e) {
                        throw new IOException(e.getMessage());
                    }
                    final BufferedImage bufImage = new BufferedImage(image.getWidth(null), image.getHeight(null), 2);
                    final Graphics g = bufImage.createGraphics();
                    g.drawImage(image, 0, 0, null);
                    return bufImage;
                }
                
                public Base64Data print(final Image v) {
                    final ByteArrayOutputStreamEx imageData = new ByteArrayOutputStreamEx();
                    final XMLSerializer xs = XMLSerializer.getInstance();
                    String mimeType = xs.getXMIMEContentType();
                    if (mimeType == null || mimeType.startsWith("image/*")) {
                        mimeType = "image/png";
                    }
                    try {
                        final Iterator<ImageWriter> itr = ImageIO.getImageWritersByMIMEType(mimeType);
                        if (!itr.hasNext()) {
                            xs.handleEvent(new ValidationEventImpl(1, Messages.NO_IMAGE_WRITER.format(mimeType), xs.getCurrentLocation(null)));
                            throw new RuntimeException("no encoder for MIME type " + mimeType);
                        }
                        final ImageWriter w = itr.next();
                        final ImageOutputStream os = ImageIO.createImageOutputStream(imageData);
                        w.setOutput(os);
                        w.write(this.convertToBufferedImage(v));
                        os.close();
                        w.dispose();
                    }
                    catch (IOException e) {
                        xs.handleError(e);
                        throw new RuntimeException(e);
                    }
                    final Base64Data bd = new Base64Data();
                    imageData.set(bd, mimeType);
                    return bd;
                }
            }, new PcdataImpl<DataHandler>(DataHandler.class, new QName[] { createXS("base64Binary") }) {
                public DataHandler parse(final CharSequence text) {
                    if (text instanceof Base64Data) {
                        return ((Base64Data)text).getDataHandler();
                    }
                    return new DataHandler(new ByteArrayDataSource(decodeBase64(text), UnmarshallingContext.getInstance().getXMIMEContentType()));
                }
                
                public Base64Data print(final DataHandler v) {
                    final Base64Data bd = new Base64Data();
                    bd.set(v);
                    return bd;
                }
            }, new PcdataImpl<Source>(Source.class, new QName[] { createXS("base64Binary") }) {
                public Source parse(final CharSequence text) throws SAXException {
                    try {
                        if (text instanceof Base64Data) {
                            return new DataSourceSource(((Base64Data)text).getDataHandler());
                        }
                        return new DataSourceSource(new ByteArrayDataSource(decodeBase64(text), UnmarshallingContext.getInstance().getXMIMEContentType()));
                    }
                    catch (MimeTypeParseException e) {
                        UnmarshallingContext.getInstance().handleError(e);
                        return null;
                    }
                }
                
                public Base64Data print(final Source v) {
                    final XMLSerializer xs = XMLSerializer.getInstance();
                    final Base64Data bd = new Base64Data();
                    final String contentType = xs.getXMIMEContentType();
                    MimeType mt = null;
                    if (contentType != null) {
                        try {
                            mt = new MimeType(contentType);
                        }
                        catch (MimeTypeParseException e) {
                            xs.handleError(e);
                        }
                    }
                    if (v instanceof DataSourceSource) {
                        final DataSource ds = ((DataSourceSource)v).getDataSource();
                        final String dsct = ds.getContentType();
                        if (dsct != null && (contentType == null || contentType.equals(dsct))) {
                            bd.set(new DataHandler(ds));
                            return bd;
                        }
                    }
                    String charset = null;
                    if (mt != null) {
                        charset = mt.getParameter("charset");
                    }
                    if (charset == null) {
                        charset = "UTF-8";
                    }
                    try {
                        final ByteArrayOutputStreamEx baos = new ByteArrayOutputStreamEx();
                        xs.getIdentityTransformer().transform(v, new StreamResult(new OutputStreamWriter(baos, charset)));
                        baos.set(bd, "application/xml; charset=" + charset);
                        return bd;
                    }
                    catch (TransformerException e2) {
                        xs.handleError(e2);
                    }
                    catch (UnsupportedEncodingException e3) {
                        xs.handleError(e3);
                    }
                    bd.set(new byte[0], "application/xml");
                    return bd;
                }
            }, new StringImpl<XMLGregorianCalendar>(XMLGregorianCalendar.class, new QName[] { createXS("anySimpleType"), DatatypeConstants.DATE, DatatypeConstants.DATETIME, DatatypeConstants.TIME, DatatypeConstants.GMONTH, DatatypeConstants.GDAY, DatatypeConstants.GYEAR, DatatypeConstants.GYEARMONTH, DatatypeConstants.GMONTHDAY }) {
                public String print(final XMLGregorianCalendar cal) {
                    final XMLSerializer xs = XMLSerializer.getInstance();
                    final QName type = xs.getSchemaType();
                    if (type != null) {
                        try {
                            checkXmlGregorianCalendarFieldRef(type, cal);
                            final String format = RuntimeBuiltinLeafInfoImpl.xmlGregorianCalendarFormatString.get(type);
                            if (format != null) {
                                return this.format(format, cal);
                            }
                        }
                        catch (MarshalException e) {
                            System.out.println(e.toString());
                            return "";
                        }
                    }
                    return cal.toXMLFormat();
                }
                
                public XMLGregorianCalendar parse(final CharSequence lexical) throws SAXException {
                    try {
                        return RuntimeBuiltinLeafInfoImpl.datatypeFactory.newXMLGregorianCalendar(lexical.toString());
                    }
                    catch (Exception e) {
                        UnmarshallingContext.getInstance().handleError(e);
                        return null;
                    }
                }
                
                private String format(final String format, final XMLGregorianCalendar value) {
                    final StringBuilder buf = new StringBuilder();
                    int fidx = 0;
                    final int flen = format.length();
                    while (fidx < flen) {
                        final char fch = format.charAt(fidx++);
                        if (fch != '%') {
                            buf.append(fch);
                        }
                        else {
                            switch (format.charAt(fidx++)) {
                                case 'Y': {
                                    this.printNumber(buf, value.getEonAndYear(), 4);
                                    continue;
                                }
                                case 'M': {
                                    this.printNumber(buf, value.getMonth(), 2);
                                    continue;
                                }
                                case 'D': {
                                    this.printNumber(buf, value.getDay(), 2);
                                    continue;
                                }
                                case 'h': {
                                    this.printNumber(buf, value.getHour(), 2);
                                    continue;
                                }
                                case 'm': {
                                    this.printNumber(buf, value.getMinute(), 2);
                                    continue;
                                }
                                case 's': {
                                    this.printNumber(buf, value.getSecond(), 2);
                                    if (value.getFractionalSecond() != null) {
                                        final String frac = value.getFractionalSecond().toString();
                                        buf.append(frac.substring(1, frac.length()));
                                        continue;
                                    }
                                    continue;
                                }
                                case 'z': {
                                    int offset = value.getTimezone();
                                    if (offset == 0) {
                                        buf.append('Z');
                                        continue;
                                    }
                                    if (offset != Integer.MIN_VALUE) {
                                        if (offset < 0) {
                                            buf.append('-');
                                            offset *= -1;
                                        }
                                        else {
                                            buf.append('+');
                                        }
                                        this.printNumber(buf, offset / 60, 2);
                                        buf.append(':');
                                        this.printNumber(buf, offset % 60, 2);
                                        continue;
                                    }
                                    continue;
                                }
                                default: {
                                    throw new InternalError();
                                }
                            }
                        }
                    }
                    return buf.toString();
                }
                
                private void printNumber(final StringBuilder out, final BigInteger number, final int nDigits) {
                    final String s = number.toString();
                    for (int i = s.length(); i < nDigits; ++i) {
                        out.append('0');
                    }
                    out.append(s);
                }
                
                private void printNumber(final StringBuilder out, final int number, final int nDigits) {
                    final String s = String.valueOf(number);
                    for (int i = s.length(); i < nDigits; ++i) {
                        out.append('0');
                    }
                    out.append(s);
                }
                
                public QName getTypeName(final XMLGregorianCalendar cal) {
                    return cal.getXMLSchemaType();
                }
            } };
        final RuntimeBuiltinLeafInfoImpl[] primary = { RuntimeBuiltinLeafInfoImpl.STRING, new StringImpl<Boolean>(Boolean.class, new QName[] { createXS("boolean") }) {
                public Boolean parse(final CharSequence text) {
                    return DatatypeConverterImpl._parseBoolean(text);
                }
                
                public String print(final Boolean v) {
                    return v.toString();
                }
            }, new PcdataImpl<byte[]>(byte[].class, new QName[] { createXS("base64Binary"), createXS("hexBinary") }) {
                public byte[] parse(final CharSequence text) {
                    return decodeBase64(text);
                }
                
                public Base64Data print(final byte[] v) {
                    final XMLSerializer w = XMLSerializer.getInstance();
                    final Base64Data bd = new Base64Data();
                    final String mimeType = w.getXMIMEContentType();
                    bd.set(v, mimeType);
                    return bd;
                }
            }, new StringImpl<Byte>(Byte.class, new QName[] { createXS("byte") }) {
                public Byte parse(final CharSequence text) {
                    return DatatypeConverterImpl._parseByte(text);
                }
                
                public String print(final Byte v) {
                    return DatatypeConverterImpl._printByte(v);
                }
            }, new StringImpl<Short>(Short.class, new QName[] { createXS("short"), createXS("unsignedByte") }) {
                public Short parse(final CharSequence text) {
                    return DatatypeConverterImpl._parseShort(text);
                }
                
                public String print(final Short v) {
                    return DatatypeConverterImpl._printShort(v);
                }
            }, new StringImpl<Integer>(Integer.class, new QName[] { createXS("int"), createXS("unsignedShort") }) {
                public Integer parse(final CharSequence text) {
                    return DatatypeConverterImpl._parseInt(text);
                }
                
                public String print(final Integer v) {
                    return DatatypeConverterImpl._printInt(v);
                }
            }, new StringImpl<Long>(Long.class, new QName[] { createXS("long"), createXS("unsignedInt") }) {
                public Long parse(final CharSequence text) {
                    return DatatypeConverterImpl._parseLong(text);
                }
                
                public String print(final Long v) {
                    return DatatypeConverterImpl._printLong(v);
                }
            }, new StringImpl<Float>(Float.class, new QName[] { createXS("float") }) {
                public Float parse(final CharSequence text) {
                    return DatatypeConverterImpl._parseFloat(text.toString());
                }
                
                public String print(final Float v) {
                    return DatatypeConverterImpl._printFloat(v);
                }
            }, new StringImpl<Double>(Double.class, new QName[] { createXS("double") }) {
                public Double parse(final CharSequence text) {
                    return DatatypeConverterImpl._parseDouble(text);
                }
                
                public String print(final Double v) {
                    return DatatypeConverterImpl._printDouble(v);
                }
            }, new StringImpl<BigInteger>(BigInteger.class, new QName[] { createXS("integer"), createXS("positiveInteger"), createXS("negativeInteger"), createXS("nonPositiveInteger"), createXS("nonNegativeInteger"), createXS("unsignedLong") }) {
                public BigInteger parse(final CharSequence text) {
                    return DatatypeConverterImpl._parseInteger(text);
                }
                
                public String print(final BigInteger v) {
                    return DatatypeConverterImpl._printInteger(v);
                }
            }, new StringImpl<BigDecimal>(BigDecimal.class, new QName[] { createXS("decimal") }) {
                public BigDecimal parse(final CharSequence text) {
                    return DatatypeConverterImpl._parseDecimal(text.toString());
                }
                
                public String print(final BigDecimal v) {
                    return DatatypeConverterImpl._printDecimal(v);
                }
            }, new StringImpl<QName>(QName.class, new QName[] { createXS("QName") }) {
                public QName parse(final CharSequence text) throws SAXException {
                    try {
                        return DatatypeConverterImpl._parseQName(text.toString(), UnmarshallingContext.getInstance());
                    }
                    catch (IllegalArgumentException e) {
                        UnmarshallingContext.getInstance().handleError(e);
                        return null;
                    }
                }
                
                public String print(final QName v) {
                    return DatatypeConverterImpl._printQName(v, XMLSerializer.getInstance().getNamespaceContext());
                }
                
                public boolean useNamespace() {
                    return true;
                }
                
                public void declareNamespace(final QName v, final XMLSerializer w) {
                    w.getNamespaceContext().declareNamespace(v.getNamespaceURI(), v.getPrefix(), false);
                }
            }, new StringImpl<Duration>(Duration.class, new QName[] { createXS("duration") }) {
                public String print(final Duration duration) {
                    return duration.toString();
                }
                
                public Duration parse(final CharSequence lexical) {
                    TODO.checkSpec("JSR222 Issue #42");
                    return RuntimeBuiltinLeafInfoImpl.datatypeFactory.newDuration(lexical.toString());
                }
            }, new StringImpl<Void>(Void.class, new QName[0]) {
                public String print(final Void value) {
                    return "";
                }
                
                public Void parse(final CharSequence lexical) {
                    return null;
                }
            } };
        final List<RuntimeBuiltinLeafInfoImpl<?>> l = new ArrayList<RuntimeBuiltinLeafInfoImpl<?>>(secondary.length + primary.length + 1);
        for (final RuntimeBuiltinLeafInfoImpl<?> item : secondary) {
            l.add(item);
        }
        try {
            l.add(new UUIDImpl());
        }
        catch (LinkageError linkageError) {}
        for (final RuntimeBuiltinLeafInfoImpl<?> item : primary) {
            l.add(item);
        }
        builtinBeanInfos = Collections.unmodifiableList((List<? extends RuntimeBuiltinLeafInfoImpl<?>>)l);
        datatypeFactory = init();
        xmlGregorianCalendarFormatString = new HashMap<QName, String>();
        final Map<QName, String> m = RuntimeBuiltinLeafInfoImpl.xmlGregorianCalendarFormatString;
        m.put(DatatypeConstants.DATETIME, "%Y-%M-%DT%h:%m:%s%z");
        m.put(DatatypeConstants.DATE, "%Y-%M-%D%z");
        m.put(DatatypeConstants.TIME, "%h:%m:%s%z");
        m.put(DatatypeConstants.GMONTH, "--%M--%z");
        m.put(DatatypeConstants.GDAY, "---%D%z");
        m.put(DatatypeConstants.GYEAR, "%Y%z");
        m.put(DatatypeConstants.GYEARMONTH, "%Y-%M%z");
        m.put(DatatypeConstants.GMONTHDAY, "--%M-%D%z");
        xmlGregorianCalendarFieldRef = new HashMap<QName, Integer>();
        final Map<QName, Integer> f = RuntimeBuiltinLeafInfoImpl.xmlGregorianCalendarFieldRef;
        f.put(DatatypeConstants.DATETIME, 17895697);
        f.put(DatatypeConstants.DATE, 17895424);
        f.put(DatatypeConstants.TIME, 16777489);
        f.put(DatatypeConstants.GDAY, 16781312);
        f.put(DatatypeConstants.GMONTH, 16842752);
        f.put(DatatypeConstants.GYEAR, 17825792);
        f.put(DatatypeConstants.GYEARMONTH, 17891328);
        f.put(DatatypeConstants.GMONTHDAY, 16846848);
    }
    
    private abstract static class StringImpl<T> extends RuntimeBuiltinLeafInfoImpl<T>
    {
        protected StringImpl(final Class type, final QName... typeNames) {
            super(type, typeNames, null);
        }
        
        public abstract String print(final T p0) throws AccessorException;
        
        public void writeText(final XMLSerializer w, final T o, final String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
            w.text(this.print(o), fieldName);
        }
        
        public void writeLeafElement(final XMLSerializer w, final Name tagName, final T o, final String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
            w.leafElement(tagName, this.print(o), fieldName);
        }
    }
    
    private abstract static class PcdataImpl<T> extends RuntimeBuiltinLeafInfoImpl<T>
    {
        protected PcdataImpl(final Class type, final QName... typeNames) {
            super(type, typeNames, null);
        }
        
        public abstract Pcdata print(final T p0) throws AccessorException;
        
        public final void writeText(final XMLSerializer w, final T o, final String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
            w.text(this.print(o), fieldName);
        }
        
        public final void writeLeafElement(final XMLSerializer w, final Name tagName, final T o, final String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
            w.leafElement(tagName, this.print(o), fieldName);
        }
    }
    
    private static class UUIDImpl extends StringImpl<UUID>
    {
        public UUIDImpl() {
            super(UUID.class, new QName[] { createXS("string") });
        }
        
        public UUID parse(final CharSequence text) throws SAXException {
            TODO.checkSpec("JSR222 Issue #42");
            try {
                return UUID.fromString(WhiteSpaceProcessor.trim(text).toString());
            }
            catch (IllegalArgumentException e) {
                UnmarshallingContext.getInstance().handleError(e);
                return null;
            }
        }
        
        public String print(final UUID v) {
            return v.toString();
        }
    }
}
