// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import java.util.Arrays;
import java.util.Comparator;
import com.wurmonline.server.creatures.Delivery;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.Wagoner;
import java.util.logging.Logger;

public class WagonerHistory extends Question
{
    private static final Logger logger;
    private final Wagoner wagoner;
    private long deliveryId;
    private int sortBy;
    private int pageNo;
    private boolean inQueue;
    private boolean waitAccept;
    private boolean inProgress;
    private boolean delivered;
    private boolean rejected;
    private boolean cancelled;
    
    public WagonerHistory(final Creature aResponder, final Wagoner wagoner) {
        super(aResponder, "History of " + wagoner.getName(), "History of " + wagoner.getName(), 148, wagoner.getWurmId());
        this.deliveryId = -10L;
        this.sortBy = 1;
        this.pageNo = 1;
        this.inQueue = true;
        this.waitAccept = true;
        this.inProgress = true;
        this.delivered = true;
        this.rejected = true;
        this.cancelled = true;
        this.wagoner = wagoner;
    }
    
    public WagonerHistory(final Creature aResponder, final Wagoner wagoner, final long deliveryId, final int sortBy, final int pageNo, final boolean inQueue, final boolean waitAccept, final boolean inProgress, final boolean delivered, final boolean rejected, final boolean cancelled) {
        super(aResponder, "History of " + wagoner.getName(), "History of " + wagoner.getName(), 148, wagoner.getWurmId());
        this.deliveryId = -10L;
        this.sortBy = 1;
        this.pageNo = 1;
        this.inQueue = true;
        this.waitAccept = true;
        this.inProgress = true;
        this.delivered = true;
        this.rejected = true;
        this.cancelled = true;
        this.wagoner = wagoner;
        this.deliveryId = deliveryId;
        this.sortBy = sortBy;
        this.pageNo = pageNo;
        this.inQueue = inQueue;
        this.waitAccept = waitAccept;
        this.inProgress = inProgress;
        this.delivered = delivered;
        this.rejected = rejected;
        this.cancelled = cancelled;
    }
    
