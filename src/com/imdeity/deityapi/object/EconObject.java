package com.imdeity.deityapi.object;

import java.text.DecimalFormat;

import net.milkbowl.vault.economy.Economy;

import com.imdeity.deityapi.exception.NegativeMoneyException;

/**
 * Handles interaction with iConomy through vault
 * 
 * @author vanZeben
 */
public class EconObject {
    
    public Economy econ = null;
    
    public EconObject(Economy econ) {
        this.econ = econ;
    }
    
    /**
     * Checks to see if the sender can pay
     * 
     * @param sender
     *            Player to check
     * @param cost
     *            Amount to check against
     * @return boolean If the player can pay or not
     */
    public boolean canPay(String sender, double cost) {
        if (this.econ.getBalance(sender) >= cost) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Creates an iConomy Account
     * 
     * @param name
     *            Name to create account under
     */
    public void createAccount(String name) {
        this.econ.createPlayerAccount(name);
    }
    
    /**
     * Returns a balance of a back account
     * 
     * @param sender
     *            name of account to get
     * @return balance of the account
     */
    public double getBalance(String sender) {
        return this.econ.getBalance(sender);
    }
    
    /**
     * Gives a player money
     * 
     * @param receiver
     *            Player to give money to
     * @param cost
     *            Amount to give
     * @return boolean Whether or not the transaction was proformed
     * @throws NegativeMoneyException
     *             Thrown if the amount is negative
     */
    public boolean receive(String receiver, double cost, String note) throws NegativeMoneyException {
        if (cost < 0) { throw new NegativeMoneyException(); }
        
        this.econ.depositPlayer(receiver, cost);
        this.updateIconomyTable("Server", receiver, cost, note);
        return true;
    }
    
    /**
     * Remove an iConomy Account (really just setting balance to 0)
     * 
     * @param name
     *            Name of account to remove
     */
    public void removeAccount(String name) {
        this.econ.withdrawPlayer(name, this.econ.getBalance(name));
    }
    
    /**
     * Removed money from a player
     * 
     * @param sender
     *            Player to remove money from
     * @param cost
     *            Amount to remove
     * @return boolean Whether or not the transaction was performed
     * @throws NegativeMoneyException
     *             Thrown if the amount is negative
     */
    public boolean send(String sender, double cost, String note) throws NegativeMoneyException {
        if (cost < 0) { throw new NegativeMoneyException(); }
        
        this.econ.withdrawPlayer(sender, cost);
        this.updateIconomyTable(sender, "Server", cost, note);
        return true;
    }
    
    /**
     * Tansfers money from one player account to another
     * 
     * @param sender
     *            Player to remove money from
     * @param receiver
     *            Player to give money to
     * @param cost
     *            Amount to send
     * @return boolean Whether or not the transaction was proformed
     * @throws NegativeMoneyException
     *             Thrown if the amount is negative
     */
    public boolean sendTo(String sender, String receiver, double cost, String note) throws NegativeMoneyException {
        if (cost < 0) { throw new NegativeMoneyException(); }
        
        if (this.canPay(sender, cost)) {
            this.econ.withdrawPlayer(sender, cost);
            this.econ.depositPlayer(receiver, cost);
            this.updateIconomyTable(sender, receiver, cost, note);
            return true;
        } else {
            return false;
        }
        
    }
    
    /**
     * Keeps a log of all the transactions preformed
     * 
     * @param sender
     *            Who sent the money
     * @param receiver
     *            Who received the money
     * @param amount
     *            How much the receiver got
     */
    @SuppressWarnings("unused")
    private void updateIconomyTable(String sender, String receiver, double amount) {
        // String sql = "INSERT INTO `deity_transaction_log` " +
        // "(`sender_name`, `receiver_name`, `transaction_amount`) " +
        // "VALUES (?,?,?)";
        //
        // DeityAPI.getAPI().getDataAPI().getMySQL().write(sql, sender,
        // receiver, amount);
    }
    
    /**
     * Keeps a log of all the transactions preformed
     * 
     * @param sender
     *            Who sent the money
     * @param receiver
     *            Who received the money
     * @param amount
     *            How much the receiver got
     */
    private void updateIconomyTable(String sender, String receiver, double amount, String note) {
        // String sql = "INSERT INTO `deity_transaction_log` " +
        // "(`sender_name`, `receiver_name`, `transaction_amount`, `notes`) " +
        // "VALUES (?,?,?,?)";
        //
        // Deity.data.getDB().Write(sql, sender, receiver, amount, note);
    }
    
    /**
     * Returns a textual representation of a players balance
     * 
     * @param playername
     * @return
     */
    public String getFormattedBalance(String playername) {
        double amount = this.getBalance(playername);
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        
        if (amount == 1) {
            return formatter.format(amount) + " " + getCurrencyNameSingular();
        } else {
            return formatter.format(amount) + " " + getCurrencyNamePlural();
        }
    }
    
    /**
     * Sends a textual representation of a balance
     * 
     * @param playername
     * @return
     */
    public String getFormattedBalance(double amount) {
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        
        if (amount == 1) {
            return formatter.format(amount) + " " + getCurrencyNameSingular();
        } else {
            return formatter.format(amount) + " " + getCurrencyNamePlural();
        }
    }
    
    /**
     * Returns the current name in singular format
     * 
     * @return
     */
    public String getCurrencyNameSingular() {
        return this.econ.currencyNameSingular();
    }
    
    /**
     * Returns the currency name in plural format
     * 
     * @return
     */
    public String getCurrencyNamePlural() {
        return this.econ.currencyNamePlural();
    }
    
}
