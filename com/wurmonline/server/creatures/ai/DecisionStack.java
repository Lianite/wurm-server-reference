// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures.ai;

import java.util.logging.Level;
import java.util.LinkedList;
import java.util.logging.Logger;

public final class DecisionStack
{
    private static final Logger logger;
    private static final int MAX_ORDERS = 5;
    private final LinkedList<Order> orders;
    
    public DecisionStack() {
        this.orders = new LinkedList<Order>();
        if (DecisionStack.logger.isLoggable(Level.FINEST)) {
            DecisionStack.logger.finest("Created new DecisionStack");
        }
    }
    
    public void clearOrders() {
        this.orders.clear();
    }
    
    public boolean addOrder(final Order order) {
        if (this.mayReceiveOrders() && order != null) {
            this.orders.addLast(order);
            return true;
        }
        return false;
    }
    
    public boolean removeOrder(final Order order) {
        if (order != null) {
            return this.orders.remove(order);
        }
        DecisionStack.logger.warning("Tried to remove a null Order from " + this);
        return false;
    }
    
    public Order getFirst() {
        return this.orders.getFirst();
    }
    
    public boolean hasOrders() {
        return !this.orders.isEmpty();
    }
    
    public boolean mayReceiveOrders() {
        return this.orders.size() < 5;
    }
    
    int getNumberOfOrders() {
        return this.orders.size();
    }
    
    static {
        logger = Logger.getLogger(DecisionStack.class.getName());
    }
}