    @Override
    public void answer(final Properties answers) {
        this.setAnswer(answers);
        if (this.type == 0) {
            WagonerHistory.logger.log(Level.INFO, "Received answer for a question with NOQUESTION.");
            return;
        }
        if (this.type == 148) {
            final boolean close = this.getBooleanProp("close");
            if (close) {
                return;
            }
            final boolean filter = this.getBooleanProp("filter");
            if (filter) {
                this.inQueue = this.getBooleanProp("inqueue");
                this.waitAccept = this.getBooleanProp("waitaccept");
                this.inProgress = this.getBooleanProp("inprogress");
                this.delivered = this.getBooleanProp("delivered");
                this.rejected = this.getBooleanProp("rejected");
                this.cancelled = this.getBooleanProp("cancelled");
            }
            else {
                for (final String key : this.getAnswer().stringPropertyNames()) {
                    if (key.startsWith("sort")) {
                        final String sid = key.substring(4);
                        this.sortBy = Integer.parseInt(sid);
                        break;
                    }
                }
            }
            final WagonerHistory wh = new WagonerHistory(this.getResponder(), this.wagoner, this.deliveryId, this.sortBy, this.pageNo, this.inQueue, this.waitAccept, this.inProgress, this.delivered, this.rejected, this.cancelled);
            wh.sendQuestion();
        }
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getBmlHeaderWithScrollAndQuestion());
        buf.append("label{text=\"\"}");
        final Delivery[] deliveries = Delivery.getDeliveriesFor(this.target, this.inQueue, this.waitAccept, this.inProgress, this.rejected, this.delivered);
        final int absSortBy = Math.abs(this.sortBy);
        final int upDown = Integer.signum(this.sortBy);
        switch (absSortBy) {
            case 1: {
                Arrays.sort(deliveries, new Comparator<Delivery>() {
                    @Override
                    public int compare(final Delivery param1, final Delivery param2) {
                        return param1.getSenderName().compareTo(param2.getSenderName()) * upDown;
                    }
                });
                break;
            }
            case 2: {
                Arrays.sort(deliveries, new Comparator<Delivery>() {
                    @Override
                    public int compare(final Delivery param1, final Delivery param2) {
                        return param1.getReceiverName().compareTo(param2.getReceiverName()) * upDown;
                    }
                });
                break;
            }
            case 3: {
                Arrays.sort(deliveries, new Comparator<Delivery>() {
                    @Override
                    public int compare(final Delivery param1, final Delivery param2) {
                        return param1.getStateName().compareTo(param2.getStateName()) * upDown;
                    }
                });
                break;
            }
            case 4: {
                Arrays.sort(deliveries, new Comparator<Delivery>() {
                    @Override
                    public int compare(final Delivery param1, final Delivery param2) {
                        if (param1.getCrates() < param2.getCrates()) {
                            return 1 * upDown;
                        }
                        return -1 * upDown;
                    }
                });
                break;
            }
            case 5: {
                Arrays.sort(deliveries, new Comparator<Delivery>() {
                    @Override
                    public int compare(final Delivery param1, final Delivery param2) {
                        return param1.getSenderCostString().compareTo(param2.getSenderCostString()) * upDown;
                    }
                });
                break;
            }
            case 6: {
                Arrays.sort(deliveries, new Comparator<Delivery>() {
                    @Override
                    public int compare(final Delivery param1, final Delivery param2) {
                        return param1.getReceiverCostString().compareTo(param2.getReceiverCostString()) * upDown;
                    }
                });
                break;
            }
            case 7: {
                Arrays.sort(deliveries, new Comparator<Delivery>() {
                    @Override
                    public int compare(final Delivery param1, final Delivery param2) {
                        if (param1.getWhenDelivered() < param2.getWhenDelivered()) {
                            return 1 * upDown;
                        }
                        return -1 * upDown;
                    }
                });
                break;
            }
        }
        buf.append("table{rows=\"1\";cols=\"8\";label{text=\"\"};" + this.colHeader("Sender", 1, this.sortBy) + this.colHeader("Receiver", 2, this.sortBy) + this.colHeader("Delivery State", 3, this.sortBy) + this.colHeader("# Crates", 4, this.sortBy) + this.colHeader("Sender Costs", 5, this.sortBy) + this.colHeader("Receiver Costs", 6, this.sortBy) + this.colHeader("When Delivered", 7, this.sortBy));
        for (final Delivery delivery : deliveries) {
            buf.append("label{text=\"\"};label{text=\"" + delivery.getSenderName() + "\"};label{text=\"" + delivery.getReceiverName() + "\"};label{text=\"" + delivery.getStateName() + "\"};label{text=\"" + delivery.getCrates() + "\"};label{text=\"" + delivery.getSenderCostString() + "\"};label{text=\"" + delivery.getReceiverCostString() + "\"};label{text=\"" + delivery.getStringDelivered() + "\"};");
        }
        buf.append("}");
        buf.append("text{text=\"\"}");
        buf.append("harray{button{text=\"Filter\";id=\"filter\"};label{text=\" by \"}checkbox{id=\"waitaccept\";text=\"Waiting for accept  \"" + (this.waitAccept ? ";selected=\"true\"" : "") + "};checkbox{id=\"inqueue\";text=\"In queue  \"" + (this.inQueue ? ";selected=\"true\"" : "") + "};checkbox{id=\"inprogress\";text=\"In Progress  \"" + (this.inProgress ? ";selected=\"true\"" : "") + "};checkbox{id=\"delivered\";text=\"Delivered  \"" + (this.delivered ? ";selected=\"true\"" : "") + "};checkbox{id=\"rejected\";text=\"Rejected  \"" + (this.rejected ? ";selected=\"true\"" : "") + "};checkbox{id=\"cancelled\";text=\"Cancelled \"" + (this.cancelled ? ";selected=\"true\"" : "") + "};};");
        buf.append("}};null;null;}");
        this.getResponder().getCommunicator().sendBml(550, 500, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    static {
        logger = Logger.getLogger(WagonerHistory.class.getName());
    }
}
