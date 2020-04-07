// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.ArrayList;
import com.wurmonline.server.NoSuchEntryException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class CreationMatrix
{
    private static Map<Integer, List<CreationEntry>> matrix;
    private static CreationMatrix instance;
    private static Map<Integer, List<CreationEntry>> advancedEntries;
    private static Map<Integer, List<CreationEntry>> simpleEntries;
    
    public static CreationMatrix getInstance() {
        if (CreationMatrix.instance == null) {
            CreationMatrix.instance = new CreationMatrix();
        }
        return CreationMatrix.instance;
    }
    
    public void addCreationEntry(final CreationEntry entry) {
        Integer space = entry.getObjectTarget();
        List<CreationEntry> entrys = CreationMatrix.matrix.get(space);
        if (entrys == null) {
            entrys = new LinkedList<CreationEntry>();
        }
        entrys.add(entry);
        CreationMatrix.matrix.put(space, entrys);
        space = entry.getObjectCreated();
        if (entry instanceof AdvancedCreationEntry) {
            entrys = CreationMatrix.advancedEntries.get(space);
            if (entrys == null) {
                entrys = new LinkedList<CreationEntry>();
            }
            entrys.add(entry);
            CreationMatrix.advancedEntries.put(space, entrys);
        }
        entrys = CreationMatrix.simpleEntries.get(space);
        if (entrys == null) {
            entrys = new LinkedList<CreationEntry>();
        }
        entrys.add(entry);
        CreationMatrix.simpleEntries.put(space, entrys);
    }
    
    public CreationEntry[] getCreationOptionsFor(final int sourceTemplateId, final int targetTemplateId) {
        List<CreationEntry> entrys = CreationMatrix.matrix.get(targetTemplateId);
        final List<CreationEntry> options = new LinkedList<CreationEntry>();
        if (entrys != null) {
            for (final CreationEntry entry : entrys) {
                if (entry.getObjectSource() != sourceTemplateId) {
                    continue;
                }
                if (entry.getObjectTarget() != targetTemplateId) {
                    continue;
                }
                options.add(entry);
            }
        }
        entrys = CreationMatrix.matrix.get(sourceTemplateId);
        if (entrys != null) {
            for (final CreationEntry entry : entrys) {
                if (entry.getObjectSource() != targetTemplateId) {
                    continue;
                }
                if (entry.getObjectTarget() != sourceTemplateId) {
                    continue;
                }
                options.add(entry);
            }
        }
        final CreationEntry[] toReturn = new CreationEntry[options.size()];
        return options.toArray(toReturn);
    }
    
    public CreationEntry[] getCreationOptionsFor(final Item source, final Item target) {
        final Integer space = target.getTemplateId();
        List<CreationEntry> entrys = CreationMatrix.matrix.get(space);
        final List<CreationEntry> options = new LinkedList<CreationEntry>();
        if (entrys != null) {
            for (final CreationEntry entry : entrys) {
                if (entry.getObjectSource() != source.getTemplateId()) {
                    continue;
                }
                if (entry.getObjectSourceMaterial() != 0 && entry.getObjectSourceMaterial() != source.getMaterial()) {
                    continue;
                }
                if (entry.getObjectTarget() != target.getTemplateId()) {
                    continue;
                }
                if (entry.getObjectTargetMaterial() != 0 && entry.getObjectTargetMaterial() != target.getMaterial()) {
                    continue;
                }
                options.add(entry);
            }
        }
        entrys = CreationMatrix.matrix.get(source.getTemplateId());
        if (entrys != null) {
            for (final CreationEntry entry : entrys) {
                if (entry.getObjectSource() != target.getTemplateId()) {
                    continue;
                }
                if (entry.getObjectSourceMaterial() != 0 && entry.getObjectSourceMaterial() != target.getMaterial()) {
                    continue;
                }
                if (entry.getObjectTarget() != source.getTemplateId()) {
                    continue;
                }
                if (entry.getObjectTargetMaterial() != 0 && entry.getObjectTargetMaterial() != source.getMaterial()) {
                    continue;
                }
                options.add(entry);
            }
        }
        final CreationEntry[] toReturn = new CreationEntry[options.size()];
        return options.toArray(toReturn);
    }
    
    public AdvancedCreationEntry getAdvancedCreationEntry(final int objectCreated) throws NoSuchEntryException {
        if (CreationMatrix.advancedEntries == null) {
            throw new NoSuchEntryException("No entry with id " + objectCreated);
        }
        final LinkedList<CreationEntry> alist = CreationMatrix.advancedEntries.get(objectCreated);
        if (alist == null) {
            throw new NoSuchEntryException("No entry with id " + objectCreated);
        }
        final AdvancedCreationEntry toReturn = alist.getFirst();
        return toReturn;
    }
    
    public final CreationEntry[] getAdvancedEntries() {
        final List<CreationEntry> list = new ArrayList<CreationEntry>();
        for (final Integer in : CreationMatrix.advancedEntries.keySet()) {
            final List<CreationEntry> entrys = CreationMatrix.advancedEntries.get(in);
            for (final CreationEntry ee : entrys) {
                if (!list.contains(ee)) {
                    list.addAll(entrys);
                }
            }
        }
        return list.toArray(new CreationEntry[list.size()]);
    }
    
    public final Map<Integer, List<CreationEntry>> getAdvancedEntriesMap() {
        return CreationMatrix.advancedEntries;
    }
    
    public final CreationEntry[] getAdvancedEntriesNotEpicMission() {
        final Set<CreationEntry> advanced = new HashSet<CreationEntry>();
        for (final List<CreationEntry> entrys : CreationMatrix.advancedEntries.values()) {
            for (final CreationEntry entry : entrys) {
                if (!entry.isOnlyCreateEpicTargetMission && entry.isCreateEpicTargetMission) {
                    advanced.add(entry);
                }
            }
        }
        return advanced.toArray(new CreationEntry[advanced.size()]);
    }
    
    public final CreationEntry[] getSimpleEntries() {
        final List<CreationEntry> list = new ArrayList<CreationEntry>();
        for (final Integer in : CreationMatrix.simpleEntries.keySet()) {
            final List<CreationEntry> entrys = CreationMatrix.simpleEntries.get(in);
            for (final CreationEntry ee : entrys) {
                if (!list.contains(ee)) {
                    list.addAll(entrys);
                }
            }
        }
        return list.toArray(new CreationEntry[list.size()]);
    }
    
    public CreationEntry getCreationEntry(final int objectCreated) {
        CreationEntry toReturn = null;
        try {
            toReturn = this.getAdvancedCreationEntry(objectCreated);
        }
        catch (NoSuchEntryException nse) {
            final Integer space = objectCreated;
            final List<CreationEntry> entrys = CreationMatrix.simpleEntries.get(space);
            if (entrys == null) {
                return toReturn;
            }
            final Iterator<CreationEntry> iterator = entrys.iterator();
            if (iterator.hasNext()) {
                final CreationEntry entry = iterator.next();
                return entry;
            }
        }
        return toReturn;
    }
    
    public CreationEntry getCreationEntry(final int objectSource, final int objectTarget, final int objectCreated) throws NoSuchEntryException {
        CreationEntry toReturn = null;
        final Integer space = objectTarget;
        List<CreationEntry> entrys = CreationMatrix.matrix.get(space);
        if (entrys != null) {
            for (final CreationEntry entry : entrys) {
                if (((entry.getObjectSource() == objectSource && entry.getObjectTarget() == objectTarget) || (entry.getObjectSource() == objectTarget && entry.getObjectTarget() == objectSource)) && entry.getObjectCreated() == objectCreated) {
                    toReturn = entry;
                }
            }
        }
        if (toReturn == null) {
            entrys = CreationMatrix.matrix.get(objectSource);
            if (entrys != null) {
                for (final CreationEntry entry : entrys) {
                    if (((entry.getObjectSource() == objectSource && entry.getObjectTarget() == objectTarget) || (entry.getObjectSource() == objectTarget && entry.getObjectTarget() == objectSource)) && entry.getObjectCreated() == objectCreated) {
                        toReturn = entry;
                    }
                }
            }
        }
        if (toReturn == null) {
            throw new NoSuchEntryException("No creation entry found for objectSource=" + objectSource + ", objectTarget=" + objectTarget + ", objectCreated=" + objectCreated);
        }
        return toReturn;
    }
    
    static {
        CreationMatrix.matrix = new HashMap<Integer, List<CreationEntry>>();
        CreationMatrix.advancedEntries = new HashMap<Integer, List<CreationEntry>>();
        CreationMatrix.simpleEntries = new HashMap<Integer, List<CreationEntry>>();
    }
}
