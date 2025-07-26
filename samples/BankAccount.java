package samples;

/**
 * This one introduces:
 *
 * Basic state (instance variable: balance)
 *
 * Exceptions
 *
 * Multiple methods using double
 *
 * void return type
 */
public class BankAccount {

    private double balance = 0;

    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Deposit must be positive");
        balance += amount;
    }

    public void withdraw(double amount) {
        if (amount > balance) throw new IllegalArgumentException("Insufficient funds");
        balance -= amount;
    }

    public double getBalance() {
        return balance;
    }
}