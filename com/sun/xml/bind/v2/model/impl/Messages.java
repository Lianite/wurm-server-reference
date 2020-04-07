// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.impl;

import java.text.MessageFormat;
import java.util.ResourceBundle;

enum Messages
{
    ID_MUST_BE_STRING, 
    MUTUALLY_EXCLUSIVE_ANNOTATIONS, 
    DUPLICATE_ANNOTATIONS, 
    NO_DEFAULT_CONSTRUCTOR, 
    CANT_HANDLE_INTERFACE, 
    CANT_HANDLE_INNER_CLASS, 
    ANNOTATION_ON_WRONG_METHOD, 
    GETTER_SETTER_INCOMPATIBLE_TYPE, 
    DUPLICATE_ENTRY_IN_PROP_ORDER, 
    DUPLICATE_PROPERTIES, 
    XML_ELEMENT_MAPPING_ON_NON_IXMLELEMENT_METHOD, 
    SCOPE_IS_NOT_COMPLEXTYPE, 
    CONFLICTING_XML_ELEMENT_MAPPING, 
    REFERENCE_TO_NON_ELEMENT, 
    NON_EXISTENT_ELEMENT_MAPPING, 
    TWO_ATTRIBUTE_WILDCARDS, 
    SUPER_CLASS_HAS_WILDCARD, 
    INVALID_ATTRIBUTE_WILDCARD_TYPE, 
    PROPERTY_MISSING_FROM_ORDER, 
    PROPERTY_ORDER_CONTAINS_UNUSED_ENTRY, 
    INVALID_XML_ENUM_VALUE, 
    FAILED_TO_INITIALE_DATATYPE_FACTORY, 
    NO_IMAGE_WRITER, 
    ILLEGAL_MIME_TYPE, 
    ILLEGAL_ANNOTATION, 
    MULTIPLE_VALUE_PROPERTY, 
    ELEMENT_AND_VALUE_PROPERTY, 
    CONFLICTING_XML_TYPE_MAPPING, 
    XMLVALUE_IN_DERIVED_TYPE, 
    SIMPLE_TYPE_IS_REQUIRED, 
    PROPERTY_COLLISION, 
    INVALID_IDREF, 
    INVALID_XML_ELEMENT_REF, 
    NO_XML_ELEMENT_DECL, 
    XML_ELEMENT_WRAPPER_ON_NON_COLLECTION, 
    ANNOTATION_NOT_ALLOWED, 
    XMLLIST_NEEDS_SIMPLETYPE, 
    XMLLIST_ON_SINGLE_PROPERTY, 
    NO_FACTORY_METHOD, 
    FACTORY_CLASS_NEEDS_FACTORY_METHOD, 
    INCOMPATIBLE_API_VERSION, 
    INCOMPATIBLE_API_VERSION_MUSTANG, 
    RUNNING_WITH_1_0_RUNTIME, 
    MISSING_JAXB_PROPERTIES, 
    TRANSIENT_FIELD_NOT_BINDABLE, 
    THERE_MUST_BE_VALUE_IN_XMLVALUE, 
    UNMATCHABLE_ADAPTER, 
    ANONYMOUS_ARRAY_ITEM, 
    ACCESSORFACTORY_INSTANTIATION_EXCEPTION, 
    ACCESSORFACTORY_ACCESS_EXCEPTION, 
    CUSTOM_ACCESSORFACTORY_PROPERTY_ERROR, 
    CUSTOM_ACCESSORFACTORY_FIELD_ERROR, 
    XMLGREGORIANCALENDAR_INVALID, 
    XMLGREGORIANCALENDAR_SEC, 
    XMLGREGORIANCALENDAR_MIN, 
    XMLGREGORIANCALENDAR_HR, 
    XMLGREGORIANCALENDAR_DAY, 
    XMLGREGORIANCALENDAR_MONTH, 
    XMLGREGORIANCALENDAR_YEAR, 
    XMLGREGORIANCALENDAR_TIMEZONE;
    
    private static final ResourceBundle rb;
    
    public String toString() {
        return this.format(new Object[0]);
    }
    
    public String format(final Object... args) {
        return MessageFormat.format(Messages.rb.getString(this.name()), args);
    }
    
    static {
        rb = ResourceBundle.getBundle(Messages.class.getName());
    }
}
