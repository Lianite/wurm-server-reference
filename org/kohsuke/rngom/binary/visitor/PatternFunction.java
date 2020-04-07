// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.binary.visitor;

import org.kohsuke.rngom.binary.AfterPattern;
import org.kohsuke.rngom.binary.RefPattern;
import org.kohsuke.rngom.binary.ListPattern;
import org.kohsuke.rngom.binary.TextPattern;
import org.kohsuke.rngom.binary.ValuePattern;
import org.kohsuke.rngom.binary.DataExceptPattern;
import org.kohsuke.rngom.binary.DataPattern;
import org.kohsuke.rngom.binary.AttributePattern;
import org.kohsuke.rngom.binary.ElementPattern;
import org.kohsuke.rngom.binary.OneOrMorePattern;
import org.kohsuke.rngom.binary.ChoicePattern;
import org.kohsuke.rngom.binary.InterleavePattern;
import org.kohsuke.rngom.binary.GroupPattern;
import org.kohsuke.rngom.binary.ErrorPattern;
import org.kohsuke.rngom.binary.NotAllowedPattern;
import org.kohsuke.rngom.binary.EmptyPattern;

public interface PatternFunction
{
    Object caseEmpty(final EmptyPattern p0);
    
    Object caseNotAllowed(final NotAllowedPattern p0);
    
    Object caseError(final ErrorPattern p0);
    
    Object caseGroup(final GroupPattern p0);
    
    Object caseInterleave(final InterleavePattern p0);
    
    Object caseChoice(final ChoicePattern p0);
    
    Object caseOneOrMore(final OneOrMorePattern p0);
    
    Object caseElement(final ElementPattern p0);
    
    Object caseAttribute(final AttributePattern p0);
    
    Object caseData(final DataPattern p0);
    
    Object caseDataExcept(final DataExceptPattern p0);
    
    Object caseValue(final ValuePattern p0);
    
    Object caseText(final TextPattern p0);
    
    Object caseList(final ListPattern p0);
    
    Object caseRef(final RefPattern p0);
    
    Object caseAfter(final AfterPattern p0);
}
